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
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * @author jteuladedenantes
 */

public class GenerateUniqueSsnUkTest {

    private String output;

    private AbstractGenerateUniqueSsn gnu = new GenerateUniqueSsnUk();

    @Before
    public void setUp() throws Exception {
        gnu.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood1() {
        output = gnu.generateMaskedRow("AL 486934 D");
        assertEquals(output, "TG 807846 D");
    }

    @Test
    public void testGood2() {
        output = gnu.generateMaskedRow("PP132459A ");
        assertEquals(output, "NJ207147A ");
    }

    @Test
    public void testWrongSsnField() {
        gnu.setKeepInvalidPattern(false);
        // without a number
        output = gnu.generateMaskedRow("PP13259A");
        assertEquals(output, null);
        // with the fobidden letter D
        output = gnu.generateMaskedRow("LO 486934 A");
        assertEquals(output, null);
        // with the fobidden letters NK
        output = gnu.generateMaskedRow("NK 486934 B");
        assertEquals(output, null);
    }
}
