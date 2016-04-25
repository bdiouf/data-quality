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
 * created by jgonzalez on 22 juin 2015. See RemoveLastChars.
 *
 */
public class RemoveLastCharsLong extends RemoveLastChars<Long> implements Serializable {

    private static final long serialVersionUID = 3563086153882260632L;

    @Override
    protected Long doGenerateMaskedField(Long l) {
        if (l != null) {
            Double extraP = Double.valueOf(integerParam.toString());
            if ((int) Math.log10(l) + 1 > extraP && extraP > 0) {
                return l / (long) Math.pow(10.0, extraP);
            } else {
                return 0L;
            }
        } else {
            return 0L;
        }
    }
}
