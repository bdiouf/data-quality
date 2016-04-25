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
import org.talend.dataquality.datamasking.functions.RemoveLastCharsLong;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class RemoveLastCharsLongTest {

    private long input = 666L;

    private long output;

    private RemoveLastCharsLong rlci = new RemoveLastCharsLong();

    @Test
    public void test() {
        rlci.integerParam = 2;
        output = rlci.generateMaskedRow(input);
        assertEquals(output, 6);
    }

    @Test
    public void testDummyGood() {
        rlci.integerParam = 10;
        output = rlci.generateMaskedRow(input);
        assertEquals(output, 0);
    }

}
