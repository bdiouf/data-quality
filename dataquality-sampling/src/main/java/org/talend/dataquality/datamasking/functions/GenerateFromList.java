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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * created by jgonzalez on 24 juin 2015. This function will modify the input by randomly selecting one of the values
 * given as parameter.
 *
 */
public abstract class GenerateFromList<T> extends Function<T> {

    private static final long serialVersionUID = 8936060786451303843L;

    protected List<T> genericTokens = new ArrayList<>();

    protected abstract void init();

    protected abstract T getDefaultOutput();

    @Override
    protected T doGenerateMaskedField(T i) {
        if (genericTokens.size() > 0) {
            return genericTokens.get(rnd.nextInt(genericTokens.size()));
        } else {
            return getDefaultOutput();
        }
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        this.init();
    }

}
