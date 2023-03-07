import com.ibm.dbb.metadata.MetadataStore;
import com.ibm.dbb.metadata.MetadataStoreFactory;
import com.ibm.dbb.metadata.BuildResult;
import com.ibm.dbb.metadata.BuildResult.QueryParms;
import com.ibm.dbb.EnvVars;
import com.ibm.dbb.build.internal.Utils;

import java.util.concurrent.TimeUnit;
import java.lang.Thread;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.TestClassOrder;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class StaticReportMigrationTests {
    private static final String GROUP = "Static-Report-Migration-Test";
    private static final String GROUP2 = "Static-Report-Migration-Test-2";
    private static final String LABEL = "buildresult";
    private static final String URL_KEY = "test-url";
    private static final String ID_KEY = "test-id";
    private static final String PW_FILE_KEY = "test-pwFile";

    private static File testDir = new File(StaticReportMigrationTests.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
    private static String listScript = new File(testDir, "../bin/create-migration-list.sh").getPath();
    private static String migrateScript = new File(testDir, "../bin/migrate-list.sh").getPath();
    private static String jsonFile = new File("list.json");

    private static String url;
    private static String id;
    private static File passwordFile;
    private static MetadataStore store;

    
    @Nested
    //@TestInstance(Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Order(1)
    class ListCreationTests {
        /*
         * Can be tested with jar-uf to replace version.properties in dbb.core-<>.jar
         * Non-functional test.
         */
        void testVersion() {
            System.out.println("Running version test.");
            File currVersion = new File(EnvVars.getHome() + "/bin/version.properties");
            File testVersion = new File(testDir, "../tests/samples/version.properties");
            File tempFile = new File("temp.properties");
            System.out.println(currVersion);
            try {
                tempFile.delete();
                assertTrue(currVersion.renameTo(tempFile), "Failed to move current version out.");
                Files.copy(testVersion.toPath(), currVersion.toPath());
                assertTrue(currVersion.exists(), "Failed to copy test version in.");
                System.out.println("Successflly Moved Files");
                String errorMessage = "DBB Version 1.1.4 is not compatable with this tool";
                List<String> command = new ArrayList<>();
                command.add(listScript);
                Map<String, String> output = runMigrationScript(command, 1);
                assertTrue(output.get("out").contains(errorMessage));

                command = new ArrayList<>();
                command.add(migrateScript);
                output = runMigrationScript(command, 1);
                assertTrue(output.get("out").contains(errorMessage));
            } finally {
                currVersion.delete();
                assertTrue(tempFile.renameTo(currVersion), "Failed to reset current version.");
            }
            
            List<String> command = new ArrayList<>();
            command.add(listScript);
        }

        void testWildcard() {
            System.out.println("Running wildcard tests.");
            List<String> command = new ArrayList<>();
            command.add(listScript);
            command.add("--url");
            command.add(url);
            command.add("--id");
            command.add(id);
            command.add("--pwFile");
            command.add(passwordFile.getPath());
            command.add("--grp");
            command.add(",*");

            Map<String, String> output = runMigrationScript(command, 0);
            Map<String, List<String>> expected = new HashMap<>();
            expected.put(GROUP, Arrays.asList(LABEL));
            expected.put(GROUP2, Arrays.asList(LABEL));
            validateMigrationList(jsonFile, expected);
        }

        void testWildcardSingleSegment() {
            System.out.println("Running wildcard tests.");
            List<String> command = new ArrayList<>();
            command.add(listScript);
            command.add("--url");
            command.add(url);
            command.add("--id");
            command.add(id);
            command.add("--pwFile");
            command.add(passwordFile.getPath());
            command.add("--grp");
            command.add("Static*");

            Map<String, String> output = runMigrationScript(command, 0);
            Map<String, List<String>> expected = new HashMap<>();
            expected.put(GROUP, Arrays.asList(LABEL));
            expected.put(GROUP2, Arrays.asList(LABEL));
            validateMigrationList(jsonFile, expected);
        }

        void testWildcardMultiSegment() {
            System.out.println("Running wildcard tests.");
            List<String> command = new ArrayList<>();
            command.add(listScript);
            command.add("--url");
            command.add(url);
            command.add("--id");
            command.add(id);
            command.add("--pwFile");
            command.add(passwordFile.getPath());
            command.add("--grp");
            command.add("*Static*Test");

            Map<String, String> output = runMigrationScript(command, 0);
            Map<String, List<String>> expected = new HashMap<>();
            expected.put(GROUP, Arrays.asList(LABEL));
            validateMigrationList(jsonFile, expected);
        }

        void testExactMatch() {
            System.out.println("Running wildcard tests.");
            List<String> command = new ArrayList<>();
            command.add(listScript);
            command.add("--url");
            command.add(url);
            command.add("--id");
            command.add(id);
            command.add("--pwFile");
            command.add(passwordFile.getPath());
            command.add("--grp");
            command.add("Static-Report-Migration-Test-2");
            
            Map<String, String> output = runMigrationScript(command, 0);
            Map<String, List<String>> expected = new HashMap<>();
            expected.put(GROUP2, Arrays.asList(LABEL));
            validateMigrationList(jsonFile, expected);
        }
    }

    @Nested
    //@TestInstance(Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Order(2)
    class IntegrationTests {

        @Test
        void migrationTest() {
            System.out.println("Running migration test.");
            List<String> command = new ArrayList<>();
            command.add(listScript);
            command.add("--url");
            command.add(url);
            command.add("--id");
            command.add(id);
            command.add("--pwFile");
            command.add(passwordFile.getPath());
            command.add("--grp");
            command.add(GROUP);
            //Map<String, String> output = runMigrationScript(command, 0);
            //assertTrue(output.get("err").trim().isEmpty(), String.format("Error stream is not empty\nOUT:\n%s\n\nERR:\n%s", output.get("out"), output.get("err")));
            //validateResults();
        }

        @Test
        void badPasswordFileTest() {
            System.out.println("Running bad password file test.");
            List<String> command = new ArrayList<>();
            command.add(listScript);
            command.add("--url");
            command.add(url);
            command.add("--id");
            command.add(id);
            command.add("--pwFile");
            command.add("~/nonexistantfile");
            command.add("--grp");
            command.add(GROUP);
            //Map<String, String> output = runMigrationScript(command, 1);
            //assertTrue(output.get("out").contains("There was an issue reading your password file"));
        }

        @Test
        void badPasswordTest() {
            System.out.println("Running bad password test.");
            List<String> command = new ArrayList<>();
            command.add(listScript);
            command.add("--url");
            command.add(url);
            command.add("--id");
            command.add(id);
            command.add("--pw");
            command.add("1eMBM6+tJspEoJiwJqfKqA==");
            command.add("--grp");
            command.add(GROUP);
            //Map<String, String> output = runMigrationScript(command, 1);
            //assertTrue(output.get("out").contains("There was an issue connecting to the Metadata Store"));
        }
    }

    @BeforeAll
    static void setupStore() throws Exception {
        System.out.println("Setting up store.");
        if (System.getProperties().containsKey(URL_KEY) == false) {
            fail(String.format("Missing URL system property '%s'.", URL_KEY));
        }
        if (System.getProperties().containsKey(ID_KEY) == false) {
            fail(String.format("Missing ID system property '%s'.", ID_KEY));
        }
        if (System.getProperties().containsKey(PW_FILE_KEY) == false) {
            fail(String.format("Missing Password File system property '%s'.", PW_FILE_KEY));
        }

        url = System.getProperty(URL_KEY);
        id = System.getProperty(ID_KEY);
        passwordFile = new File(System.getProperty(PW_FILE_KEY));

        store = MetadataStoreFactory.createDb2MetadataStore(url, id, passwordFile);
    
        setupCollection();
    }

    @AfterAll
    static void cleanupStore() {
        System.out.println("Cleaning up store.");
        //store.deleteBuildResults(GROUP);
        //store.deleteCollection(GROUP);
    }

    private static void setupCollection() throws Exception {
        System.out.println("Setting up collection.");
        store.deleteBuildResults(GROUP);
        store.deleteCollection(GROUP);

        // Create first collection
        store.createCollection(GROUP);
        BuildResult newResult = store.createBuildResult(GROUP, LABEL);
        newResult.setState(BuildResult.COMPLETE);

        String samplesFolder = "samples/";
        // Report data is labled with the version used to create it, in case of differences between versions
        newResult.setBuildReportData(new FileInputStream(new File(testDir, samplesFolder + "result-data-2.0.0.json")));
        newResult.setBuildReport(new FileInputStream(new File(testDir, samplesFolder + "report.html")));

        // Create second collection
        store.createCollection(GROUP2);
        newResult = store.createBuildResult(GROUP2, LABEL);
        newResult.setState(BuildResult.COMPLETE);

        newResult.setBuildReportData(new FileInputStream(new File(testDir, samplesFolder + "result-data-2.0.0.json")));
        newResult.setBuildReport(new FileInputStream(new File(testDir, samplesFolder  + "report.html")));

        // Assert file content
        System.out.println("Asserting test file content.");
        String htmlString = '{"date":"28-Feb-2022 17:26:26","build":"151","id":"DBB API Version","type":"VERSION","version":"1.1.3"}';
        String dataString = '{"date":"06-Dec-2022 17:13:58","build":"113","id":"DBB API Version","type":"VERSION","version":"2.0.0"}';
        List<BuildResult> results = store.getBuildResults(Collections.singletonMap(QueryParms.GROUP, GROUP));
        for (BuildResult result : results) {
            assertEquals(GROUP, result.getGroup());
            assertEquals(LABEL, result.getLabel());
            assertTrue(Utils.readFromStream(result.getBuildReport().getContent(), "UTF-8").contains(htmlString));
            assertTrue(Utils.readFromStream(result.getBuildReportData().getContent(), "UTF-8").contains(dataString));
        }
        assertTrue(results.size() == 1);
    }

    private void validateMigrationList(File jsonFile, Map<String, List<String>> expected) {
        JSONObject json;
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
            json = JSONObject.parse(reader);
        }

        expected.forEach((key, value) -> {
            assertTrue(json.containsKey(key));
            assertIterableEquals(value, json.get(key));
        });
    }

    private void validateResults() {
        System.out.println("Validating results.");
        for (BuildResult result : store.getBuildResults(Collections.singletonMap(QueryParms.GROUP, GROUP))) {
            String content = Utils.readFromStream(result.getBuildReport().getContent(), "UTF-8");
            assertTrue(content.contains('<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-us" lang="en-us" style="scroll-behavior: smooth;">'), String.format("Result data '%s:%s' not readable, bad encoding likely.", result.getGroup(), result.getLabel()));
            assertFalse(content.contains("</script>"), String.format("Result '%s:%s' not converted.", result.getGroup(), result.getLabel()));
        }
    }

    private Map<String, String> runMigrationScript(String command, int expectedRC) throws IOException, InterruptedException {
        return runMigrationScript(Arrays.asList(command.split(" ")));
    }

    private Map<String, String> runMigrationScript(List<String> command, int expectedRC) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.environment().put("DBB_HOME", EnvVars.getHome());
        
        Process process = processBuilder.start();
        long startTime = System.currentTimeMillis();
        long maxTime = 3 * 60 * 1000; // Minutes (3) -> MS
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedWriter stdOut = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        char[] buffer = new char[16*1024];

        StringBuilder output = new StringBuilder();
        while (System.currentTimeMillis() - startTime < maxTime) {
            int charsRead = stdInput.read(buffer);
            if (charsRead == -1) break;
            
            String newString = new String(buffer, 0, charsRead);
            output.append(newString);
            if (newString.toLowerCase().contains("('y' or 'n')")) {
                stdOut.write("y");
                stdOut.newLine();
                stdOut.flush();
            }
            
            Thread.sleep(1000);
        }

        StringBuilder error = new StringBuilder();
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = stdError.readLine()) != null) {
            error.append(line);
            error.append("\n");
        }
        process.destroy();
        
        int rc = process.exitValue();
        String errorString = error.toString();
        String outputString = output.toString();

        assertEquals(expectedRC, rc, String.format("Script return code is not equal to %s\nOUT:\n%s\n\nERR:\n%s", expectedRC, outputString, errorString));
        
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("rc", rc);
        returnMap.put("out", outputString);
        returnMap.put("err", errorString);
        return returnMap;
    }

    
}