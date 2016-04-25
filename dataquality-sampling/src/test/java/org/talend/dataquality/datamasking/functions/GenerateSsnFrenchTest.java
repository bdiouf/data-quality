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
import org.talend.dataquality.datamasking.functions.GenerateSsnFr;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 20 août 2015 Detailled comment
 *
 */
public class GenerateSsnFrenchTest {

    private String output;

    private GenerateSsnFr gnf = new GenerateSsnFr();

    @Before
    public void setUp() throws Exception {
        gnf.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = gnf.generateMaskedRow(null);
        assertEquals(output, "2490145075272 83"); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        gnf.setRandomWrapper(new RandomWrapper());
        boolean res = true;
        for (int i = 0; i < 10; ++i) {
            String tmp = gnf.generateMaskedRow(null);
            res = (tmp.charAt(0) == '1' || tmp.charAt(0) == '2');
            assertEquals("wrong number : " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gnf.keepNull = true;
        output = gnf.generateMaskedRow(null);
        assertEquals(output, null);
    }
}
