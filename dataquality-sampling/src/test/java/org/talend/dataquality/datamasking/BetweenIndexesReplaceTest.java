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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.functions.BetweenIndexesReplace;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class BetweenIndexesReplaceTest {

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    private BetweenIndexesReplace bir = new BetweenIndexesReplace();

    @Before
    public void tearUp() {
        bir.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        bir.parameters = "2, 4, X".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bir.generateMaskedRow(input);
        assertEquals("SXXXe", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        bir.parameters = "-2, 8".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bir.generateMaskedRow(input);
        assertEquals("Ahwma", output); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        bir.parameters = "1".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad2() {
        bir.parameters = "lk, df".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bir.generateMaskedRow(input);
        assertEquals(input, output);
    }

}
