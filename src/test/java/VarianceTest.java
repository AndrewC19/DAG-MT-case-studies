// Fixed version of Variance
//import fixed.apache.math.Variance;

// Buggy version of Variance
import buggy.apache.math.Variance;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class VarianceTest {
    /***
     * Tests for the following metamorphic relations:
     *
     * MEAN:
     * mean --> numerator | [begin, length, values, weights]
     * mean _||_ variance | [denominator, length, numerator]
     * mean _||_ denominator | [begin, length, weights]
     *
     * LENGTH:
     * length --> numerator | [begin, mean, values, weights]
     * length --> variance | [denominator, numerator]
     * length --> denominator | [begin, weights]
     *
     * BEGIN:
     * begin --> numerator | [length, mean, values, weights]
     * begin --> denominator | [length, weights]
     *
     * VALUES:
     * values --> numerator | [begin, length, mean, weights]
     *
     * WEIGHTS:
     * weights --> numerator | [begin, length, mean, values]
     * weights --> denominator | [begin, length]
     *
     * NUMERATOR (unobserved):
     * numerator --> variance | [begin, denominator, length, mean, values, weights]
     * numerator _||_ denominator | [begin, length, mean, values, weights]
     *
     * DENOMINATOR (unobserved):
     * denominator --> variance | [begin, length, numerator, weights]
     * denominator _||_ values | [begin, length, weights]
     *
     * VARIANCE:
     * variance _||_ begin | [denominator, length, numerator]
     * variance _||_ values | [denominator, length, numerator]
     * variance _||_ weights | [denominator, length, numerator]
     */
    private Variance var;
    private int seed;

    @Before
    public void setUp() {
        this.var = new Variance();
        this.seed = 100;
    }

    // MEAN
    @Test
    public void meanShouldCauseNumerator(){
        // Define fixed inputs: values, weights, length, begin
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - begin, this.seed);

        double sourceMean = generateDouble(0.5, 100.0);
        double followUpMean = sourceMean * 2;

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, sourceMean, begin, length).get("numerator");
        double followUpVariance = (double) var.evaluate(values, weights, followUpMean, begin, length).get("numerator");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    @Test
    public void meanShouldNotCauseVariance(){
        // Define fixed inputs
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);
        double denominator = generateDouble(0.5, 100.0, this.seed);
        double numerator = generateDouble(0.5, 100.0, this.seed);
        Map<String, Double> fraction = new HashMap<String, Double>() {{
            put("numerator", numerator);
            put("denominator", denominator);
        }};

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - begin, this.seed);

        double sourceMean = generateDouble(0.5, 100.0);
        double followUpMean = sourceMean * 2;

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, sourceMean, begin, length,
                fraction).get("variance");
        double followUpVariance = (double) var.evaluate(values, weights, followUpMean, begin, length,
                fraction).get("variance");

        // The intervention should not cause variance to change
        assertEquals(sourceVariance, followUpVariance, 0);
    }

    @Test
    public void meanShouldNotCauseDenominator(){
        // Define fixed inputs: values, weights, length, begin
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - begin, this.seed);

        double sourceMean = generateDouble(0.5, 100.0);
        double followUpMean = sourceMean * 2;

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, sourceMean, begin, length).get("denominator");
        double followUpVariance = (double) var.evaluate(values, weights, followUpMean, begin, length).get("denominator");

        // The intervention should cause variance to change
        assertEquals(sourceVariance, followUpVariance, 0);
    }

    // LENGTH
    @Test
    public void lengthShouldCauseNumerator(){
        // Define fixed inputs: values, weights, mean, begin
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int sourceLength = generateInt(1, values.length - begin, this.seed);
        int followUpLength = sourceLength + 1;

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, mean, begin, sourceLength).get("numerator");
        double followUpVariance = (double) var.evaluate(values, weights, mean, begin, followUpLength).get("numerator");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    @Test
    public void lengthShouldCauseVariance(){
        // Define fixed inputs: values, weights, mean, begin
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int sourceLength = generateInt(1, values.length - begin, this.seed);
        int followUpLength = sourceLength + 1;

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, mean, begin, sourceLength).get("variance");
        double followUpVariance = (double) var.evaluate(values, weights, mean, begin, followUpLength).get("variance");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    @Test
    public void lengthShouldCauseDenominator(){
        // Define fixed inputs: values, weights, mean, begin
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int sourceLength = generateInt(1, values.length - begin, this.seed);
        int followUpLength = sourceLength + 1;

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, mean, begin, sourceLength).get("denominator");
        double followUpVariance = (double) var.evaluate(values, weights, mean, begin, followUpLength).get("denominator");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    // BEGIN
    @Test
    public void beginShouldCauseNumerator(){
        // Define fixed inputs: values, weights, mean, length
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);

        // Define our intervention for the input begin i.e. a source, follow-up pair
        int sourceBegin = 0;
        int followUpBegin = 1;

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - followUpBegin, this.seed);

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, mean, sourceBegin, length).get("numerator");
        double followUpVariance = (double) var.evaluate(values, weights, mean, followUpBegin, length).get("numerator");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    @Test
    public void beginShouldCauseDenominator(){

        // Define fixed inputs: values, weights, mean, length
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);

        // Define our intervention for the input begin i.e. a source, follow-up pair
        int sourceBegin = 0;
        int followUpBegin = 1;

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - followUpBegin, this.seed);

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, mean, sourceBegin, length).get("denominator");
        double followUpVariance = (double) var.evaluate(values, weights, mean, followUpBegin, length).get("denominator");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    @Test
    public void beginShouldNotCauseVariance(){
        // Define fixed inputs: values, weights, mean, length
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        double denominator = generateDouble(0.5, 100.0, this.seed);
        double numerator = generateDouble(0.5, 100.0, this.seed);
        Map<String, Double> fraction = new HashMap<String, Double>() {{
            put("numerator", numerator);
            put("denominator", denominator);
        }};

        // Define our intervention for the input begin i.e. a source, follow-up pair
        int sourceBegin = 0;
        int followUpBegin = 1;

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - followUpBegin, this.seed);

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, mean, sourceBegin, length,
                fraction).get("variance");
        double followUpVariance = (double) var.evaluate(values, weights, mean, followUpBegin, length,
                fraction).get("variance");

        // The intervention should cause variance to change
        assertEquals(sourceVariance, followUpVariance, 0);
    }


    // VALUES
    @Test
    public void valuesShouldCauseNumerator(){
        // Define fixed inputs
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);

        // Define intervention
        double[] sourceValues = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] followUpValues = sourceValues.clone();
        for (int i = 0; i < followUpValues.length; i++) {
            followUpValues[i] *= 2;
        }
        int begin = generateInt(0, sourceValues.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, sourceValues.length - begin, this.seed);

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(sourceValues, weights, mean, begin, length).get("numerator");
        double followUpVariance = (double) var.evaluate(followUpValues, weights, mean, begin, length).get("numerator");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    @Test
    public void valuesShouldNotCauseDenominator(){
        // Define fixed inputs
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);

        // Define intervention
        double[] sourceValues = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] followUpValues = sourceValues.clone();
        for (int i = 0; i < followUpValues.length; i++) {
            followUpValues[i] *= 2;
        }
        int begin = generateInt(0, sourceValues.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, sourceValues.length - begin, this.seed);

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(sourceValues, weights, mean, begin, length).get("denominator");
        double followUpVariance = (double) var.evaluate(followUpValues, weights, mean, begin, length).get("denominator");

        // The intervention should cause variance to change
        assertEquals(sourceVariance, followUpVariance, 0);
    }

    @Test
    public void valuesShouldNotCauseVariance(){
        // Define fixed inputs
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        double denominator = generateDouble(0.5, 100.0, this.seed);
        double numerator = generateDouble(0.5, 100.0, this.seed);
        Map<String, Double> fraction = new HashMap<String, Double>() {{
            put("numerator", numerator);
            put("denominator", denominator);
        }};

        // Define intervention
        double[] sourceValues = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] followUpValues = sourceValues.clone();
        for (int i = 0; i < followUpValues.length; i++) {
            followUpValues[i] *= 2;
        }
        int begin = generateInt(0, sourceValues.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, sourceValues.length - begin, this.seed);

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(sourceValues, weights, mean, begin, length,
                fraction).get("variance");
        double followUpVariance = (double) var.evaluate(followUpValues, weights, mean, begin, length,
                fraction).get("variance");

        // The intervention should cause variance to change
        assertEquals(sourceVariance, followUpVariance, 0);
    }


    // WEIGHTS
    @Test
    public void weightsShouldCauseNumerator(){
        // Define fixed inputs
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - begin, this.seed);

        // Define intervention
        double[] sourceWeights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double[] followUpWeights = sourceWeights.clone();
        for (int i = 0; i < followUpWeights.length; i++) {
            followUpWeights[i] *= 2;
        }

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, sourceWeights, mean, begin, length).get("numerator");
        double followUpVariance = (double) var.evaluate(values, followUpWeights, mean, begin, length).get("numerator");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    @Test
    public void weightsShouldCauseDenominator(){
        // Define fixed inputs
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - begin, this.seed);

        // Define intervention
        double[] sourceWeights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double[] followUpWeights = sourceWeights.clone();
        for (int i = 0; i < followUpWeights.length; i++) {
            followUpWeights[i] *= 2;
        }

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, sourceWeights, mean, begin, length).get("denominator");
        double followUpVariance = (double) var.evaluate(values, followUpWeights, mean, begin, length).get("denominator");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    @Test
    public void weightsShouldNotCauseVariance(){
        // Define fixed inputs
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);
        double denominator = generateDouble(0.5, 100.0, this.seed);
        double numerator = generateDouble(0.5, 100.0, this.seed);
        Map<String, Double> fraction = new HashMap<String, Double>() {{
            put("numerator", numerator);
            put("denominator", denominator);
        }};

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - begin, this.seed);

        // Define intervention
        double[] sourceWeights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double[] followUpWeights = sourceWeights.clone();
        for (int i = 0; i < followUpWeights.length; i++) {
            followUpWeights[i] *= 2;
        }

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, sourceWeights, mean, begin, length,
                fraction).get("variance");
        double followUpVariance = (double) var.evaluate(values, followUpWeights, mean, begin, length,
                fraction).get("variance");

        // The intervention should cause variance to change
        assertEquals(sourceVariance, followUpVariance, 0);
    }


    // NUMERATOR
    @Test
    public void numeratorShouldCauseVariance(){
        // Define fixed inputs
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - begin, this.seed);

        // Source and follow-up
        double numerator = generateDouble(0.5, 100.0, this.seed);
        double followUpNumerator = numerator*2;
        Map<String, Double> sourceFraction = new HashMap<String, Double>() {{
            put("numerator", numerator);
        }};
        Map<String, Double> followUpFraction = new HashMap<String, Double>() {{
            put("numerator", followUpNumerator);
        }};

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, mean, begin, length,
                sourceFraction).get("variance");
        double followUpVariance = (double) var.evaluate(values, weights, mean, begin, length,
                followUpFraction).get("variance");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    @Test
    public void numeratorShouldNotCauseDenominator(){
        // Define fixed inputs
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - begin, this.seed);

        // Source and follow-up
        double numerator = generateDouble(0.5, 100.0, this.seed);
        double followUpNumerator = numerator*2;
        Map<String, Double> sourceFraction = new HashMap<String, Double>() {{
            put("numerator", numerator);
        }};
        Map<String, Double> followUpFraction = new HashMap<String, Double>() {{
            put("numerator", followUpNumerator);
        }};

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, mean, begin, length,
                sourceFraction).get("denominator");
        double followUpVariance = (double) var.evaluate(values, weights, mean, begin, length,
                followUpFraction).get("denominator");

        // The intervention should cause variance to change
        assertEquals(sourceVariance, followUpVariance, 0);
    }

    // DENOMINATOR
    @Test
    public void denominatorShouldCauseVariance(){
        // Define fixed inputs
        double[] values = generateDoubleArray(5, 1.0, 10.0, this.seed);
        double[] weights = generateDoubleArray(5, 5.0, 15.0, this.seed);
        double mean = generateDouble(0.5, 10.0, this.seed);
        int begin = generateInt(0, values.length - 2, this.seed);

        // Length + begin cannot exceed the length of the array
        int length = generateInt(1, values.length - begin, this.seed);

        // Source and follow-up
        double sourceDenominator = generateDouble(0.5, 100.0, this.seed);
        double followUpDenominator = sourceDenominator*2;
        Map<String, Double> sourceFraction = new HashMap<String, Double>() {{
            put("denominator", sourceDenominator);
        }};
        Map<String, Double> followUpFraction = new HashMap<String, Double>() {{
            put("denominator", followUpDenominator);
        }};

        // Compute source and follow-up variances
        double sourceVariance = (double) var.evaluate(values, weights, mean, begin, length,
                sourceFraction).get("variance");
        double followUpVariance = (double) var.evaluate(values, weights, mean, begin, length,
                followUpFraction).get("variance");

        // The intervention should cause variance to change
        assertNotEquals(sourceVariance, followUpVariance);
    }

    // INPUT GENERATORS
    public double[] generateDoubleArray(int nItems, double minValue, double maxValue) {
        Random random = new Random();
        return random.doubles(nItems, minValue, maxValue).toArray();
    }

    public double[] generateDoubleArray(int nItems, double minValue, double maxValue, long seed) {
        Random random = new Random(seed);
        return random.doubles(nItems, minValue, maxValue).toArray();
    }

    public double generateDouble(double minValue, double maxValue) {
        Random random = new Random();
        return minValue + (maxValue - minValue) * random.nextDouble();
    }

    public double generateDouble(double minValue, double maxValue, long seed) {
        Random random = new Random(seed);
        return minValue + (maxValue - minValue) * random.nextDouble();
    }

    public int generateInt(int minValue, int maxValue) {
        Random random = new Random();
        return minValue + random.nextInt(maxValue - minValue);
    }

    public int generateInt(int minValue, int maxValue, long seed) {
        Random random = new Random(seed);
        return minValue + random.nextInt(maxValue - minValue);
    }
}
