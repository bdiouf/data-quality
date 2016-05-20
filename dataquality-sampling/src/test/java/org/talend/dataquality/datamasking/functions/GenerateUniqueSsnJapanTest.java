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
public class GenerateUniqueSsnJapanTest {

    private String output;

    private GenerateUniqueSsnJapan gnj = new GenerateUniqueSsnJapan();

    @Before
    public void setUp() throws Exception {
        gnj.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = gnj.generateMaskedRow("830807527228");
        assertEquals(output, "211807151545");
        output = gnj.generateMaskedRow("486953617449");
        assertEquals(output, "585103893290");
    }

    @Test
    public void testWrongSsnField() {
        gnj.setKeepInvalidPattern(false);
        // without a number
        output = gnj.generateMaskedRow("83080727228");
        assertEquals(output, null);
        // with a letter instead of a number
        output = gnj.generateMaskedRow("83080752722P");
        assertEquals(output, null);
    }
}
