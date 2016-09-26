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
package org.talend.dataquality.semantic.validator.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by vlesquere on 24/06/16.
 */
public class USPhoneNumberValidatorTest {

    private USPhoneNumberValidator validator;

    @Before
    public void setUp() {
        validator = new USPhoneNumberValidator();
    }

    @Test
    public void validPhoneNumber() {
        Assert.assertTrue(validator.isValid("+1-541-754-3010"));
        Assert.assertTrue(validator.isValid("1-541-754-3010"));
        Assert.assertTrue(validator.isValid("(541) 754-3010"));
        Assert.assertTrue(validator.isValid("(724) 203-2300"));
    }

    @Test
    public void invalidPhoneNumber() {
        Assert.assertFalse(validator.isValid("001-541-754-3010"));
        Assert.assertFalse(validator.isValid("74-3010"));
        Assert.assertFalse(validator.isValid("191 541 754 3010"));

        Assert.assertFalse(validator.isValid("19-49-89-636-48018"));
        Assert.assertFalse(validator.isValid("000147554323"));
        Assert.assertFalse(validator.isValid("00(0)147554323"));
    }
}
