@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript

import groovy.transform.Field;
import groovy.cli.commons.CliBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Option;
import groovy.cli.commons.OptionAccessor;
import java.util.stream.Collectors;
import java.util.function.Function;

@Field def versionUtils = loadScript(new File("check-version.groovy"));
@Field boolean debug = false;

String leastAcceptableVersion = "2.0.0";
String mostAcceptableVersion = "3.0.0";
// Check DBB Version
String errorMessage;
if ((errorMessage = versionUtils.checkVersion(leastAcceptableVersion, mostAcceptableVersion)) != null) {
    println(errorMessage);
    System.exit(1);
}

// Main execution block
try {
    // Load connection API script
    def connectionScript = loadScript(new File("connection-2.x.groovy"));

    // Process CLI Arguments
    OptionAccessor options = getOptions(args);
    if (options.debug) {
        this.debug = true;
        connectionScript.setDebug(true);
    }

    File jsonFile = options.arguments()[0] as File;
    if (jsonFile.getParentFile().exists() == false) {
        jsonFile.getParentFile().mkdirs();
    }

    // Instantiate MetadataStore
    if (options.props) {
        Properties props = new Properties();
        props.load(options.props);
        // Override url if its passed in
        if (options.url) {
            props.setProperty("url", options.url);
        }
        if (options.pw) {
            connectionScript.setStore(options.id, options.pw, props);
        } else {
            connectionScript.setStore(options.id, options.pwFile as File, props);
        }
    } else {
        if (options.pw) {
            connectionScript.setStore(options.url, options.id, options.pw);
        } else {
            connectionScript.setStore(options.url, options.id, options.pwFile as File);
        }
    }
    // Collect groups
    List<String> groups = collectGroups(options.grps ?: null, options.grpf ? options.grpf as File : null);
    List<String> resultGroups = connectionScript.getBuildResultGroups();
    groups = matchGroups(resultGroups, groups);
    if (groups.size() == 0) {
        println("No groups matched.");
        System.exit(0);
    }
    
    def results = connectionScript.getBuildResults(groups);
    if (results.size() == 0) {
        println("No non-static build reports found.");
        System.exit(0);
    }

    def json = connectionScript.getJSONObject();
    def list = connectionScript.getJSONArray(); // Assign to preload its datatype
    for (def result : results) {
        if (json.containsKey(result.getGroup())) {
            list = json.get(result.getGroup());
            list.add(result.getLabel());
        } else {
            list = connectionScript.getJSONArray();
            list.add(result.getLabel());
            json.put(result.getGroup(), list);
        }
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))) {
        writer.write(json.serialize(true));
    }
    
} catch (Exception error) {
    throw error;
    println(error.getMessage());
    System.exit(1);
}

