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

import org.junit.Test;
import org.talend.dataquality.datamasking.functions.RemoveLastCharsString;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class RemoveLastCharsStringTest {

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    private RemoveLastCharsString rlcs = new RemoveLastCharsString();

    @Test
    public void test() {
        rlcs.integerParam = 2;
        output = rlcs.generateMaskedRow(input);
        assertEquals(output, "Ste"); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        rlcs.integerParam = 10;
        output = rlcs.generateMaskedRow(input);
        assertEquals(output, ""); //$NON-NLS-1$
    }

}
