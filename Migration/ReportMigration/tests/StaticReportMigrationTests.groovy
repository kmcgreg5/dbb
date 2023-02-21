import com.ibm.dbb.metadata.MetadataStore;
import com.ibm.dbb.metadata.MetadataStoreFactory;
import com.ibm.dbb.metadata.BuildResult;
import com.ibm.dbb.metadata.BuildResult.QueryParms;
import com.ibm.dbb.EnvVars;
import com.ibm.dbb.build.internal.Utils;

import java.util.concurrent.TimeUnit;
import java.lang.Thread;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StaticReportMigrationTests {
    private static final String GROUP = "Static-Report-Migration-Test";
    private static final String LABEL = "buildresult";
    private static final String URL_KEY = "test-url";
    private static final String ID_KEY = "test-id";
    private static final String PW_FILE_KEY = "test-pwFile";

    private File testDir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();

    private static String url;
    private static String id;
    private static File passwordFile;
    private static MetadataStore store;

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class IntegrationTests {
        @BeforeEach
        void setupCollection() throws IOException {
            System.out.println("Setting up result.");
            store.deleteBuildResults(GROUP);
            store.deleteCollection(GROUP);

            store.createCollection(GROUP);
            BuildResult result = store.createBuildResult(GROUP, LABEL);
            result.setState(BuildResult.COMPLETE);

            String samplesFolder = "samples/";
            // Report data is labled with the version used to create it, in case of differences between versions
            result.setBuildReportData(new FileInputStream(new File(testDir, samplesFolder + "result-data-2.0.0.json")));
            result.setBuildReport(new FileInputStream(new File(testDir, samplesFolder + "report.html")));
        }

        @Test
        void someTest() {
            System.out.println("Running test.");
            String script = new File(testDir, "../bin/static-report-migration.sh").getPath();

            List<String> command = new ArrayList<>();
            command.add(script);
            command.add("--url");
            command.add(url);
            command.add("--id");
            command.add(id);
            command.add("--pwFile");
            command.add(passwordFile.getPath());
            command.add("--groups");
            command.add(GROUP);
            runMigrationScript(command);
            validateResults();
        }
    }

    @BeforeAll
    static void setupStore() throws IOException {
        //Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr--r--");
        //Files.setPosixFilePermissions(Paths.get(script), permissions);
        System.out.println("Setting up store.");
        if (System.getProperties().containsKey(URL_KEY) == false) {
            fail(String.format("Missing URL system property '%s'.", URL_KEY))
        }
        if (System.getProperties().containsKey(ID_KEY) == false) {
            fail(String.format("Missing ID system property '%s'.", ID_KEY))
        }
        if (System.getProperties().containsKey(PW_FILE_KEY) == false) {
            fail(String.format("Missing Password File system property '%s'.", PW_FILE_KEY))
        }

        url = System.getProperty(URL_KEY)
        id = System.getProperty(ID_KEY)
        passwordFile = new File(System.getProperty(PW_FILE_KEY))

        store = MetadataStoreFactory.createDb2MetadataStore(url, id, passwordFile)
    }

    @AfterAll
    static void cleanupStore() {
        System.out.println("Cleaning up store.");
        store.deleteBuildResults(GROUP);
        store.deleteCollection(GROUP);
    }

    private void validateResults() {
        System.out.println("Validating results.");
        for (BuildResult result : store.getBuildResults(Collections.singletonMap(QueryParms.GROUP, GROUP))) {
            assertFalse(Utils.readFromStream(result.getBuildReport().getContent(), "UTF-8").contains("</script>"), String.format("Result '%s:%s' not converted.", result.getGroup(), result.getLabel()));
        }
    }

    private void runMigrationScript(String command) throws IOException, InterruptedException {
        runMigrationScript(Arrays.asList(command.split(" ")));
    }

    private void runMigrationScript(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.environment().put("DBB_HOME", EnvVars.getHome());
        System.out.println("Waiting for process.");
        
        Process process = processBuilder.start();
        long startTime = System.currentTimeMillis();
        long maxTime = 3 * 60 * 1000; // Minutes (3) -> MS
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        char[] buffer = new char[16*1024];

        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();
        long elapsedTime = 0;
        System.out.println("Entering loop.");
        while (elapsedTime < maxTime) {
            System.out.println("At the top.");
            
            System.out.println("Reading Input.");
            int charsRead = stdInput.read(buffer);
            if (charsRead == -1) {
                System.out.println("Exiting, Stdin");
                break;
            }
            
            String newString = new String(buffer, 0, charsRead);
            output.append(newString);
            System.out.println("NEW STRING: " + newString);
            if (newString.toLowerCase().contains("('y' or 'n')")) {
                System.out.println("Sending confirmation.");
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                out.write("y");
                out.newLine();
                out.flush();
            }
            System.out.println("OUTPUT: " + output.toString());
            
            System.out.println("Reading Error.");
            int charsRead = stdError.read(buffer);
            if (charsRead == -1) {
                System.out.println("Exiting, Stderr");
                break;
            }

            error.append(buffer, 0, charsRead);
            System.out.println("ERROR: " + error.toString());
            
            Thread.sleep(1000);
            System.out.println("End sleep.");
            elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println(String.format("Elapsed Time: %s", elapsedTime / 1000));
        }

        System.out.println(output);
        System.out.println(error);
        process.destroy();
        System.out.println("Process finished");
        int rc = process.exitValue();
        String errorMessage = String.format("Script return code is not equal to 0\nOUT:\n%s\n\nERR:\n%s", output, error);
        assertEquals(0, rc, errorMessage);
    }

    private String instreamToString(InputStream is) throws IOException {
		StringBuilder buffer = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = br.readLine()) != null) {
			buffer.append(line);
			buffer.append('\n');
		}
        String output = new String(buffer);
        System.out.println(output);
		return output;
	}
}