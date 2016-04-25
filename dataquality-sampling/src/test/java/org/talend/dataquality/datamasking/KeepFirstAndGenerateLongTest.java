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
import org.talend.dataquality.datamasking.functions.KeepFirstAndGenerateLong;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class KeepFirstAndGenerateLongTest {

    private String output;

    private Long input = 123456L;

    private KeepFirstAndGenerateLong kfag = new KeepFirstAndGenerateLong();

    @Before
    public void setUp() throws Exception {
        kfag.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        kfag.integerParam = 3;
        output = kfag.generateMaskedRow(input).toString();
        assertEquals(output, "123830"); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        kfag.integerParam = 7;
        output = kfag.generateMaskedRow(input).toString();
        assertEquals(output, input.toString());
    }
}
