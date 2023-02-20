import com.ibm.dbb.metadata.MetadataStore;
import com.ibm.dbb.metadata.MetadataStoreFactory;
import com.ibm.dbb.metadata.BuildResult;
import com.ibm.dbb.metadata.BuildResult.QueryParms;
import com.ibm.dbb.EnvVars;
import com.ibm.dbb.build.internal.Utils;

import java.util.concurrent.TimeUnit;

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
        store.deleteBuildResults(GROUP);
        store.deleteCollection(GROUP);
    }

    private void validateResults() {
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
        
        Process process = processBuilder.start();
        String output;
        String error;
        try {
            boolean success = process.waitFor(3, TimeUnit.MINUTES);
            
            int rc = process.exitValue();
            
            output = instreamToString(process.getInputStream());
            error = instreamToString(process.getErrorStream());
            assertTrue(success, "The migration process has timed out.");
            String errorMessage = String.format("Script return code is not equal to 0\nOUT:\n%s\n\nERR:\n%s", output, error);
            assertEquals(0, rc, errorMessage);
        } finally {
            System.out.println(output);
            System.out.println(error);
        }
        
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