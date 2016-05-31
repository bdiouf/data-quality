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
package org.talend.dataquality.datamasking.semantic;

import org.talend.dataquality.datamasking.functions.Function;

public class IntegerFunctionAdapter extends Function<String> {

    private static final long serialVersionUID = -2845447810365033162L;

    private Function<Integer> function;

    public IntegerFunctionAdapter(Function<Integer> functionToAdapt) {
        function = functionToAdapt;
    }

    @Override
    protected String doGenerateMaskedField(String input) {
        if (input == null || EMPTY_STRING.equals(input.trim())) {
            return input;
        }
        try {
            final Integer inputInt = Integer.parseInt(input);
            final Integer result = function.generateMaskedRow(inputInt);
            return String.valueOf(result);
        } catch (NumberFormatException nfe) {
            return input;
        }
    }

}
