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
import org.talend.dataquality.datamasking.functions.ReplaceLastCharsLong;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 1 juil. 2015 Detailled comment
 *
 */
public class ReplaceLastCharsLongTest {

    private String output;

    private Long input = 123456L;

    private ReplaceLastCharsLong rlcl = new ReplaceLastCharsLong();

    @Before
    public void setUp() throws Exception {
        rlcl.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        rlcl.integerParam = 3;
        output = rlcl.generateMaskedRow(input).toString();
        assertEquals(output, "123830"); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        rlcl.integerParam = 7;
        output = rlcl.generateMaskedRow(input).toString();
        assertEquals(output, "830807"); //$NON-NLS-1$
    }

}
