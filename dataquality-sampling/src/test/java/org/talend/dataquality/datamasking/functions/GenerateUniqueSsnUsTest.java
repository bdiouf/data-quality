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
public class GenerateUniqueSsnUsTest {

    private String output;

    private AbstractGenerateUniqueSsn gnu = new GenerateUniqueSsnUs();

    @Before
    public void setUp() throws Exception {
        gnu.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood1() {
        output = gnu.generateMaskedRow("153 65 4862");
        assertEquals(output, "513 99 6374");
    }

    @Test
    public void testGood2() {
        output = gnu.generateMaskedRow("1 56 46 45 99");
        assertEquals(output, "1 63 91 55 89");
    }

    @Test
    public void testWrongSsnField() {
        gnu.setKeepInvalidPattern(false);
        // without a number
        output = gnu.generateMaskedRow("153 65 486");
        assertEquals(output, null);
        // with the fobidden number 666
        output = gnu.generateMaskedRow("666 65 4862");
        assertEquals(output, null);
        // with the fobidden number 00
        output = gnu.generateMaskedRow("153 00 4862");
        assertEquals(output, null);
    }
}
