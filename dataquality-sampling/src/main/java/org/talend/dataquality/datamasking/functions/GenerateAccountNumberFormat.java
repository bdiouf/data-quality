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

import java.io.Serializable;

/**
 * created by jgonzalez on 19 juin 2015. This function produces a correct account number and tries to keep the country
 * where it's from.
 *
 */
public class GenerateAccountNumberFormat extends GenerateAccountNumber implements Serializable {

    private static final long serialVersionUID = 116648954835024228L;

    @Override
    protected String doGenerateMaskedField(String str) {
        boolean keepFormat = ("true").equals(parameters[0]); //$NON-NLS-1$
        String accountNumberFormat = EMPTY_STRING;
        if (str != null && str.length() > 9 && !EMPTY_STRING.equals(str)) {
            try {
                accountNumberFormat = super.generateIban(str, keepFormat);
            } catch (NumberFormatException e) {
                accountNumberFormat = super.generateIban();
            }
        } else {
            accountNumberFormat = super.generateIban();
        }
        return accountNumberFormat;

    }

}
