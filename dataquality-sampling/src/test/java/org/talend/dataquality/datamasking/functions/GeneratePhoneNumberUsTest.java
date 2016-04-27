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
import org.talend.dataquality.datamasking.functions.GeneratePhoneNumberUS;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 19 août 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberUsTest {

    private String output;

    private GeneratePhoneNumberUS gpnus = new GeneratePhoneNumberUS();

    @Before
    public void setUp() throws Exception {
        gpnus.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = gpnus.generateMaskedRow(null);
        assertEquals(output, "730-207-5272"); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        boolean res = true;
        gpnus.setRandomWrapper(new RandomWrapper());
        for (int i = 0; i < 10; ++i) {
            String tmp = gpnus.generateMaskedRow(null);
            res = (tmp.charAt(0) != '0' && tmp.charAt(1) != tmp.charAt(2) && tmp.charAt(4) != '0');
            assertEquals("invalid pĥone number " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gpnus.keepNull = true;
        output = gpnus.generateMaskedRow(null);
        assertEquals(output, null);
    }
}
