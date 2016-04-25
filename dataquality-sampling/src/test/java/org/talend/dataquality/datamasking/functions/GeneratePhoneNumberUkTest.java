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
import org.talend.dataquality.datamasking.functions.GeneratePhoneNumberUK;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 19 août 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberUkTest {

    private String output;

    private GeneratePhoneNumberUK gpnuk = new GeneratePhoneNumberUK();

    @Before
    public void setUp() throws Exception {
        gpnuk.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = gpnuk.generateMaskedRow(null);
        assertEquals(output, "020 3830 8075"); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        boolean res = true;
        gpnuk.setRandomWrapper(new RandomWrapper());
        for (int i = 0; i < 10; ++i) {
            String tmp = gpnuk.generateMaskedRow(null);
            res = (tmp.substring(0, 5).equals("020 3")); //$NON-NLS-1$
            assertEquals("invalid pĥone number " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gpnuk.keepNull = true;
        output = gpnuk.generateMaskedRow(null);
        assertEquals(output, null);
    }
}
