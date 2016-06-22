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
 * return a new String that only contains the input elements that are between the bounds given as parameter.
 *
 */
public class BetweenIndexesKeep extends BetweenIndexes {

    private static final long serialVersionUID = 1913164034646800125L;

    @Override
    protected void initAttributes() {
        super.endIndex = Integer.valueOf(parameters[0]) - 1;
        super.toRemove = true;
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        String tAux = super.doGenerateMaskedField(str);
        if (tAux == EMPTY_STRING)
            return EMPTY_STRING;
        super.beginIndex = Integer.valueOf(parameters[1]) - 1;
        super.endIndex = Integer.MAX_VALUE;
        return super.doGenerateMaskedField(tAux);
    }

    @Override
    protected boolean validParameters() {
        return parameters.length == 2 && patternNumber.matcher(parameters[0]).matches()
                && patternNumber.matcher(parameters[1]).matches();
    }
}
