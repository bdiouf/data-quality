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
public class ReplaceNumericLong extends ReplaceNumeric<Long> implements Serializable {

    private static final long serialVersionUID = -2539616719332426704L;

    @Override
    public Long generateMaskedRow(Long l) {
        if (l == null && keepNull) {
            return null;
        } else {
            if (l != null) {
                String res = l.toString();
                if (integerParam >= 0 && integerParam <= 9) {
                    res = res.replaceAll("\\d", String.valueOf(integerParam)); //$NON-NLS-1$
                } else {
                    throw new IllegalArgumentException("The parameter for \"replace all digits\" function must be a digit"); //$NON-NLS-1$
                }
                return Long.valueOf(res);
            } else {
                return 0L;
            }
        }
    }

}
