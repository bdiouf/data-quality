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
import org.talend.dataquality.datamasking.functions.GeneratePhoneNumberFrench;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberFrenchTest {

    private String output;

    private GeneratePhoneNumberFrench gpn = new GeneratePhoneNumberFrench();

    @Before
    public void setUp() throws Exception {
        gpn.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = gpn.generateMaskedRow(null);
        assertEquals(output, "+33 930807527"); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        boolean res = true;
        gpn.setRandomWrapper(new RandomWrapper());
        for (int i = 0; i < 10; ++i) {
            String tmp = gpn.generateMaskedRow(null);
            int digit = Integer.valueOf(tmp.charAt(4) + "");
            res = (digit >= 1 && digit <= 9);
            assertEquals("invalid pĥone number " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gpn.setKeepNull(true);
        output = gpn.generateMaskedRow(null);
        assertEquals(output, null);
    }

}
