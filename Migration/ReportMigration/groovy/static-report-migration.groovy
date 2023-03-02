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
