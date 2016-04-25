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
 * created by jgonzalez on 19 juin 2015. See GenerateFromFile.
 *
 */

public class GenerateFromFileString extends GenerateFromFile<String> implements Serializable {

    private static final long serialVersionUID = 6360879458690229195L;

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
