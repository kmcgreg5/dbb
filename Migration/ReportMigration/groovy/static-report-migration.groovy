@groovy.transform.BaseScript com.ibm.dbb.groovy.ScriptLoader baseScript

import groovy.transform.Field;

@Field def versionUtils = loadScript(new File("check-version.groovy"));
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
    def connectionScript = loadScript(new File("connection-2.x.groovy"));

    // Parse arguments and instantiate client
    if (!connectionScript.parseArgsInstantiate(args)) {
        System.exit(1);
    }

    // Ensure tagging on generated html files
    connectionScript.enableFileTagging();
    // consolidate, 
    def results = connectionScript.getBuildResults();

    if (results.size() == 0) {
        println("No non-static build reports found.")
    } else {
        println("You are about to convert ${results.size()} reports. Would you like to proceed ('y' or 'n'): ")
        // Works where there is no Console instance
        String response = System.in.newReader().readLine().trim().toLowerCase();
        if (response.equals("y") || response.equals("yes")) {
            connectionScript.convertBuildReports(results);
            println("Finished conversion.");
        } else {
            println("Conversion skipped.");
        }
    }
} catch (Exception error) {
    println(error.getMessage());
    System.exit(1);
}

public boolean parseArgsInstantiate(String[] args) {
    String usage = "static-report-migration.sh [options] [--help]";
    String header = "Using DBB version ${versionUtils.getVersion()}";
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
