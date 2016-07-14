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

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class BetweenIndexesKeepTest {

    private BetweenIndexesKeep bik = new BetweenIndexesKeep();

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    @Test
    public void testGood() {
        bik.parse("2, 4", false, new RandomWrapper(42));
        output = bik.generateMaskedRow(input);
        assertEquals("tev", output); //$NON-NLS-1$
    }

    @Test
    public void testGood2() {
        bik.parse("1, 2", false, new RandomWrapper(42));
        output = bik.generateMaskedRow(input);
        assertEquals("St", output); //$NON-NLS-1$
    }

    @Test
    public void testGoodForStringIndexOutOfBoundsException() {
        bik.parse("1, 3", false, new RandomWrapper(42)); //$NON-NLS-1$
        output = bik.generateMaskedRow("kent"); //$NON-NLS-1$
        assertEquals("ken", output); //$NON-NLS-1$
        output = bik.generateMaskedRow("kent"); //$NON-NLS-1$
        assertEquals("ken", output); //$NON-NLS-1$
    }

    @Test
    public void testWrongParameter() {
        try {
            bik.parse("0, 8", false, new RandomWrapper(42));
            fail("should get exception with input " + bik.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bik.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        try {
            bik.parse("1", false, new RandomWrapper(42));
            fail("should get exception with input " + bik.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bik.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad2() {
        try {
            bik.parse("lk, df", false, new RandomWrapper(42));
            fail("should get exception with input " + bik.parameters); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = bik.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyParameters() {
        bik.parse("423,452", false, new RandomWrapper(42));
        output = bik.generateMaskedRow(input);
        assertEquals("", output);
    }
}
