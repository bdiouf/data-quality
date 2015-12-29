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
package org.talend.dataquality.datamasking.Functions;

import java.io.Serializable;

/**
 * created by jgonzalez on 22 juin 2015. See ReplaceNumeric.
 *
 */
public class ReplaceNumericString extends ReplaceNumeric<String> implements Serializable {

    private static final long serialVersionUID = 8707035612963121276L;

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            if (str != null && !EMPTY_STRING.equals(str)) {
                if (parameters[0].matches("[0-9]|[a-zA-Z]| ")) { //$NON-NLS-1$
                    if ((" ").equals(parameters[0])) { //$NON-NLS-1$
                        return str.replaceAll("\\d", parameters[0]).replace(" ", EMPTY_STRING); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        return str.replaceAll("\\d", parameters[0]); //$NON-NLS-1$
                    }
                } else {
                    throw new IllegalArgumentException(
                            "The parameter for \"replace all digits\" function must be a digit or a letter"); //$NON-NLS-1$
                }
            } else {
                return EMPTY_STRING;
            }
        }
    }
}