private OptionAccessor getOptions(String[] args) {
    String usage = "create-migration-list.sh <json-file> [options] [--help]";
    String header = "Using DBB version ${versionUtils.getVersion()}";
    CliBuilder parser = new CliBuilder(usage:usage, header:header, stopAtNonOption:false);

    parser.id(type:String, longOpt:'id', args:1, required:true, 'Db2 Metadata Store user id.');

    // One required but not mutually exclusive. URL can overwrite props "url" property.
    parser.url(type:String, longOpt:'url', args:1, 'Db2 Metadata Store URL. Example: jdbc:db2:<Db2 server location>');
    parser.props(type:File, longOpt:'properties', args:1, 'Db2 Metadata Store connection properties.');

    // Mutually exclusive groups
    // Groups do not support the type argument, so they must be cast to the proper type.
    OptionGroup passwordGroup = new OptionGroup();
    passwordGroup.setRequired(true);
    passwordGroup.addOption(parser.option("pw", [type:String, longOpt:"pw", args:1], 'Encrypted Db2 Metadata Store password.'));
    passwordGroup.addOption(parser.option("pwFile", [type:File, longOpt:'pwFile', args:1], 'Db2 Metadata Store password file.'));
    parser.options.addOptionGroup(passwordGroup);
    
    // Group for groups
    OptionGroup groupGroup = new OptionGroup();
    groupGroup.setRequired(true);
    groupGroup.addOption(parser.option("grp", [longOpt:"grp", args:Option.UNLIMITED_VALUES, valueSeparator:','], "A comma seperated list of groups."));
    groupGroup.addOption(parser.option("grpf", [type:File, longOpt:"grpf", args:1], "A file containing groups seperated by new lines."));
    parser.options.addOptionGroup(groupGroup);

    parser.debug(longOpt:"debug", 'Prints entries that are skipped.');
    parser.help(longOpt:"help", 'Prints this message.');
    
    OptionAccessor options = parser.parse(args);
    if (options == null) System.exit(1);
    if (!options.url && !options.props) {
        println("error: Connection properties, 'url' or 'props', must be specified.");
        parser.usage();
        System.exit(1);
    }
    if (options.arguments() == null) {
        println("error: Positional argument, 'json-file', must be specified.");
        parser.usage();
        System.exit(1);
    }
    if ((options.arguments()[0] as File).isFile() == false) {
        println("error: Positional argument, 'json-file', must be a valid file path.");
        parser.usage();
        System.exit(1);
    }
    if (options.help) {
        parser.usage();
        System.exit(0);
    }
    return options;
}

private List<String> collectGroups(List<String> groupsArg, File groupsFileArg) {
    List<String> groups = new ArrayList<>();
    // Pull groups out of argument list and file argument
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

    return groups;
}

private List<String> matchGroups(List<String> resultGroups, List<String> groups) {
    // Sort group list from longest to shortest to match the most specific entries first
    groups.sort(Comparator.comparingInt(String::length));

    // Return the entire set if '*' is passed in as a group
    List<String> matchedGroups = new ArrayList<>();
    if (groups.contains("*")) {
        matchedGroups.addAll(resultGroups);
        return matchedGroups;
    }

    for (String group : groups) {
        if (group.contains("*")) {
            // Transform to ArrayList to add .remove() functionality
            List<String> matchItems = new ArrayList<>(Arrays.asList(group.split("\\*")));
            
            // Creates a map where the key is the group name, and the value is the text left to match
            Map<String, String> partialMatches = resultGroups.stream().filter(match -> {
                return group.startsWith("*") ? match.contains(matchItems.get(0)) : match.startsWith(matchItems.get(0));
            }).collect(Collectors.toMap(Function.identity(), match -> { // Remove matched string from value
                return match.substring(match.indexOf(matchItems.get(0)) + matchItems.get(0).length());
            }));
            matchItems.remove(0);

            // Iterate over the items left to match
            for (String matchText : matchItems) {
                // Same functions as the stream used to make the initial set, but different types and no handling for initial wildcard (*)
                partialMatches = partialMatches.entrySet().stream().filter(entry -> {
                    return entry.getValue().contains(matchText);
                }).collect(Collectors.toMap(Map.Entry::getKey, entry -> { // Remove matched string from value
                    return entry.getValue().substring(entry.getValue().indexOf(matchText) + matchText.length());
                }));
            }

            // Remove items with remaining text to match if there is not a wildcard (*) at the end
            if (group.endsWith("*") == false) {
                partialMatches = partialMatches.entrySet().stream().filter(entry -> entry.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
            // Add to the matched groups list
            partialMatches.keySet().forEach(key -> {
                resultGroups.remove(key);
                matchedGroups.add(key);
            });

            if (partialMatches.isEmpty() && this.debug) {
                println("Group '$group' did not match any stored result groups.");
            }

        } else {
            if (resultGroups.contains(group)) {
                // Remove from result groups to avoid duplicates
                resultGroups.remove(group);
                matchedGroups.add(group);
            } else if (this.debug) {
                println("Group '$group' did not match any stored result groups.");
            }
        }
    }
    
    return matchedGroups
}