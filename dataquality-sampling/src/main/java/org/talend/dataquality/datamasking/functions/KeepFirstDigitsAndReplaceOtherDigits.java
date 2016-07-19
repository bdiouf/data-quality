package org.talend.dataquality.datamasking.functions;

import java.util.Random;

public class KeepFirstDigitsAndReplaceOtherDigits extends Function<String> {

    private int integerParam;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        try {
            integerParam = Integer.parseInt(parameters[0]);
        } catch (Exception e) {
            throw new IllegalArgumentException("The parameter " + parameters[0] + " is not an integer.");
        }
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder sb = new StringBuilder(EMPTY_STRING);
        if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
            String s = str.trim();
            int totalDigit = 0;
            for (int i = 0; i < s.length(); ++i) {
                if (Character.isDigit(s.charAt(i))) {
                    totalDigit++;
                }
            }
            if (integerParam > totalDigit) {
                return str;
            }
            for (int i = 0; i < integerParam; ++i) {
                sb.append(s.charAt(i));
                if (!Character.isDigit(s.charAt(i))) {
                    integerParam++;
                }
            }
            for (int i = integerParam; i < s.length(); ++i) {
                if (Character.isDigit(s.charAt(i))) {
                    sb.append(rnd.nextInt(9));
                } else {
                    sb.append(s.charAt(i));
                }
            }
        }
        return sb.toString();
    }

}
