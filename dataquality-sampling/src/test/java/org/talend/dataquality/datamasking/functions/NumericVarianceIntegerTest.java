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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.functions.NumericVarianceInteger;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class NumericVarianceIntegerTest {

    private String output;

    private Integer input = 123;

    private NumericVarianceInteger nvi = new NumericVarianceInteger();

    @Before
    public void setUp() throws Exception {
        nvi.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        nvi.integerParam = 10;
        output = nvi.generateMaskedRow(input).toString();
        assertEquals(output, String.valueOf(114));
    }

    @Test
    public void testDummy() {
        nvi.integerParam = -10;
        output = nvi.generateMaskedRow(input).toString();
        assertEquals(output, String.valueOf(114));
    }
}
