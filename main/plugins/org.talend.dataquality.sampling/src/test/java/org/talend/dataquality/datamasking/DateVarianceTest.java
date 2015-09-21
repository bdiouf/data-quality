// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.DateVariance;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class DateVarianceTest {

    private DateVariance dv = new DateVariance();

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //$NON-NLS-1$

    private Date input;

    @Before
    public void setUp() throws Exception {
        input = sdf.parse("29-02-1992"); //$NON-NLS-1$
        dv.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        dv.parameters = "31".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        String output = sdf.format(dv.generateMaskedRow(input));
        assertEquals(output, "04-02-1992"); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        dv.parameters = "0".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        String output = sdf.format(dv.generateMaskedRow(input));
        assertEquals(output, "04-02-1992"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        dv.parameters = "j".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        String output = sdf.format(dv.generateMaskedRow(input));
        assertEquals(output, "04-02-1992"); //$NON-NLS-1$
    }
}
