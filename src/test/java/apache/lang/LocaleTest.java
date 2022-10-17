package apache.lang;


import causality.CausalRelationship;
import causality.ConditionalIndependence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class LocaleTest extends FormatTest {

    // LOCALE MRs:
    // locale --> mRules | ['pattern', 'timeZoneID'],
    // locale --> formattedString | ['mMaxLengthEstimate', 'mRules'],
    // locale _||_ mMaxLengthEstimate | ['mRules'],

    @Before
    public void setUp() {
        super.setUp();
        this.treatmentVariables = new String[]{"localeLanguage", "localeCountry"};
        this.followUpValues = new String[]{"en", "GB"};
        this.shouldCauseRelations = new CausalRelationship[]{
                new CausalRelationship("locale", "formattedString",
                        new String[]{"mMaxLengthEstimate", "mRules"}),
                new CausalRelationship("locale", "mRules",
                        new String[]{"pattern", "timeZoneID"})
        };
        this.shouldNotCauseRelations = new ConditionalIndependence[]{
                new ConditionalIndependence("locale", "mMaxLengthEstimate",
                        new String[]{"mRules"})
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