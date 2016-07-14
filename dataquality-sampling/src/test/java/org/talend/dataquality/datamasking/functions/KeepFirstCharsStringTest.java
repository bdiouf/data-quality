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

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class KeepFirstCharsStringTest {

    private String output;

    private String input = "a1b2c3d456"; //$NON-NLS-1$

    private KeepFirstCharsString kfag = new KeepFirstCharsString();

    @Test
    public void testGood() {
        kfag.parse("3", false, new RandomWrapper(42));
        output = kfag.generateMaskedRow(input);
        assertEquals("a1b8h0m075", output); //$NON-NLS-1$
    }

    @Test
    public void testGoodForContainSpecailCharacter() {
        kfag.parse("3", false, new RandomWrapper(42)); //$NON-NLS-1$
        output = kfag.generateMaskedRow("+33123456"); //$NON-NLS-1$
        Assert.assertTrue("The result should start with +331", output.startsWith("+331")); //$NON-NLS-1$
        output = kfag.generateMaskedRow("(00)33456"); //$NON-NLS-1$
        Assert.assertTrue("The result should start with (00)3", output.startsWith("(00)3")); //$NON-NLS-1$
        output = kfag.generateMaskedRow("1-86-555-555"); //$NON-NLS-1$
        Assert.assertTrue("The result should start with 1-86", output.startsWith("1-86")); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        kfag.parse("15", false, new RandomWrapper(542));
        output = kfag.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void testParameters() {
        kfag.parse("5,8", false, new RandomWrapper(542));
        output = kfag.generateMaskedRow(input);
        assertEquals("a1b2c88888", output);
    }

}
