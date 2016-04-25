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

/**
 * created by jgonzalez on 22 juin 2015. This class is used when the requested function is BetweenIndexesKeep. It will
 * return a new String that will contain all the input elements but those which are between the bounds given as
 * parameter.
 *
 */
public class BetweenIndexesRemove extends BetweenIndexes {

    private static final long serialVersionUID = -198474878692287672L;

    @Override
    protected String doGenerateMaskedField(String str) {
        if (super.check(str, 2)) {
            super.setBounds(str);
            return str.substring(0, begin - 1) + str.substring(end, str.length());
        } else {
            return EMPTY_STRING;
        }
    }
}
