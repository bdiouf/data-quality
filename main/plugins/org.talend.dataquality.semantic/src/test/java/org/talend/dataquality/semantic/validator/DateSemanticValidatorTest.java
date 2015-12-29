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
package org.talend.dataquality.semantic.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.validator.impl.DateSemanticValidator;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class DateSemanticValidatorTest {

    private DateSemanticValidator validator;

    @Before
    public void init() {
    }

    @Test
    public void testEmptyString() throws Exception {
        validator = new DateSemanticValidator();
        assertFalse(validator.isValid(""));
        assertFalse(validator.isValid(null));
    }

    @Test
    public void testValidDateUS() throws Exception {
        Locale.setDefault(new Locale("en", "US"));
        validator = new DateSemanticValidator();
        assertTrue(validator.isValid("2/18/1901"));
        assertTrue(validator.isValid("1970-01-01"));
        assertTrue(validator.isValid("1970/01/01"));
        assertTrue(validator.isValid("January 26, 1969 at 3:21 PM"));
        assertTrue(validator.isValid("26-jan-1969 at 3:21:00.123 pm"));
        assertTrue(validator.isValid("1969-01-26 15:21:00.123456 PM PST"));
        assertTrue(validator.isValid("This 4th day of July, 1776"));
        assertTrue(validator.isValid("11 Oct 16"));

        assertTrue(validator.isValid("1/26/1969"));
        assertTrue(validator.isValid("1/26/69"));

        assertTrue(validator.isValid("26/1/1969"));
        assertTrue(validator.isValid("26/1/69"));

        assertFalse(validator.isValid("12"));
        assertFalse(validator.isValid("13"));
        assertFalse(validator.isValid("aaaaa"));
        assertFalse(validator.isValid("9 rue pages, 92150 suresnes"));

        assertFalse(validator.isValid("value5"));
        assertFalse(validator.isValid("29/2/2015"));
        assertFalse(validator.isValid("2/29/2015"));
        assertFalse(validator.isValid("4/31/2015"));
        assertFalse(validator.isValid("31/4/2015"));
        assertFalse(validator.isValid("5/32/2015"));
        assertFalse(validator.isValid("32/5/2015"));
    }

    @Test
    public void testValidDateFR() throws Exception {
        Locale.setDefault(new Locale("fr", "FR"));
        validator = new DateSemanticValidator();
        assertTrue(validator.isValid("2/18/1901"));
        assertTrue(validator.isValid("1970-01-01"));
        assertTrue(validator.isValid("1970/01/01"));
        assertTrue(validator.isValid("January 26, 1969 at 3:21 PM"));
        assertTrue(validator.isValid("26-jan-1969 at 3:21:00.123 pm"));
        assertTrue(validator.isValid("1969-01-26 15:21:00.123456 PM PST"));
        assertTrue(validator.isValid("This 4th day of July, 1776"));
        assertTrue(validator.isValid("11 Oct 16"));

        assertTrue(validator.isValid("26/1/1969"));
        assertTrue(validator.isValid("26/1/69"));

        assertTrue(validator.isValid("1/26/1969"));
        assertTrue(validator.isValid("1/26/69"));

        assertFalse(validator.isValid("12"));
        assertFalse(validator.isValid("13"));
        assertFalse(validator.isValid("aaaaa"));
        assertFalse(validator.isValid("9 rue pages, 92150 suresnes"));

        assertFalse(validator.isValid("value5"));
        assertFalse(validator.isValid("29/2/2015"));
        assertFalse(validator.isValid("2/29/2015"));
        assertFalse(validator.isValid("4/31/2015"));
        assertFalse(validator.isValid("31/4/2015"));
        assertFalse(validator.isValid("5/32/2015"));
        assertFalse(validator.isValid("32/5/2015"));
    }

    @Test
    public void testMixedDates() throws Exception {
        validator = new DateSemanticValidator();
        final InputStream stream = this.getClass().getResourceAsStream("dates.txt");
        assertNotNull(stream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            validator.isValid(line);
        }
    }
}
