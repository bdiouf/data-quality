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
import org.talend.dataquality.datamasking.functions.GenerateAccountNumberSimple;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateAccountNumberSimpleTest {

    private String output;

    private GenerateAccountNumberSimple gans = new GenerateAccountNumberSimple();

    @Before
    public void setUp() throws Exception {
        gans.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = gans.generateMaskedRow(gans.EMPTY_STRING);
        assertEquals(output, "FR54 0384 0558 93A2 20ZR 3V86 K48"); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gans.keepNull = true;
        output = gans.generateMaskedRow(null);
        assertEquals(output, null);
    }

}
