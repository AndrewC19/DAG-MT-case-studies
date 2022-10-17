package apache.lang;

import causality.CausalRelationship;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class mRulesTest extends FormatTest {

    // mRULES MRs:
    // mRules --> mMaxLengthEstimate | ['locale', 'pattern', 'timeZoneID'],
    // mRules --> formattedString | ['locale', 'mMaxLengthEstimate', 'pattern', 'timeZoneID'],

    @Before
    public void setUp() {
        super.setUp();
        this.treatmentVariables = new String[]{"mRules"};
        this.followUpValues = new String[]{"string literal content"};

        this.shouldCauseRelations = new CausalRelationship[]{
                new CausalRelationship("mRules", "mMaxLengthEstimate",
                        new String[]{"locale", "pattern", "timeZoneID"}),
                new CausalRelationship("mRules", "formattedString",
                        new String[]{"locale", "mMaxLengthEstimate", "pattern", "timeZoneID"})
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