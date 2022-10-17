package apache.lang;

import causality.CausalRelationship;
import causality.ConditionalIndependence;
import causality.DAGProperty;
import com.sun.tools.attach.VirtualMachine;

// Buggy version of FastDateFormat
import org.apache.commons.lang3.time.FastDateFormat;

// Fixed version of FastDateFormat
//import org.apache.commons.lang3.fixed.time.FastDateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;

public class FormatTest {

    public String[] treatmentVariables;
    public String[] followUpValues;
    public HashMap<String, String> inputConfig;
    public CausalRelationship[] shouldCauseRelations;
    public ConditionalIndependence[] shouldNotCauseRelations;

    private static final String CONFIG_FILE_PATH = "config.properties";
    private static final String OUTPUT_FILE_PATH = "results.properties";
    private static final String JAVA_AGENT_PATH = "instrumentation-agent.jar";


    @Before
    public void setUp() {
        // Configure the baseline (source test case) configuration
        this.inputConfig = new HashMap<>();
        this.inputConfig.put("timeZoneID", "UK");
        this.inputConfig.put("localeLanguage", "en");
        this.inputConfig.put("localeCountry", "US");
        this.inputConfig.put("pattern", "EEEE', week 'ww");
        this.inputConfig.put("year", "2010");
        this.inputConfig.put("month", "0");
        this.inputConfig.put("date", "1");
        this.inputConfig.put("mRules", "");
        writeToConfigFile(this.inputConfig);
    }

    @After
    public void tearDown() throws IOException {
        // Clear the config files after executing each test
        Properties config = new Properties();
        config.load(new FileInputStream(CONFIG_FILE_PATH));
        config.clear();
        config.store(new FileOutputStream(CONFIG_FILE_PATH), null);

        Properties results = new Properties();
        results.load(new FileInputStream(OUTPUT_FILE_PATH));
        results.clear();
        results.store(new FileOutputStream(OUTPUT_FILE_PATH), null);
    }

    @Test
    public void shouldCauseTest() throws IOException {
        for (CausalRelationship cr: this.shouldCauseRelations) {
            setUp();

            // Fix value of any listed adjustment variables
            if (cr.getAdjustmentVars() != null)
                addAdjustmentsToInputConfig(cr);
            writeToConfigFile(inputConfig);
            System.out.println("SOURCE IC: " +inputConfig);

            // Run source test case
            HashMap<String, String> sourceResults = runFormatWithConfigFile();

            // Apply intervention to form follow-up test case
            for (int i=0; i<this.treatmentVariables.length; i++) {
                inputConfig.put(this.treatmentVariables[i], this.followUpValues[i]);
            }

            writeToConfigFile(inputConfig);
            System.out.println("FOLLOW-UP IC: " + inputConfig);

            // Run follow-up test case
            HashMap<String, String> followUpResults = runFormatWithConfigFile();

            System.out.println(sourceResults);
            System.out.println(followUpResults);
            // Apply assertion
            org.junit.Assert.assertNotEquals(
                    "Failed on: " + cr.outcomeVar,
                    sourceResults.get(cr.outcomeVar),
                    followUpResults.get(cr.outcomeVar));
            tearDown();
        }
    }

    @Test
    public void shouldNotCauseTest() throws IOException {
        for (ConditionalIndependence ci : this.shouldNotCauseRelations) {
            setUp();

            // Fix value of adjustment variables
            if (ci.getAdjustmentVars() != null)
                addAdjustmentsToInputConfig(ci);
            writeToConfigFile(inputConfig);

            // Run source test case
            attachAgentToVM();
            HashMap<String, String> sourceResults = runFormatWithConfigFile();
            System.out.println("SOURCE:");
            System.out.println(inputConfig);
            System.out.println(sourceResults);

            // Apply intervention to create follow-up
            for (int i=0; i<this.treatmentVariables.length; i++) {
                inputConfig.put(this.treatmentVariables[i], this.followUpValues[i]);
                writeToConfigFile(inputConfig);
            }


            // Run follow-up test case
            attachAgentToVM();
            HashMap<String, String> followUpResults = runFormatWithConfigFile();
            System.out.println("FOLLOW UP:");
            System.out.println(inputConfig);
            System.out.println(followUpResults);
            // Apply assertion
            org.junit.Assert.assertEquals(
                    "Failed on: " + ci.toString(),
                    sourceResults.get(ci.outcomeVar),
                    followUpResults.get(ci.outcomeVar));

            tearDown();
        }
    }

    // GENERATORS
    public String sampleValue(String variableName) {
        String value;
        switch (variableName) {
            case "pattern":
                value = samplePattern();
                break;
            case "timeZoneID":
                value = sampleTimeZoneID();
                break;
            case "localeLanguage":
                value = sampleLocaleLanguage();
                break;
            case "localeCountry":
                value = sampleLocaleCountry();
                break;
            case "year":
                value = Integer.toString(sampleInt(1, 20));
                break;
            case "month":
                value = Integer.toString(sampleInt(1, 12));
                break;
            case "date":
                value = Integer.toString(sampleInt(1, 28));
                break;
            case "mMaxLengthEstimate":
                value = Integer.toString(sampleInt(1, 100));
                break;
            case "mRules":
                value = "test";
                break;
            default:
                throw new IllegalStateException("No generator found for the following variable: " + variableName);
        }
        return value;
    }

