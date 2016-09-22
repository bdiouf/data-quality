package org.talend.dataquality.datamasking.functions;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberFrTest {

    private String output;

    private AbstractGenerateUniquePhoneNumber gnf = new GenerateUniquePhoneNumberFr();

    @Before
    public void setUp() throws Exception {
        gnf.setRandom(new Random(42));
        gnf.setKeepFormat(true);
    }

    @Test
    public void testValidWithFormat() {
        output = gnf.generateMaskedRow("01.42.95.45.24");
        assertEquals("01.42.59.52.92", output);
        output = gnf.generateMaskedRow("(0033) 6 48 98 75 12");
        assertEquals("(0033) 6 48 63 58 96", output);
    }

    @Test
    public void testValidWithoutFormat() {
        gnf.setKeepFormat(false);
        // with spaces
        output = gnf.generateMaskedRow("01.42.95.45.24");
        assertEquals("0142595292", output);
    }

    @Test
    public void testInvalid() {
        // without a number
        output = gnf.generateMaskedRow("35686");
        assertEquals(null, output);
        gnf.setKeepInvalidPattern(true);
        // with a letter
        output = gnf.generateMaskedRow("556 425 98A59");
        assertEquals("556 425 98A59", output);
    }

}
