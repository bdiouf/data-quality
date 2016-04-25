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
 * created by jgonzalez on 22 juin 2015. See ReplaceNumeric.
 *
 */
public class ReplaceNumericInteger extends ReplaceNumeric<Integer> implements Serializable {

    private static final long serialVersionUID = -6679442505476932276L;

    @Override
    protected Integer doGenerateMaskedField(Integer i) {
        if (i != null) {
            String res = i.toString();
            if (integerParam >= 0 && integerParam <= 9) {
                res = replacePattern(res, String.valueOf(integerParam));
            } else {
                throw new IllegalArgumentException("The parameter for \"replace all digits\" function must be a digit"); //$NON-NLS-1$
            }
            return Integer.valueOf(res);
        } else {
            return 0;
        }
    }
}
