@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript

import com.ibm.dbb.metadata.MetadataStoreFactory;
import com.ibm.dbb.metadata.MetadataStore;
import com.ibm.dbb.metadata.BuildResult;
import com.ibm.dbb.metadata.BuildResult.QueryParms;
import com.ibm.dbb.build.report.BuildReport;
import com.ibm.dbb.build.BuildProperties;
import com.ibm.dbb.build.internal.Utils;
import com.ibm.dbb.metadata.Attachment;
import com.ibm.dbb.build.VersionInfo;

import groovy.transform.Field;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.stream.Collectors;

@Field MetadataStore store = null;
@Field boolean debug = false;

/****************************
**  Store Instantiation    **
*****************************/

/**
 * Instantiates a DB2 Metadata Store, passing through the input arguments to the proper factory method.
 * 
 * @param url       The url of the db2 store instance.
 * @param id        The user id for the db2 store instance.
 * @param password  The encrypted password for the db2 store instance.
 */
public void setStore(String url, String id, String password) {
    store = MetadataStoreFactory.createDb2MetadataStore(url, id, password);
}

/**
 * Instantiates a Db2 Metadata Store.
 * {@link #setStore(String, String, String)}
 * 
 * @param url           The url of the db2 store instance.
 * @param id            The user id for the db2 store instance.
 * @param passwordFile  The password file for the db2 store instance.
 */
public void setStore(String url, String id, File passwordFile) {
    store = MetadataStoreFactory.createDb2MetadataStore(url, id, passwordFile);
}

/**
 * Instantiates a Db2 Metadata Store.
 * {@link #setStore(String, String, String)}
 * 
 * @param id            The user id for the db2 store instance.
 * @param password      The encrypted password for the db2 store instance.
 * @param properties    A properties object containing connection information for the db2 store instance.
 */
public void setStore(String id, String password, Properties properties) {
    store = MetadataStoreFactory.createDb2MetadataStore(url, id, properties);
}

/**
 * Instantiates a Db2 Metadata Store.
 * {@link #setStore(String, String, String)}
 * 
 * @param id            The url of the db2 store instance.
 * @param passwordFile  The password file for the db2 store instance.
 * @param properties    A properties object containing connection information for the db2 store instance.
 */
public void setStore(String id, File passwordFile, Properties properties) {
    store = MetadataStoreFactory.createDb2MetadataStore(url, passwordFile, properties);
}

/****************************
**  Command Execution      **
*****************************/

/**
 * Enables file tagging to ensure proper saving and parsing of the generated temporary report files.
 */
public void enableFileTagging() {
    BuildProperties.setProperty(Utils.FILE_TAGGING_OPTION_NAME, "true");
}


/**
 * Retrieves build results for a list of groups and removes items that are missing a build report or a '</script>' tag in their html content.
 * 
 * @param groups    The groups to retrieve build results for.
 * @return          A list of non-static build results.
 */
public List<BuildResult> getNonStaticBuildResults(List<String> groups) {
    List<BuildResult> results = retrieveBuildResults(groups);
    filterBuildResults(results);
    return results;
}

/**
 * Retrieves build results from a group that match the input labels.
 * 
 * @param group     The group to retrieve build results for.
 * @param labels    The build results to retrieve.
 * @return          A list of build results.
 */
public List<BuildResult> getBuildResultsFromGroup(String group, List<String> labels) {
    List<BuildResult> results = retrieveBuildResults(group);
    results.removeIf(result -> {
        return !labels.contains(result.getLabel());
    });
    return results;
}

/**
 * Regenerates the HTML for the input build results.
 * 
 * @param results   A list of build results to regenerate HTML for.
 */
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

/**
 * Returns all of the build result groups from the db2 store instance.
 */
public List<String> getBuildResultGroups() {
    return store.listBuildResultGroups();
}

/**
 * Retrieves and collects a list of build results from multiple groups.
 * {@link #retrieveBuildResults(String)}
 * @param groups    The list of groups to retrieve results for.
 * @return          The collected list of build results.
 */
private List<BuildResult> retrieveBuildResults(List<String> groups) {
    // Multiple requests to avoid excess memory usage by returning all and then filtering
    List<BuildResult> results = new ArrayList<>();
    for (String group : groups) {
        results.addAll(retrieveBuildResults(group));
    }
    return results;
}

