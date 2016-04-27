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
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceCharactersTest {

    private String output;

    private String input = "input value"; //$NON-NLS-1$

    private ReplaceCharacters rc = new ReplaceCharacters();

    @Before
    public void setUp() throws Exception {
        rc.rnd = new RandomWrapper(42);
    }

    @Test
    public void testGood() {
        rc.parameters = "X".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = rc.generateMaskedRow(input);
        assertEquals(output, "XXXXX XXXXX"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        rc.parameters = Function.EMPTY_STRING.split(","); //$NON-NLS-1$
        output = rc.generateMaskedRow(input);
        assertEquals(output, "AAAAA AAAAA"); //$NON-NLS-1$
    }

}
