@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript

import com.ibm.dbb.metadata.MetadataStoreFactory;
import com.ibm.dbb.metadata.MetadataStore;
import com.ibm.dbb.metadata.BuildResult;
import com.ibm.dbb.metadata.BuildResult.QueryParms;
import com.ibm.dbb.build.report.BuildReport;
import com.ibm.dbb.build.BuildProperties;
import com.ibm.dbb.build.internal.Utils;
import com.ibm.dbb.build.BuildException;
import com.ibm.dbb.build.VersionInfo;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import groovy.transform.Field;
import java.nio.file.Path;
import java.nio.file.Files;
import groovy.cli.commons.CliBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Option;
import java.util.stream.Collectors;

@Field MetadataStore store = null;
@Field boolean debug = false;

/****************************
**  Store Instantiation    **
*****************************/

// Db2 Metadata Store instantiation
public void setStore(String url, String id, String password) {
    store = MetadataStoreFactory.createDb2MetadataStore(url, id, password);
}

public void setStore(String url, String id, File passwordFile) {
    store = MetadataStoreFactory.createDb2MetadataStore(url, id, passwordFile);
}

public void setStore(String id, String password, Properties properties) {
    store = MetadataStoreFactory.createDb2MetadataStore(url, id, properties);
}

public void setStore(String id, File passwordFile, Properties properties) {
    store = MetadataStoreFactory.createDb2MetadataStore(url, passwordFile, properties);
}

/****************************
**  Command Execution      **
*****************************/

public void enableFileTagging() {
    BuildProperties.setProperty(Utils.FILE_TAGGING_OPTION_NAME, "true");
}



public List<BuildResult> getNonStaticBuildResults(List<String> groups) {
    List<BuildResult> results = retrieveBuildResults(groups);
    filterBuildResults(results);
    return results;
}


public List<BuildResult> getBuildResultsFromGroup(String group, List<String> labels) {
    List<BuildResult> results = retrieveBuildResults(group);
    results.removeIf(result -> {
        return !labels.contains(result.getLabel());
    });
    return results;
}


public void convertBuildReports(List<BuildResult> results) {
    for (BuildResult result : results) {
        Path html = Files.createTempFile("dbb-report-mig", ".html");
        
        BuildReport report = BuildReport.parse(result.getBuildReportData().getContent());
        report.generateHTML(html.toFile());
        result.setBuildReport(new FileInputStream(html.toFile()));
        
        System.out.println("${result.getGroup()}:${result.getLabel()} converted.");
        Files.delete(html);
    }
}

public List<String> getBuildResultGroups() {
    return store.listBuildResultGroups();
}

private List<BuildResult> retrieveBuildResults(List<String> groups) {
    // Multiple requests to avoid excess memory usage by returning all and then filtering
    List<BuildResult> results = new ArrayList<>();
    for (String group : groups) {
        results.addAll(retrieveBuildResults(group));
    }
    return results;
}

private List<BuildResult> retrieveBuildResults(String group) {
    return store.getBuildResults(Collections.singletonMap(QueryParms.GROUP, group));
}

private void filterBuildResults(List<BuildResult> results) {
    results.removeIf(result-> { // IO, Build
        String content = Utils.readFromStream(result.getBuildReport().getContent(), "UTF-8");
        if (content == null) {
            if (debug) {
                System.out.println(String.format("Result '%s:%s' has no content... Skipping.", result.getGroup(), result.getLabel()));
            }
            return true;
        } else if (content.contains("</script>") == false) {
            if (debug) {
                System.out.println(String.format("Result '%s:%s' has no script tag... Skipping.", result.getGroup(), result.getLabel()));
            }
            return true;
        }
        return false;
    });
}

public void createMigrationList(File jsonFile, List<BuildResult> results) {
    // Create JSON Object
    JSONObject json = new JSONObject();
    for (BuildResult result : results) {
        if (json.containsKey(result.getGroup())) {
            JSONArray list = json.get(result.getGroup());
            list.add(result.getLabel());
        } else {
            JSONArray list = new JSONArray();
            list.add(result.getLabel());
            json.put(result.getGroup(), list);
        }
    }
    // Write JSON to file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))) {
        writer.write(json.serialize(true));
    }
}

public Map<String, List<String>> readMigrationList(File jsonFile) {
    JSONObject json;
    try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
        json = JSONObject.parse(reader);
    }

    Map<String, List<String>> list = json.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return list;
}
/****************************
**  Utilities              **
*****************************/

public void setDebug(boolean on) {
    this.debug = on;
}
