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
public class ReplaceNumericFloat extends ReplaceNumeric<Float> implements Serializable {

    private static final long serialVersionUID = -2936953156402359732L;

    @Override
    public Float generateMaskedRow(Float f) {
        if (f == null && keepNull) {
            return null;
        } else {
            if (f != null) {
                String res = f.toString();
                if (integerParam >= 0 && integerParam <= 9) {
                    res = res.replaceAll("\\d", String.valueOf(integerParam)); //$NON-NLS-1$
                } else {
                    throw new IllegalArgumentException("The parameter for \"replace all digits\" function must be a digit"); //$NON-NLS-1$
                }
                return Float.valueOf(res);
            } else {
                return 0.0f;
            }
        }
    }
}
