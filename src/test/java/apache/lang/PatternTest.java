package apache.lang;

import causality.CausalRelationship;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import causality.ConditionalIndependence;

public class PatternTest extends FormatTest {

    // PATTERN MRs:
    // pattern --> mRules | ['locale', 'timeZoneID'],
    // pattern _||_ mMaxLengthEstimate | ['mRules'],
    // pattern _||_ formattedString | ['locale', 'mMaxLengthEstimate', 'mRules'],

    @Before
    public void setUp() {
        super.setUp();
        this.treatmentVariables = new String[]{"pattern"};
        this.followUpValues = new String[]{"EEE, dd MMM yyyy"};

        this.shouldCauseRelations = new CausalRelationship[]{
                new CausalRelationship("pattern", "mRules",
                        new String[]{"locale", "timeZoneID"})
        };

        this.shouldNotCauseRelations = new ConditionalIndependence[]{
                new ConditionalIndependence("pattern", "mMaxLengthEstimate",
                        new String[]{"mRules"}),
                new ConditionalIndependence("pattern", "formattedString",
                        new String[]{"locale", "mMaxLengthEstimate", "mRules"})
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

    @Test
    public void shouldNotCauseTest() throws IOException {
        super.shouldNotCauseTest();
    }
}