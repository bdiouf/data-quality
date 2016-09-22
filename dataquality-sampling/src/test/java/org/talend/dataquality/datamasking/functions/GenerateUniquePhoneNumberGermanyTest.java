package org.talend.dataquality.datamasking.functions;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberGermanyTest {

    private String output;

    private AbstractGenerateUniquePhoneNumber gng = new GenerateUniquePhoneNumberGermany();

    @Before
    public void setUp() throws Exception {
        gng.setRandom(new Random(56));
        gng.setKeepFormat(true);
    }

    @Test
    public void testValidWithFormat() {
        output = gng.generateMaskedRow("(089) / 636-48018");
        assertEquals("(089) / 788-87882", output);
    }

    @Test
    public void testValidWithoutFormat() {
        gng.setKeepFormat(false);
        output = gng.generateMaskedRow("(089) / 636-48018");
        assertEquals("(089)78887882", output);
    }

    @Test
    public void testInvalid() {
        // without a number
        output = gng.generateMaskedRow("35686");
        assertEquals(null, output);
        gng.setKeepInvalidPattern(true);
        // with a letter
        output = gng.generateMaskedRow("556 425 98A59");
        assertEquals("556 425 98A59", output);
    }

}
