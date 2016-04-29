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

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class KeepLastAndGenerateStringTest {

    private String output;

    private String input = "123456"; //$NON-NLS-1$

    private KeepLastAndGenerateString klads = new KeepLastAndGenerateString();

    @Before
    public void setUp() throws Exception {
        klads.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        klads.integerParam = 3;
        output = klads.generateMaskedRow(input);
        assertEquals(output, "830456"); //$NON-NLS-1$

        // add msjian test for bug TDQ-11339: fix a "String index out of range: -1" exception
        String inputa[] = new String[] { "test1234", "pp456", "wei@sina.com" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String outputa[] = new String[] { "test8034", "pp756", "wei@sina.com" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        klads.integerParam = 2;
        for (int i = 0; i < inputa.length; i++) {
            output = klads.generateMaskedRow(inputa[i]);
            assertEquals(output, outputa[i]);
        }
        // TDQ-11339~
    }

    @Test
    public void testDummyGood() {
        klads.integerParam = 7;
        output = klads.generateMaskedRow(input);
        assertEquals(output, input);
    }

}
