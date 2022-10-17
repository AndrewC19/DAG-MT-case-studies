package causality;

import java.util.Arrays;

public class ConditionalIndependence implements DAGProperty {

    public String treatmentVar;
    public String outcomeVar;
    public String[] adjustmentVars;

    public ConditionalIndependence(String treatmentVar, String outcomeVar) {
        this.treatmentVar = treatmentVar;
        this.outcomeVar = outcomeVar;
    }

    public ConditionalIndependence(String treatmentVar, String outcomeVar, String[] adjustmentVar) {
        this.treatmentVar = treatmentVar;
        this.outcomeVar = outcomeVar;
        this.adjustmentVars = adjustmentVar;
    }

    public String[] getAdjustmentVars() {
        return this.adjustmentVars;
    }

    public String toString() {
        return this.treatmentVar + " _||_ "  + this.outcomeVar + " | " + Arrays.toString(this.adjustmentVars);
    }

}
