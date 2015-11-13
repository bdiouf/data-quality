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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileHashString;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateFromFileHashStringTest {

    private String output;

    private String path = GenerateFromFileStringTest.path;

    private String pathWin = GenerateFromFileStringTest.pathWin;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGood() {
        GenerateFromFileHashString gffhs = new GenerateFromFileHashString();
        gffhs.parse(path, false, new RandomWrapper(42));
        output = gffhs.generateMaskedRow(null);
        assertEquals("Brad X", output); //$NON-NLS-1$
    }

    @Test
    public void testSeparatorWin() {
        GenerateFromFileHashString gffhs = new GenerateFromFileHashString();
        gffhs.parse(pathWin, false, new RandomWrapper(42));
        int runTimes = 10000;
        String[] keysArray = new String[] { "Brad X", "Marouane", "Matthieu", "Xavier", "Aymen" };
        while (runTimes > 0) {
            output = gffhs.generateMaskedRow(null);
            assertTrue(ArrayUtils.contains(keysArray, output)); //$NON-NLS-1$
            runTimes--;
        }
    }

    @Test
    public void testNull() {
        GenerateFromFileHashString gffhs = new GenerateFromFileHashString();
        gffhs.parse(path, false, new RandomWrapper(42));
        gffhs.keepNull = true;
        output = gffhs.generateMaskedRow(null);
        assertEquals(null, output);
    }
}
