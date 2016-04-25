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
import org.talend.dataquality.datamasking.functions.MaskAddress;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class MaskAddressTest {

    private String output;

    private MaskAddress ma = new MaskAddress();

    @Before
    public void setUp() throws Exception {
        ma.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        String input = "5 rue de l'oise"; //$NON-NLS-1$
        output = ma.generateMaskedRow(input);
        assertEquals(output, "8 rue XX XXXXXX"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        String input = "not an address"; //$NON-NLS-1$
        output = ma.generateMaskedRow(input);
        assertEquals(output, "XXX XX XXXXXXX"); //$NON-NLS-1$
    }

}
