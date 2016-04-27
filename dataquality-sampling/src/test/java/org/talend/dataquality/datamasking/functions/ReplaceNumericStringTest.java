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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.talend.dataquality.datamasking.functions.ReplaceNumericString;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceNumericStringTest {

    private String input = "abc123def"; //$NON-NLS-1$

    private String output;

    private ReplaceNumericString rns = new ReplaceNumericString();

    @Test
    public void testGood() {
        rns.parameters = "0".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = rns.generateMaskedRow(input);
        assertEquals(output, "abc000def"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        rns.parameters = "0X".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        rns.rnd = new RandomWrapper(42);
        try {
            output = rns.generateMaskedRow(input);
            fail("should get exception with input " + rns.integerParam); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
    }
}
