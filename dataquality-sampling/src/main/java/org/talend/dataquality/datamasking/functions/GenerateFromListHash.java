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
 * created by jgonzalez on 24 juin 2015. This function works like GenerateFromList, the only difference is that it will
 * use the hashCode() function provided by Java to choose an element from the list. When having the hashCode, we apply a
 * modulo according to the number of elements in the list.
 *
 */
public abstract class GenerateFromListHash<T> extends Function<T> {

    private static final long serialVersionUID = 8813074434737742166L;

    protected List<T> genericTokens = new ArrayList<T>();

    protected abstract void init();

    protected abstract T getDefaultOutput();

    @Override
    protected T doGenerateMaskedField(T i) {
        if (genericTokens.size() > 0) {
            if (i == null) {
                return genericTokens.get(rnd.nextInt(genericTokens.size()));
            } else {
                return genericTokens.get(Math.abs(i.hashCode() % genericTokens.size()));
            }
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
