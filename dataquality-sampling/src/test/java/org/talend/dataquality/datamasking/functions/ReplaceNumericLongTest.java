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
import org.talend.dataquality.datamasking.functions.ReplaceNumericLong;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceNumericLongTest {

    private long input = 123;

    private long output;

    private ReplaceNumericLong rnl = new ReplaceNumericLong();

    @Test
    public void testGood() {
        rnl.parameters = "6".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        rnl.integerParam = 6;
        output = rnl.generateMaskedRow(input);
        assertEquals(output, 666);
    }

    @Test
    public void testBad() {
        rnl.integerParam = 10;
        rnl.rnd = new RandomWrapper(42);
        try {
            output = rnl.generateMaskedRow(input);
            fail("should get exception with input " + rnl.integerParam); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
    }
}
