//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package fixed.apache.math;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.WeightedEvaluation;
import org.apache.commons.math3.util.MathUtils;

public class Variance extends AbstractStorelessUnivariateStatistic implements Serializable, WeightedEvaluation {
    private static final long serialVersionUID = -9111962718267217978L;
    protected SecondMoment moment = null;
    protected boolean incMoment = true;
    private boolean isBiasCorrected = true;

    public Variance() {
        this.moment = new SecondMoment();
    }

    public Variance(SecondMoment m2) {
        this.incMoment = false;
        this.moment = m2;
    }

    public Variance(boolean isBiasCorrected) {
        this.moment = new SecondMoment();
        this.isBiasCorrected = isBiasCorrected;
    }

    public Variance(boolean isBiasCorrected, SecondMoment m2) {
        this.incMoment = false;
        this.moment = m2;
        this.isBiasCorrected = isBiasCorrected;
    }

    public Variance(Variance original) throws NullArgumentException {
        copy(original, this);
    }

    public void increment(double d) {
        if (this.incMoment) {
            this.moment.increment(d);
        }

    }

    public double getResult() {
        if (this.moment.n == 0L) {
            return 0.0D / 0.0;
        } else if (this.moment.n == 1L) {
            return 0.0D;
        } else {
            return this.isBiasCorrected ? this.moment.m2 / ((double)this.moment.n - 1.0D) : this.moment.m2 / (double)this.moment.n;
        }
    }

    public long getN() {
        return this.moment.getN();
    }

    public void clear() {
        if (this.incMoment) {
            this.moment.clear();
        }

    }

