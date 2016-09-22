package org.talend.dataquality.datamasking.functions;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by jdenantes on 21/09/16.
 */
public class GenerateUniquePhoneNumberUsTest {

    private String output;

    private AbstractGenerateUniquePhoneNumber gnu = new GenerateUniquePhoneNumberUs();

    @Before
    public void setUp() throws Exception {
        gnu.setRandom(new Random(42));
        gnu.setKeepFormat(true);
    }

    @Test
    public void testKeepInvalidPatternTrue() {
        gnu.setKeepInvalidPattern(true);
        output = gnu.generateMaskedRow(null);
        assertEquals(null, output);
        output = gnu.generateMaskedRow("");
        assertEquals("", output);
        output = gnu.generateMaskedRow("AHDBNSKD");
        assertEquals("AHDBNSKD", output);
    }

    @Test
    public void testKeepInvalidPatternFalse() {
        gnu.setKeepInvalidPattern(false);
        output = gnu.generateMaskedRow(null);
        assertEquals(null, output);
        output = gnu.generateMaskedRow("");
        assertEquals(null, output);
        output = gnu.generateMaskedRow("AHDBNSKD");
        assertEquals(null, output);
    }

    @Test
    public void testGood1() {
        output = gnu.generateMaskedRow("35-6/42-5/9 865");
        assertEquals("35-6/40-3/5 545", output);
    }

    @Test
    public void testGood2() {
        gnu.setKeepFormat(false);
        // with spaces
        output = gnu.generateMaskedRow("356-425-9865");
        assertEquals("3564035545", output);
    }

    @Test
    public void testWrongSsnFieldNumber() {
        gnu.setKeepInvalidPattern(false);
        // without a number
        output = gnu.generateMaskedRow("25 986");
        assertEquals(null, output);
    }

    @Test
    public void testWrongSsnFieldLetter() {
        gnu.setKeepInvalidPattern(false);
        // with a wrong letter
        output = gnu.generateMaskedRow("556 425 98A59");
        assertEquals(null, output);
    }

}
