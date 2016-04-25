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
import org.talend.dataquality.datamasking.functions.GeneratePhoneNumberJapan;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 20 août 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberJapanTest {

    private String output;

    private GeneratePhoneNumberJapan gpnj = new GeneratePhoneNumberJapan();

    @Before
    public void setUp() throws Exception {
        gpnj.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = gpnj.generateMaskedRow(null);
        assertEquals(output, "3 8308 0752"); //$NON-NLS-1$
    }

    @Test
    public void testCheck() {
        boolean res = true;
        gpnj.setRandomWrapper(new RandomWrapper());
        for (int i = 0; i < 10; ++i) {
            String tmp = gpnj.generateMaskedRow(null);
            res = (tmp.charAt(0) == '3');
            assertEquals("invalid pĥone number " + tmp, res, true); //$NON-NLS-1$
        }
    }

    @Test
    public void testNull() {
        gpnj.keepNull = true;
        assertEquals(output, null);
    }
}
