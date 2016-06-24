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
package org.talend.dataquality.semantic.validator;

import java.util.Locale;

import org.talend.dataquality.standardization.phone.PhoneNumberHandler;

public abstract class AbstractPhoneNumberValidator {

    private static PhoneNumberHandler handler = new PhoneNumberHandler();

    protected boolean isValidPhoneNumber(String phoneNumber, Locale locale) {
        return handler.isValidPhoneNumber(phoneNumber, locale.getCountry());
    }
}