    public String samplePattern() {
        // Sample date patterns taken from the docs examples that contain year, month, and day:
        // https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html

        String[] patterns = new String[]{
                "yyyy.MM.dd G 'at' HH:mm:ss z, '~week 'ww",
                "EEE, MMM d, ''yy, '~week 'ww",
                "yyyyy.MMMMM.dd GGG hh:mm aaa, '~week 'ww",
                "EEE, d MMM yyyy HH:mm:ss Z, '~week 'ww",
                "yyMMddHHmmssZ, '~week 'ww",
                "yyyy-MM-ww-dd'T'HH:mm:ss, '~week 'ww"};
        int randomIndex = new Random().nextInt(patterns.length);
        return patterns[randomIndex];
    }

    public String sampleTimeZoneID() {
        String[] availableTimeZoneIDs = TimeZone.getAvailableIDs();
        int randomIndex = new Random().nextInt(availableTimeZoneIDs.length);
        return availableTimeZoneIDs[randomIndex];
    }

    public String sampleLocaleLanguage() {
        String[] availableLocaleLanguages = Locale.getISOLanguages();
        int randomIndex = new Random().nextInt(availableLocaleLanguages.length);
        return availableLocaleLanguages[randomIndex];
    }

    public String sampleLocaleCountry() {
        String[] availableLocaleCountries = Locale.getISOCountries();
        int randomIndex = new Random().nextInt(availableLocaleCountries.length);
        return availableLocaleCountries[randomIndex];
    }

    public int sampleInt(int minValue, int maxValue) {
        Random random = new Random();
        return minValue + random.nextInt(maxValue - minValue);
    }

    public double sampleDouble(double minValue, double maxValue) {
        Random random = new Random();
        return minValue + (maxValue - minValue) * random.nextDouble();
    }


    // HELPERS
    /***
     * Write the config HashMap to the config properties file.
     * @param config A HashMap containing variable-value pairs to instantiate and instrument
     *               the method-under-test.
     */
    public void writeToConfigFile(HashMap<String, String> config) {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            Properties prop = new Properties();

            for (String key: config.keySet()) {
                prop.setProperty(key, config.get(key));
            }
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /***
     * Run the method-under-test (FastDataFormat.format) with the given config.
     *
     * @return A HashMap containing variable-value pairs for the input and outputs that feature
     * in our causal DAG.
     */
    public HashMap<String, String> runFormatWithConfigFile() {
        HashMap<String, String> results = new HashMap<>();
        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {

            Properties prop = new Properties();
            prop.load(input);

            attachAgentToVM();

            // Instantiate FastDateFormat with the specified pattern, time zone, and locale
            String pattern = prop.getProperty("pattern");
            TimeZone timeZone = TimeZone.getTimeZone(prop.getProperty("timeZoneID"));
            Locale locale = new Locale(prop.getProperty("localeLanguage"), prop.getProperty("localeCountry"));
            FastDateFormat sourceFDF = FastDateFormat.getInstance(pattern, timeZone, locale);

            // Construct the date instance from the specified day, month, and year
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(prop.getProperty("year")),
                    Integer.parseInt(prop.getProperty("month")),
                    Integer.parseInt(prop.getProperty("date")));
            Date d = cal.getTime();

            // Call the method-under-test
            sourceFDF.format(d);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Read results from the results.properties file
        try (InputStream resultsInput = new FileInputStream(OUTPUT_FILE_PATH)) {
            Properties resultsFile = new Properties();
            resultsFile.load(resultsInput);

            for (String resultName: resultsFile.stringPropertyNames())
                results.put(resultName, resultsFile.getProperty(resultName));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    /***
     * Attach the agent conducting instrumentation to the running VM.
     */
    public static void attachAgentToVM() {
        try {
            String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
            String pid = nameOfRunningVM.substring(0, nameOfRunningVM.indexOf('@'));
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(JAVA_AGENT_PATH, CONFIG_FILE_PATH);
            vm.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Given a conditional independence relation, sample and adjust for the specified adjustment variables.
     * @param dp A DAG property that is either a conditional independence relation or a causal relationship.
     */
    public void addAdjustmentsToInputConfig(DAGProperty dp) {
        for (String adjustmentVar : dp.getAdjustmentVars()) {
            if (adjustmentVar.equals("locale")) {
                inputConfig.put("localeLanguage", sampleLocaleLanguage());
                inputConfig.put("localeCountry", sampleLocaleCountry());
            } else {
                inputConfig.put(adjustmentVar, sampleValue(adjustmentVar));
            }
        }
        writeToConfigFile(inputConfig);
    }
}