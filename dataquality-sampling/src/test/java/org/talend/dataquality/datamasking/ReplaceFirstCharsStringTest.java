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
import org.talend.dataquality.datamasking.functions.ReplaceFirstCharsString;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class ReplaceFirstCharsStringTest {

    private String output;

    private String input = "123456";

    private ReplaceFirstCharsString rfcs = new ReplaceFirstCharsString();

    @Before
    public void setUp() throws Exception {
        rfcs.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        rfcs.integerParam = 3;
        output = rfcs.generateMaskedRow(input);
        assertEquals(output, "830456");
    }

    @Test
    public void testDummyGood() {
        rfcs.integerParam = 7;
        output = rfcs.generateMaskedRow(input);
        assertEquals(output, "830807");
    }

}
