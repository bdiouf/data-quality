// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.KeepLastAndGenerateString;
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
    }

    @Test
    public void testDummyGood() {
        klads.integerParam = 7;
        output = klads.generateMaskedRow(input);
        assertEquals(output, input);
    }

}
