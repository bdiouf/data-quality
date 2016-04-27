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

import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 24 juin 2015. This function works like GenerateFromFile, the only difference is that it will
 * use the hashCode() function provided by Java to choose an element from the list. When having the hashCode, we apply a
 * modulo according to the number of elements in the list.
 *
 */
public abstract class GenerateFromFileHash<T2> extends GenerateFromFile<T2> {

    private static final long serialVersionUID = -4616169672287269594L;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, RandomWrapper rand) {
        super.parse(extraParameter, keepNullValues, rand);
        super.init();
    }
}
