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

import org.junit.Test;
import org.talend.dataquality.datamasking.functions.ReplaceAll;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceAllTest {

    private String output;

    private String input = "input value"; //$NON-NLS-1$

    private ReplaceAll ra = new ReplaceAll();

    @Test
    public void testGood() {
        ra.parameters = "X".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = ra.generateMaskedRow(input);
        assertEquals(output, "XXXXXXXXXXX"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        ra.parameters = "8X".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = ra.generateMaskedRow(input);
        assertEquals(output, ""); //$NON-NLS-1$
    }
}
