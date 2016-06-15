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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * created by jgonzalez on 19 juin 2015. This function works like GenerateFromList, the difference is that the parameter
 * is now a String holding the path to a file in the user’s computer.
 *
 */
public abstract class GenerateFromFile<T> extends Function<T> {

    private static final long serialVersionUID = 1556057898878709265L;

    protected List<String> substituteList = new ArrayList<>();

    protected List<T> genericTokens = new ArrayList<T>();

    protected void init() {
        try {
            substituteList = KeysLoader.loadKeys(parameters[0]);
        } catch (IOException | NullPointerException e) {
            // We do nothing here because in is already set.
        }
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        init();
    }

    @Override
    protected T doGenerateMaskedField(T t) {
        if (genericTokens.size() > 0) {
            return genericTokens.get(rnd.nextInt(genericTokens.size()));
        } else {
            return getDefaultOutput();
        }
    }

    protected abstract T getDefaultOutput();
}
