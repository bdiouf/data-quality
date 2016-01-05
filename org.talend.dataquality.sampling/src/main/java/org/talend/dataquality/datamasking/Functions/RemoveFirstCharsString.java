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
 * created by jgonzalez on 22 juin 2015. See RemoveFirstChars.
 *
 */
public class RemoveFirstCharsString extends RemoveFirstChars<String> implements Serializable {

    private static final long serialVersionUID = 9025405180061771063L;

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0 && integerParam < str.length()) {
                return str.substring(integerParam);
            } else {
                return EMPTY_STRING;
            }
        }
    }
}
