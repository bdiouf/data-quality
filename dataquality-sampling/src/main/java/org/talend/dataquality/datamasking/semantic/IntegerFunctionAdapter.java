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
