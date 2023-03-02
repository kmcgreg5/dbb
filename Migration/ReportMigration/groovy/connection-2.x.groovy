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

import groovy.transform.Field;
import java.nio.file.Path;
import java.nio.file.Files;
import groovy.cli.commons.CliBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Option;

@Field MetadataStore client = null;
@Field List<String> groups = new ArrayList<>();
@Field boolean debug = false;

/****************************
**  Argument Parsing       **
*****************************/

boolean parseArgsInstantiate(String[] args) {
    String usage = "static-report-migration.sh [options] [--help]";
    String header = "Using DBB version ${VersionInfo.getInstance().getVersion()}";
    CliBuilder parser = new CliBuilder(usage:usage, header:header);

    parser.id(type:String, longOpt:'id', args:1, required:true, 'Db2 Metadata Store user id.');
    // Mutually exclusive groups
    // Groups do not support the type argument, so they must be cast to the proper type.
    OptionGroup passwordGroup = new OptionGroup();
    passwordGroup.setRequired(true);
    passwordGroup.addOption(parser.option("pw", [type:String, longOpt:"pw", args:1], 'Encrypted Db2 Metadata Store password.'))
    passwordGroup.addOption(parser.option("pwFile", [type:File, longOpt:'pwFile', args:1], 'Db2 Metadata Store password file.'))

    // Group 2
    OptionGroup groupGroup = new OptionGroup();
    groupGroup.setRequired(true);
    groupGroup.addOption(parser.option("grp", [longOpt:"grp", args:Option.UNLIMITED_VALUES, valueSeparator:','], "A comma seperated list of groups."));
    groupGroup.addOption(parser.option("grpf", [type:File, longOpt:"grpf", args:1], "A file containing groups seperated by new lines."));

    // One required but not mutually exclusive. URL can overwrite props "url" property.
    parser.url(type:String, longOpt:'url', args:1, 'Db2 Metadata Store URL. Example: jdbc:db2:<Db2 server location>');
    parser.props(type:File, longOpt:'properties', args:1, 'Db2 Metadata Store connection properties.');

    parser.help(longOpt:"help", 'Prints this message.');
    parser.debug(longOpt:"debug", 'Prints entries that are skipped.');
    
    // Should not display any output, just used to validate positional arguments
    parser.options.addOptionGroup(passwordGroup)
    parser.options.addOptionGroup(groupGroup)
    def options = parser.parse(args);
    if (options == null) return false;
    if (!options.url && !options.props) {
        println("error: Connection properties, 'url' or 'props', must be specified.");
        parser.usage();
        return false;
    }
    if (options.help) {
        parser.usage();
        return false;
    }

    // Options passed validation
    if (options.debug) debug = true;

    if (options.props) {
        Properties props = new Properties();
        props.load(options.props);
        // Override url if its passed in
        if (options.url) {
            props.setProperty("url", options.url);
        }
        if (options.pw) {
            setClient(options.id, options.pw, props);
        } else {
            setClient(options.id, options.pwFile as File, props);
        }
    } else {
        if (options.pw) {
            setClient(options.url, options.id, options.pw);
        } else {
            setClient(options.url, options.id, options.pwFile as File);
        }
    }

    setGroups(options.grps ?: null, options.grpf ? options.grpf as File : null);

    return true;
}

void setGroups(List<String> groupsArg, File groupsFileArg) {
    // Parses from both items
    if (groupsArg != null) {
        for (String group : groupsArg) {
            groups.add(group.trim());
        }
    }

    if (groupsFileArg != null) {
        groupsFileArg.eachLine { group ->
            group = group.trim();
            if (group.isEmpty()) return;
            // Remove trailing comma in case a CSV is passed in.
            if (group.endsWith(",")) group = group.substring(0, group.length()-1);
            if (groups.contains(group) == false) {
                groups.add(group);
            }
        }
    }
}

/****************************
**  Client Instantiation   **
*****************************/

// Db2 Metadata Store instantiation
private void setClient(String url, String id, String password) {
    client = exceptionClosure {MetadataStoreFactory.createDb2MetadataStore(url, id, password) };
}

private void setClient(String url, String id, File passwordFile) {
    client = exceptionClosure {MetadataStoreFactory.createDb2MetadataStore(url, id, passwordFile) };
}

private void setClient(String id, String password, Properties properties) {
    client = exceptionClosure {MetadataStoreFactory.createDb2MetadataStore(url, id, properties) };
}

private void setClient(String id, File passwordFile, Properties properties) {
    client = exceptionClosure {MetadataStoreFactory.createDb2MetadataStore(url, passwordFile, properties) };
}

/****************************
**  Command Execution      **
*****************************/

public void enableFileTagging() {
    exceptionClosure {
        BuildProperties.setProperty(Utils.FILE_TAGGING_OPTION_NAME, "true");
    }
}



public List<BuildResult> getBuildResults() {
    return exceptionClosure {
        List<BuildResult> results = retrieveBuildResults();
        filterBuildResults(results);
        return results;
    }
}



public void convertBuildReports(List<BuildResult> results) {
    exceptionClosure {
        for (BuildResult result : results) {
            Path html = Files.createTempFile("dbb-report-mig", ".html");
            
            BuildReport report = BuildReport.parse(result.getBuildReportData().getContent());
            report.generateHTML(html.toFile());
            result.setBuildReport(new FileInputStream(html.toFile()));
            
            System.out.println("${result.getGroup()}:${result.getLabel()} converted.");
            Files.delete(html);
        }
    }
}

private List<BuildResult> retrieveBuildResults() {
    // Multiple requests to avoid excess memory usage by returning all and then filtering
    List<BuildResult> results = new ArrayList<>();
    for (String group : groups) {
        results.addAll( exceptionClosure {client.getBuildResults(Collections.singletonMap(QueryParms.GROUP, group)) });
    }
    return results;
}

private void filterBuildResults(List<BuildResult> results) {
    results.removeIf(result-> { // IO, Build
        String content = exceptionClosure {Utils.readFromStream(result.getBuildReport().getContent(), "UTF-8") };
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


/****************************
**  Utilities              **
*****************************/

private def exceptionClosure(Closure closure) {
    try {
        closure()
    } catch (BuildException error) {
        String message;
        Throwable root = getRootCause(error);
        if (root instanceof IllegalArgumentException && root.getMessage().contains("passwordFile")) {
            message = String.format("There was an issue reading your password file: '%s'", root.getMessage());
        } else {
            message = String.format("There was an issue connecting to the Metadata Store: '%s'", error.getMessage());
        }
        
        throw new Exception(message);
    } catch (IOException error) {
        // Unexplained because exceptions have no one discernable source/cause
        String message = String.format("There was an unexpected exception: '%s'", error.getMessage());
        throw new Exception(message);
    }
}

private Throwable getRootCause(Throwable rootCause) {
    while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
        rootCause = rootCause.getCause();
    }
    return rootCause;
}