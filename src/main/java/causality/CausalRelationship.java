package causality;

public class CausalRelationship implements DAGProperty {

    public String treatmentVar;
    public String outcomeVar;
    public String[] adjustmentVars;

    public CausalRelationship(String treatmentVar, String outcomeVar) {
        this.treatmentVar = treatmentVar;
        this.outcomeVar = outcomeVar;
    }

    public CausalRelationship(String treatmentVar, String outcomeVar, String[] adjustmentVar) {
        this.treatmentVar = treatmentVar;
        this.outcomeVar = outcomeVar;
        this.adjustmentVars = adjustmentVar;
    }

    public String[] getAdjustmentVars() {
        return this.adjustmentVars;
    }

    public String toString() {
        return this.treatmentVar + " --> " + this.outcomeVar + " | " + this.adjustmentVars.toString();
    }

}
