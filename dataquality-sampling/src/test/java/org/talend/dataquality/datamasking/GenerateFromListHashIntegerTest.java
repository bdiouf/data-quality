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
import org.talend.dataquality.datamasking.functions.GenerateFromListHashInteger;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateFromListHashIntegerTest {

    private String output;

    private GenerateFromListHashInteger gflhi = new GenerateFromListHashInteger();

    @Before
    public void setUp() throws Exception {
        gflhi.parse("101, 11, 0", false, new RandomWrapper(42)); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gflhi.generateMaskedRow(null).toString();
        assertEquals(output, "0"); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gflhi.keepNull = true;
        output = gflhi.generateMaskedRow(0).toString();
        assertEquals(output, "101"); //$NON-NLS-1$
    }

}
