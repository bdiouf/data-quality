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

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 22 juin 2015. This function will replace every character by the parameter.
 *
 */
public class ReplaceAll extends Function<String> implements Serializable {

    private static final long serialVersionUID = -6755455022090241272L;

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            if (str != null && !EMPTY_STRING.equals(str) && parameters[0].matches("[0-9]|[a-zA-Z]")) { //$NON-NLS-1$
                return str.replaceAll(".", parameters[0]); //$NON-NLS-1$
            } else {
                return EMPTY_STRING;
            }
        }
    }
}
