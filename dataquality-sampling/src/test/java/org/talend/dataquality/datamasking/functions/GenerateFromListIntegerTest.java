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
import org.talend.dataquality.datamasking.functions.GenerateFromListInteger;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateFromListIntegerTest {

    private String output;

    private GenerateFromListInteger gfli = new GenerateFromListInteger();

    @Before
    public void setUp() throws Exception {
        gfli.setRandomWrapper(new RandomWrapper(42));
        gfli.parameters = "101, 11, 0".split(","); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGood() {
        output = gfli.generateMaskedRow(null).toString();
        assertEquals(output, "0"); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gfli.keepNull = true;
        output = gfli.generateMaskedRow(0).toString();
        assertEquals(output, "0");
    }
}
