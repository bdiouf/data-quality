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
package org.talend.dataquality.datamasking.functions;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.talend.dataquality.datamasking.functions.MaskFullEmailDomainByX;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * DOC qzhao class global comment. Detailled comment
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MaskFullEmailDomainByXTest {

    private String output;

    private String mail = "hehe.hehe@uestc.edu.cn";

    private MaskFullEmailDomainByX maskEmailDomainByX = new MaskFullEmailDomainByX();

    @Test
    public void test1Good() {
        maskEmailDomainByX.parse("", false, new RandomWrapper());
        output = maskEmailDomainByX.generateMaskedRow(mail);
        Assert.assertEquals("hehe.hehe@XXXXX.XXX.XX", output);
    }

    @Test
    public void testReal() {
        maskEmailDomainByX.parse("", true, new RandomWrapper(Long.valueOf(12345678)));
        output = maskEmailDomainByX.generateMaskedRow("dewitt.julio@hotmail.com");
        Assert.assertEquals("dewitt.julio@XXXXXXX.XXX", output);

    }

    @Test
    public void test2WithInput() {
        maskEmailDomainByX.parse("hehe", false, new RandomWrapper());
        output = maskEmailDomainByX.generateMaskedRow(mail);
        Assert.assertEquals("hehe.hehe@XXXXX.XXX.XX", output);
    }

    @Test
    public void test2WithOneCharacter() {
        maskEmailDomainByX.parse("A", false, new RandomWrapper());
        output = maskEmailDomainByX.generateMaskedRow(mail);
        Assert.assertEquals("hehe.hehe@AAAAA.AAA.AA", output);
    }

    @Test
    public void test2WithOneDigit() {
        maskEmailDomainByX.parse("1", false, new RandomWrapper());
        output = maskEmailDomainByX.generateMaskedRow(mail);
        Assert.assertEquals("hehe.hehe@XXXXX.XXX.XX", output);
    }

    @Test
    public void test3NullEmail() {
        maskEmailDomainByX.parse("", false, new RandomWrapper());
        output = maskEmailDomainByX.generateMaskedRow(null);
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void test3KeepNullEmail() {
        maskEmailDomainByX.parse("", true, new RandomWrapper());
        output = maskEmailDomainByX.generateMaskedRow(null);
        Assert.assertTrue(output == null);
    }

    @Test
    public void test4EmptyEmail() {
        maskEmailDomainByX.parse("", false, new RandomWrapper());
        output = maskEmailDomainByX.generateMaskedRow("");
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void test5WrongFormat() {
        maskEmailDomainByX.parse("", false, new RandomWrapper());
        output = maskEmailDomainByX.generateMaskedRow("hehe");
        Assert.assertEquals(output, "hehe");
    }

}