    public double evaluate(double[] values) throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        } else {
            return this.evaluate(values, 0, values.length);
        }
    }

    public double evaluate(double[] values, int begin, int length) throws MathIllegalArgumentException {
        double var = 0.0D / 0.0;
        if (this.test(values, begin, length)) {
            this.clear();
            if (length == 1) {
                var = 0.0D;
            } else if (length > 1) {
                Mean mean = new Mean();
                double m = mean.evaluate(values, begin, length);
                var = this.evaluate(values, m, begin, length);
            }
        }

        return var;
    }

    public double evaluate(double[] values, double[] weights, int begin, int length) throws MathIllegalArgumentException {
        double var = 0.0D / 0.0;
        if (this.test(values, weights, begin, length)) {
            this.clear();
            if (length == 1) {
                var = 0.0D;
            } else if (length > 1) {
                Mean mean = new Mean();
                double m = mean.evaluate(values, weights, begin, length);
                var = (double) this.evaluate(values, weights, m, begin, length).get("variance");
            }
        }

        return var;
    }

    public double evaluate(double[] values, double[] weights) throws MathIllegalArgumentException {
        return this.evaluate(values, weights, 0, values.length);
    }

    public double evaluate(double[] values, double mean, int begin, int length) throws MathIllegalArgumentException {
        double var = 0.0D / 0.0;
        if (this.test(values, begin, length)) {
            if (length == 1) {
                var = 0.0D;
            } else if (length > 1) {
                double accum = 0.0D;
                double dev = 0.0D;
                double accum2 = 0.0D;

                for(int i = begin; i < begin + length; ++i) {
                    dev = values[i] - mean;
                    accum += dev * dev;
                    accum2 += dev;
                }

                double len = (double)length;
                if (this.isBiasCorrected) {
                    var = (accum - accum2 * accum2 / len) / (len - 1.0D);
                } else {
                    var = (accum - accum2 * accum2 / len) / len;
                }
            }
        }

        return var;
    }

    public double evaluate(double[] values, double mean) throws MathIllegalArgumentException {
        return this.evaluate(values, mean, 0, values.length);
    }

    public Map<String, Object> evaluate(double[] values, double[] weights, double mean, int begin, int length) throws MathIllegalArgumentException {

        // INSTRUMENTED CODE: Return all inputs and outputs.
        Map<String, Object> inputOutputMap = new HashMap<String, Object>();
        inputOutputMap.put("values", values);
        inputOutputMap.put("weights", weights);
        inputOutputMap.put("mean", mean);
        inputOutputMap.put("begin", begin);
        inputOutputMap.put("length", length);

        double var = 0.0D / 0.0;
        if (this.test(values, weights, begin, length)) {
            if (length == 1) {
                var = 0.0D;
            } else if (length > 1) {
                double accum = 0.0D;
                double dev = 0.0D;
                double accum2 = 0.0D;

                for(int i = begin; i < begin + length; ++i) {
                    dev = values[i] - mean;
                    accum += weights[i] * dev * dev;
                    accum2 += weights[i] * dev;
                }

                double sumWts = 0.0D;

                for(int i = begin; i < begin + length; ++i) {
                    sumWts += weights[i];
                }

                // INSTRUMENTED CODE: Obtain the numerator and denominator
                double numerator = (accum - (accum2 * accum2 / sumWts));
                double denominator = sumWts;
                if (isBiasCorrected) {
                    denominator -= 1;
                    var = numerator / denominator;
                } else {
                    var = numerator / denominator;
                }
                inputOutputMap.put("numerator", numerator);
                inputOutputMap.put("denominator", denominator);
            }
        }

        inputOutputMap.put("variance", var);
        return inputOutputMap;
    }

    public Map<String, Object> evaluate(double[] values, double[] weights, double mean, int begin, int length,
                                        Map<String, Double> fraction) throws MathIllegalArgumentException {

        // INSTRUMENTED CODE: Return all inputs and outputs.
        Map<String, Object> inputOutputMap = new HashMap<String, Object>();
        inputOutputMap.put("values", values);
        inputOutputMap.put("weights", weights);
        inputOutputMap.put("mean", mean);
        inputOutputMap.put("begin", begin);
        inputOutputMap.put("length", length);

        double var = 0.0D / 0.0;
        if (this.test(values, weights, begin, length)) {
            if (length == 1) {
                var = 0.0D;
            } else if (length > 1) {
                double accum = 0.0D;
                double dev = 0.0D;
                double accum2 = 0.0D;

                for(int i = begin; i < begin + length; ++i) {
                    dev = values[i] - mean;
                    accum += weights[i] * dev * dev;
                    accum2 += weights[i] * dev;
                }

                double sumWts = 0.0D;

                for(int i = begin; i < begin + length; ++i) {
                    sumWts += weights[i];
                }

                // INSTRUMENTED CODE: Obtain the numerator and denominator
                double numerator = 0.0;
                double denominator = 0.0;
                if (fraction.containsKey("numerator")) {
                    numerator += fraction.get("numerator");
                } else {
                    numerator += (accum - (accum2 * accum2 / sumWts));
                }
                if (fraction.containsKey("denominator")) {
                    denominator += fraction.get("denominator");
                } else {
                    denominator += sumWts;
                }

                if (isBiasCorrected) {
                    denominator -= 1;
                    var = numerator / denominator;
                } else {
                    var = numerator / denominator;
                }
                inputOutputMap.put("numerator", numerator);
                inputOutputMap.put("denominator", denominator);
            }
        }

        inputOutputMap.put("variance", var);
        return inputOutputMap;
    }


    public double evaluate(double[] values, double[] weights, double mean) throws MathIllegalArgumentException {
        return (double) this.evaluate(values, weights, mean, 0, values.length).get("variance");
    }

    public boolean isBiasCorrected() {
        return this.isBiasCorrected;
    }

    public void setBiasCorrected(boolean biasCorrected) {
        this.isBiasCorrected = biasCorrected;
    }

    public Variance copy() {
        Variance result = new Variance();
        copy(this, result);
        return result;
    }

    public static void copy(Variance source, Variance dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.moment = source.moment.copy();
        dest.isBiasCorrected = source.isBiasCorrected;
        dest.incMoment = source.incMoment;
    }
}
