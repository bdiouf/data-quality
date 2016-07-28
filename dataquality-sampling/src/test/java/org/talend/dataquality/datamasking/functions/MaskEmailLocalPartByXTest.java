package org.talend.dataquality.datamasking.functions;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.duplicating.RandomWrapper;

public class MaskEmailLocalPartByXTest {

    private String output;

    private String mail = "hehe.hehe@uestc.edu.cn";

    private String spemail = "hehe@telecom-bretagne.eu";

    private MaskEmailLocalPartByX maskEmailLocalPartByX = new MaskEmailLocalPartByX();

    @Test
    public void test1Good() {
        maskEmailLocalPartByX.parse("", false, new RandomWrapper());
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void testSpecialEmail() {
        maskEmailLocalPartByX.parse("", true, new RandomWrapper(Long.valueOf(12345678)));
        output = maskEmailLocalPartByX.generateMaskedRow(spemail);
        Assert.assertEquals("XXXX@telecom-bretagne.eu", output);

    }

    @Test
    public void test2WithInput() {
        maskEmailLocalPartByX.parse("hehe", false, new RandomWrapper());
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void test2WithOneCharacter() {
        maskEmailLocalPartByX.parse("A", false, new RandomWrapper());
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("AAAAAAAAA@uestc.edu.cn", output);
    }

    @Test
    public void test2WithOneDigit() {
        maskEmailLocalPartByX.parse("1", false, new RandomWrapper());
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void test3NullEmail() {
        maskEmailLocalPartByX.parse("", false, new RandomWrapper());
        output = maskEmailLocalPartByX.generateMaskedRow(null);
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void test3KeepNullEmail() {
        maskEmailLocalPartByX.parse("", true, new RandomWrapper());
        output = maskEmailLocalPartByX.generateMaskedRow(null);
        Assert.assertTrue(output == null);
    }

    @Test
    public void test4EmptyEmail() {
        maskEmailLocalPartByX.parse("", false, new RandomWrapper());
        output = maskEmailLocalPartByX.generateMaskedRow("");
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void test5WrongFormat() {
        maskEmailLocalPartByX.parse("", false, new RandomWrapper());
        output = maskEmailLocalPartByX.generateMaskedRow("hehe");
        Assert.assertEquals(output, "hehe");
    }
}
