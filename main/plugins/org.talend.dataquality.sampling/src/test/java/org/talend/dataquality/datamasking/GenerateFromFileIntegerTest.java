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

import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileInteger;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateFromFileIntegerTest {

    private String output;

    private String path = "/home/jgonzalez/Bureau/data/numbers.txt"; //$NON-NLS-1$

    private GenerateFromFileInteger gffi = new GenerateFromFileInteger();

    @Test
    public void testGood() {
        gffi.parse(path, false, new RandomWrapper(42));
        output = gffi.generateMaskedRow(0).toString();
        assertEquals(output, "10"); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gffi.parse(gffi.EMPTY_STRING, false, new RandomWrapper(42));
        output = gffi.generateMaskedRow(0).toString();
        assertEquals(output, "0"); //$NON-NLS-1$
    }
}
