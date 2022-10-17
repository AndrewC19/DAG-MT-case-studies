package apache.lang;

import causality.CausalRelationship;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class mMaxLengthEstimateTest extends FormatTest {

    // mMAX-LENGTH-ESTIMATE MRs:
    // mMaxLengthEstimate --> formattedString | ['locale', 'mRules']

    @Before
    public void setUp() {
        super.setUp();
        this.treatmentVariables = new String[]{"mMaxLengthEstimate"};
        this.followUpValues = new String[]{"0"};

        // Doesn't actually change the formattedString. It only modifies the initial starting size of the
        // StringBuffer it's stored in. Example of a syntactic causation: syntax suggests this variable should have a
        // causal effect. However, it cannot be observed in practice -- at least not in terms of its imapct on the
        // formatted string.
        this.shouldCauseRelations = new CausalRelationship[]{
                new CausalRelationship("mMaxLengthEstimate", "formattedString",
                        new String[]{"locale", "mRules"})
        };
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

    @Test
    public void shouldCauseTest() throws IOException {
        super.shouldCauseTest();
    }
}