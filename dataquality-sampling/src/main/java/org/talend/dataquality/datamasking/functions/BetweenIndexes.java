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

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 22 juin 2015. This class is used when the requested function is either BetweenIndexesKeep,
 * BetweenIndexesRemove or BetweenIndexesReplace. It will set the bounds of the indexes according to the input length.
 *
 */
public abstract class BetweenIndexes extends Function<String> {

    private static final long serialVersionUID = 1114307514352123034L;

    protected static int begin = 0;

    protected static int end = 0;

    protected boolean check(String str, int length) {
        return (parameters.length == length && str != null && !EMPTY_STRING.equals(str));
    }

    protected void setBounds(String str) {
        int a = 0, b = 0;
        try {
            a = Integer.valueOf(parameters[0].trim());
            b = Integer.valueOf(parameters[1].trim());
        } catch (NumberFormatException e) {
            a = 0;
            b = 0;
        }
        begin = (a < b) ? a : b;
        end = (a > b) ? a : b;

        if (begin < 1) {
            begin = 1;
        }
        if (end > str.length()) {
            end = str.length();
        }
    }

    @Override
    protected abstract String doGenerateMaskedField(String t);
}
