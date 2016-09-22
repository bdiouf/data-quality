package org.talend.dataquality.datamasking.functions;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberJapanTest {

    private String output;

    private AbstractGenerateUniquePhoneNumber gnj = new GenerateUniquePhoneNumberJapan();

    @Before
    public void setUp() throws Exception {
        gnj.setRandom(new Random(56));
        gnj.setKeepFormat(true);
    }

    @Test
    public void testValidWithFormat() {
        output = gnj.generateMaskedRow("49-92 8 7895");
        assertEquals("49-43 1 9355", output);
    }

    @Test
    public void testValidWithoutFormat() {
        gnj.setKeepFormat(false);
        output = gnj.generateMaskedRow("49-92 8 7895");
        assertEquals("494319355", output);
    }

    @Test
    public void testInvalid() {
        // without a number
        output = gnj.generateMaskedRow("35686");
        assertEquals(null, output);
        gnj.setKeepInvalidPattern(true);
        // with a letter
        output = gnj.generateMaskedRow("556 425 98A59");
        assertEquals("556 425 98A59", output);
    }
}
