package org.talend.dataquality.datamasking.functions;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberUkTest {

    private String output;

    private AbstractGenerateUniquePhoneNumber gnu = new GenerateUniquePhoneNumberUk();

    @Before
    public void setUp() throws Exception {
        gnu.setRandom(new Random(56));
        gnu.setKeepFormat(true);
    }

    @Test
    public void testValidWithFormat() {
        output = gnu.generateMaskedRow("07700 900343");
        assertEquals("07706 689307", output);
    }

    @Test
    public void testValidWithoutFormat() {
        gnu.setKeepFormat(false);
        output = gnu.generateMaskedRow("07700 900343");
        assertEquals("07706689307", output);
    }

    @Test
    public void testInvalid() {
        // without a number
        output = gnu.generateMaskedRow("35686");
        assertEquals(null, output);
        gnu.setKeepInvalidPattern(true);
        // with a letter
        output = gnu.generateMaskedRow("556 425 98A59");
        assertEquals("556 425 98A59", output);
    }
}
