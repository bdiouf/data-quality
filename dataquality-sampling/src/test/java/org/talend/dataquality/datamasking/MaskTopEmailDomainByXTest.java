// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.datamasking.functions.MaskTopEmailDomainByX;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * DOC qzhao class global comment. Detailled comment
 */
public class MaskTopEmailDomainByXTest {

    private String output;

    private String mailStandard = "hehe@gmail.com";

    private String mailWithPointsInLocal = "hehe.haha@gmail.com";

    private String mailMultipalDomaim = "hehe.haha@uestc.in.edu.cn";

    private MaskTopEmailDomainByX maskTopEmailDomainByX = new MaskTopEmailDomainByX();

    @Test
    public void testGoodStandard() {
        maskTopEmailDomainByX.parse("", false, new RandomWrapper());
        output = maskTopEmailDomainByX.generateMaskedRow(mailStandard);
        Assert.assertEquals("hehe@XXXXX.com", output);
    }

    @Test
    public void testGoodWithPointsInLocal() {
        maskTopEmailDomainByX.parse("", false, new RandomWrapper());
        output = maskTopEmailDomainByX.generateMaskedRow(mailWithPointsInLocal);
        Assert.assertEquals("hehe.haha@XXXXX.com", output);
    }

    @Test
    public void testMultipalDomaim() {
        maskTopEmailDomainByX.parse("", false, new RandomWrapper());
        output = maskTopEmailDomainByX.generateMaskedRow(mailMultipalDomaim);
        Assert.assertEquals("hehe.haha@XXXXX.XX.XXX.cn", output);
    }

    @Test
    public void testOneCharacter() {
        maskTopEmailDomainByX.parse("Z", false, new RandomWrapper());
        output = maskTopEmailDomainByX.generateMaskedRow(mailMultipalDomaim);
        Assert.assertEquals("hehe.haha@ZZZZZ.ZZ.ZZZ.cn", output);
    }

    @Test
    public void testString() {
        maskTopEmailDomainByX.parse("Zed", false, new RandomWrapper());
        output = maskTopEmailDomainByX.generateMaskedRow(mailMultipalDomaim);
        Assert.assertEquals("hehe.haha@XXXXX.XX.XXX.cn", output);
    }

    @Test
    public void testOneDigit() {
        maskTopEmailDomainByX.parse("Zed", false, new RandomWrapper());
        output = maskTopEmailDomainByX.generateMaskedRow(mailMultipalDomaim);
        Assert.assertEquals("hehe.haha@XXXXX.XX.XXX.cn", output);
    }

    @Test
    public void testNullEmail() {
        maskTopEmailDomainByX.parse("", false, new RandomWrapper());
        output = maskTopEmailDomainByX.generateMaskedRow(null);
        Assert.assertEquals("", output);
    }

    @Test
    public void testKeepNullEmail() {
        maskTopEmailDomainByX.parse("", true, new RandomWrapper());
        output = maskTopEmailDomainByX.generateMaskedRow(null);
        Assert.assertEquals(output, output);
    }

    @Test
    public void testEmptyEmail() {
        maskTopEmailDomainByX.parse("", false, new RandomWrapper());
        output = maskTopEmailDomainByX.generateMaskedRow("");
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void testWrongFormat() {
        maskTopEmailDomainByX.parse("", false, new RandomWrapper());
        output = maskTopEmailDomainByX.generateMaskedRow("hehe");
        Assert.assertEquals(output, "hehe");
    }

}
