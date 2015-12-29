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
 * created by jgonzalez on 22 juil. 2015 Detailled comment
 *
 */
public class GenerateSsnJapan extends Function<String> implements Serializable {

    private static final long serialVersionUID = -8621894245597689328L;

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            StringBuilder result = new StringBuilder(EMPTY_STRING);
            for (int i = 0; i < 12; ++i) {
                result.append(rnd.nextInt(9));
            }
            return result.toString();
        }
    }

}
