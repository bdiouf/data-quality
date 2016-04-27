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
import org.talend.dataquality.datamasking.functions.GenerateFromListLong;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateFromListLongTest {

    private String output;

    private GenerateFromListLong gfll = new GenerateFromListLong();

    @Before
    public void setUp() throws Exception {
        gfll.setRandomWrapper(new RandomWrapper(42));
        gfll.parameters = "101, 11, 0".split(","); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGood() {
        output = gfll.generateMaskedRow(null).toString();
        assertEquals(output, "0"); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gfll.keepNull = true;
        output = gfll.generateMaskedRow(0L).toString();
        assertEquals(output, "0"); //$NON-NLS-1$
    }
}
