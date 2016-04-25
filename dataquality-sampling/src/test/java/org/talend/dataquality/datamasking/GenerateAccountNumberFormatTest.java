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
import org.talend.dataquality.datamasking.functions.GenerateAccountNumberFormat;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateAccountNumberFormatTest {

    private String output;

    private GenerateAccountNumberFormat ganf = new GenerateAccountNumberFormat();

    @Before
    public void setUp() throws Exception {
        ganf.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = ganf.generateMaskedRow("DK0125634987589632"); //$NON-NLS-1$
        assertEquals(output, "DK49 0384 0558 9322 62"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        output = ganf.generateMaskedRow("not an iban"); //$NON-NLS-1$
        assertEquals(output, "FR54 0384 0558 93A2 20ZR 3V86 K48"); //$NON-NLS-1$
    }
}