/**
 * Retrieves build results from a single group.
 * 
 * @param group     The group to retrieve results for.
 * @return          The collected list of build results.
 */
private List<BuildResult> retrieveBuildResults(String group) {
    return store.getBuildResults(Collections.singletonMap(QueryParms.GROUP, group));
}

/**
 * Filters out build results inplace that are missing report content, or a '</script>' tag from their HTML.
 * This is to create a build result list only including non-static Build Reports.
 * 
 * @param results   The results to filter.
 */
private void filterBuildResults(List<BuildResult> results) {
    results.removeIf(result-> { // IO, Build
        Attachment buildReport = result.getBuildReport();
        if (buildReport == null) {
            if (debug) {
                System.out.println(String.format("Result '%s:%s' has no report... Skipping.", result.getGroup(), result.getLabel()));
            }
            return true;
        }

        String content = Utils.readFromStream(buildReport.getContent(), "UTF-8");
        if (content == null) {
            if (debug) {
                System.out.println(String.format("Result '%s:%s' report has no content... Skipping.", result.getGroup(), result.getLabel()));
            }
            return true;
        } else if (content.contains("</script>") == false) {
            if (debug) {
                System.out.println(String.format("Result '%s:%s' report has no script tag... Skipping.", result.getGroup(), result.getLabel()));
            }
            return true;
        }
        return false;
    });
}


/****************************
**  Utilities              **
*****************************/

/**
 * Sets the debug state of this script.
 * 
 * @param on    The desired debug state.
 */
public void setDebug(boolean on) {
    this.debug = on;
}

/**
 * Creates a json formatted migration list with the input arguments.
 * 
 * @param jsonFile  The location to create the migration list at.
 * @param results   The results to include in the list.
 */
public void createMigrationList(File jsonFile, List<BuildResult> results) {
    boolean versionIsTwo = VersionInfo.getInstance().getVersion() == "2.0.0";
    // Create JSON Object
    def json;
    if (versionIsTwo) {
        json = Class.forName("com.ibm.json.java.JSONObject").newInstance();
    } else {
        json = Class.forName("com.google.gson.JsonObject").newInstance();
    }

    for (BuildResult result : results) {
        boolean containsKey;
        if (versionIsTwo) {
            containsKey = json.containsKey(result.getGroup());
        } else {
            containsKey = json.has(result.getGroup());
        }

        if (containsKey) {
            def list;
            if (versionIsTwo) {
                list = json.get(result.getGroup());
            } else {
                list = json.getAsJsonArray(result.getGroup());
            }
            list.add(result.getLabel());
        } else {
            def list;
            if (versionIsTwo) {
                list = Class.forName("com.ibm.json.java.JSONArray").newInstance();
            } else {
                list = Class.forName("com.google.gson.JsonArray").newInstance();
            }
            list.add(result.getLabel());
            if (versionIsTwo) {
                json.put(result.getGroup(), list);
            } else {
                json.add(result.getGroup(), list);
            }
            
        }
    }
    // Write JSON to file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))) {
        if (versionIsTwo) {
            writer.write(json.serialize(true));
        } else {
            def gson = Class.forName("com.google.gson.GsonBuilder").newInstance().setPrettyPrinting().create();
            gson.toJson(json, writer);
        }
    }
}

/**
 * Returns a Map reflecting the state of the input migration list.
 * 
 * @param jsonFile  The migration list to read.
 * @return          A Map containing the info from the input json file.
 */
public Map<String, List<String>> readMigrationList(File jsonFile) {
    boolean versionIsTwo = VersionInfo.getInstance().getVersion() == "2.0.0";
    def json;
    try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
        if (versionIsTwo) {
            json = Class.forName("com.ibm.json.java.JSONObject").parse(reader);
        } else {
            def gson = Class.forName("com.google.gson.GsonBuilder").newInstance().setPrettyPrinting().create();
            json = gson.fromJson(reader, Class.forName("com.google.gson.JsonObject"));
        }
    }

    Map<String, List<String>> list;
    if (versionIsTwo) {
        list = json.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    } else {
        list = new HashMap<>();
        json.keySet().stream().forEach(key -> {
            System.out.println(key);
            List<String> jsonList = new ArrayList();
            json.getAsJsonArray(key).forEach(value -> {
                System.out.println(value);
                jsonList.add(value.getAsString());
            });
            list.put(key, jsonList);
        });
    }
    return list;
}
