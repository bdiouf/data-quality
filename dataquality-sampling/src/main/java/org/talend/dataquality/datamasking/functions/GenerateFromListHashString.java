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

import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 24 juin 2015. See GenerateFromListHash.
 *
 */
public class GenerateFromListHashString extends GenerateFromListHash<String> implements Serializable {

    private static final long serialVersionUID = -6003768593059919308L;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, RandomWrapper rand) {
        super.parse(extraParameter, keepNullValues, rand);
        super.init();
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (StringTokens.size() > 0) {
            if (str == null || EMPTY_STRING.equals(str)) {
                return StringTokens.get(rnd.nextInt(StringTokens.size()));
            } else {
                return StringTokens.get(Math.abs(str.hashCode()) % StringTokens.size());
            }
        } else {
            return EMPTY_STRING;
        }
    }
}
