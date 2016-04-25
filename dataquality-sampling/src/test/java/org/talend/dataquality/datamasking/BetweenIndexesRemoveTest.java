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

import org.junit.Test;
import org.talend.dataquality.datamasking.functions.BetweenIndexesRemove;

public class BetweenIndexesRemoveTest {

    private BetweenIndexesRemove bir = new BetweenIndexesRemove();

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    @Test
    public void testGood() {
        bir.parameters = "2, 4".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bir.generateMaskedRow(input);
        assertEquals("Se", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        bir.parameters = "-2, 8".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
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
