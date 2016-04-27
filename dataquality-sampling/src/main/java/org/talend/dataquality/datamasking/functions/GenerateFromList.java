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

/**
 * created by jgonzalez on 24 juin 2015. This function will modify the input by randomly selecting one of the values
 * given as parameter.
 *
 */
public abstract class GenerateFromList<T2> extends Function<T2> {

    private static final long serialVersionUID = 8936060786451303843L;

    protected List<String> StringTokens = new ArrayList<>();

    protected void init() {
        StringTokens.clear();
        for (String tmp : parameters) {
            StringTokens.add(tmp.trim());
        }
    }

}
