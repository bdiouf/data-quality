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
 * created by jgonzalez on 24 juin 2015. See GenerateFromList.
 *
 */
public class GenerateFromListString extends GenerateFromList<String> {

    private static final long serialVersionUID = 5737608518316422798L;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, RandomWrapper rand) {
        super.parse(extraParameter, keepNullValues, rand);
        super.init();
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (StringTokens.size() > 0) {
            return StringTokens.get(rnd.nextInt(StringTokens.size()));
        } else {
            return EMPTY_STRING;
        }
    }
}
