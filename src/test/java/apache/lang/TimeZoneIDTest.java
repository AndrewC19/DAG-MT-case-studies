package apache.lang;

import causality.CausalRelationship;
import causality.ConditionalIndependence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TimeZoneIDTest extends FormatTest {

    // TIME ZONE ID MRs:
    // timeZoneID -> mRules | ['locale', 'pattern'],
    // timeZoneID _||_ mMaxLengthEstimate | ['mRules'],
    // timeZoneID _||_ formattedString | ['locale', 'mMaxLengthEstimate', 'mRules'],

    @Before
    public void setUp() {
        super.setUp();
        this.treatmentVariables = new String[]{"timeZoneID"};
        this.followUpValues = new String[]{"Canada/Saskatchewan"};

        this.shouldCauseRelations = new CausalRelationship[]{
                new CausalRelationship("timeZoneID", "mRules",
                        new String[]{"locale", "pattern"})
        };
        this.shouldNotCauseRelations = new ConditionalIndependence[]{
                new ConditionalIndependence("timeZoneID", "mMaxLengthEstimate",
                        new String[]{"mRules"}),
                new ConditionalIndependence("timeZoneID", "formattedString",
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