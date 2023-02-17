import com.ibm.dbb.metadata.MetadataStore;
import com.ibm.dbb.metadata.MetadataStoreFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.fail;

class StaticReportMigrationTests {
    /*static final String testLocation = StaticReportMigrationTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    static final String samplesFolder = "com/ibm/dbb/migration/samples/";
    static final String passwordFolder = "com/ibm/dbb/metadata/passwordUtilFiles/";
    
    static final String group = "Static-Report-Migration-Test";
    static final String label = "buildresult";
    static final String dbbHome = new File(testLocation, "../../DBBZtoolkitUnit").getAbsolutePath();
    static final String script = new File(testLocation, "../sample/report-migration/static-report-migration.groovy").getAbsolutePath();
    static final String testLibs = testLocation + "lib/db2jcc4.jar:" + testLocation + "lib/db2jcc_license_cisuz.jar";

    static final String url = "someurl";
    static final String id = "someusr";
    static final File passwordFile = new File("somepwfile");*/
    private static final String GROUP = "Report-Migration-Test";
    private static final String LABEL = "buildresult";
    private static final String URL_KEY = "test-url";
    private static final String ID_KEY = "test-id";
    private static final String PW_FILE_KEY = "test-pwFile";
    static final String group = "Static-Report-Migration-Test";
    static final String label = "buildresult";

    private static String url;
    private static String id;
    private static File passwordFile;
    private static MetadataStore store;

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class IntegrationTests {
        @BeforeEach
        void setup() throws IOException {
            //Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr--r--");
            //Files.setPosixFilePermissions(Paths.get(script), permissions);
            System.out.println("SETUP Inner")
            println("SCRIPTDIR")
            File testDir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile()
            println(testDir)
        }

        @Test
        void someTest() {
            System.out.println("SOMETEST Inner")
            fail("Somereason")
        }
    }

    @BeforeAll
    static void setup() throws IOException {
        //Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr--r--");
        //Files.setPosixFilePermissions(Paths.get(script), permissions);
        System.out.println("SETUP Outer")
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

    @Test
    void someTest() {
        System.out.println("SOMETEST Outer")
        fail("Somereason")
    }
    /*
    void setupResult(MetadataStore store) throws BuildException, FileNotFoundException {
        store.deleteBuildResults(group);
        store.deleteCollection(group);

        store.createCollection(group);
        BuildResult result = store.createBuildResult(group, label);
        result.setState(BuildResult.COMPLETE);
        // Report data is labled with the version used to create it, in case of differences between versions
        result.setBuildReportData(new FileInputStream(new File(testLocation, samplesFolder + "result-data-2.0.0.json")));
        result.setBuildReport(new FileInputStream(new File(testLocation, samplesFolder + "report.html")));
    }

    @Test
    void testDB2Update2x() throws IOException, BuildException, InterruptedException {
        // create store
        MetadataStore store = MetadataStoreFactory.createDb2MetadataStore(url, id, passwordFile);
        // load result into store
        try {
            System.out.println(testLibs);
            System.out.println(dbbHome);
            setupResult(store);

            // run script
            List<String> command = new ArrayList<>();
            command.add(dbbHome + "/bin/groovyz");
            //command.add("-cp");
            //command.add(testLibs);
            command.add(script);
            command.add("db2");
            command.add("--url");
            command.add(url);
            command.add("--id");
            command.add(id);
            command.add("--pwFile");
            command.add(passwordFile.getAbsolutePath());
            runMigrationScript(command);

            validateResults(store);
        } finally {
            store.deleteBuildResults(group);
            store.deleteCollection(group);
        }
    }

    void validateResults(MetadataStore store) throws BuildException, IOException {
        for (BuildResult result : store.getBuildResults(new HashMap<QueryParms, String>())) {
            assertFalse(Utils.readFromStream(result.getBuildReport().getContent(), "UTF-8").contains("</script>"), result.toString());
        }
    }

    void runMigrationScript(String command) throws IOException, InterruptedException {
        runMigrationScript(Arrays.asList(command.split(" ")));
    }

    void runMigrationScript(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.environment().put("DBB_HOME", dbbHome);
        processBuilder.environment().put("CLASSPATH", testLibs);
        
        Process process = processBuilder.start();
        int rc = process.waitFor();

        System.out.println(instreamToString(process.getInputStream()));
        System.err.println(instreamToString(process.getErrorStream()));

        assertEquals(0, rc);
    }

    private static String instreamToString(InputStream is) throws IOException {
		StringBuilder buffer = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = br.readLine()) != null) {
			buffer.append(line);
			buffer.append('\n');
		}
		return new String(buffer);
	}*/
}