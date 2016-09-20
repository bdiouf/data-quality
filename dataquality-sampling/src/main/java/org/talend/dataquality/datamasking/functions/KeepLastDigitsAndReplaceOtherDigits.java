package org.talend.dataquality.datamasking.functions;

import java.util.Random;

public class KeepLastDigitsAndReplaceOtherDigits extends Function<String> {

    private int integerParam;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        try {
            integerParam = Integer.parseInt(parameters[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The parameter " + parameters[0] + " is not an integer.");
        }
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
            String s = str.trim();
            StringBuilder sb = new StringBuilder(EMPTY_STRING);
            if (integerParam > s.length()) {
                return str;
            }
            if (integerParam < s.length()) {
                StringBuilder end = new StringBuilder(EMPTY_STRING);
                for (int i = s.length() - 1; i >= s.length() - integerParam; --i) {
                    if (i < 0) {
                        break;
                    }
                    end.append(s.charAt(i));
                    if (!Character.isDigit(s.charAt(i))) {
                        integerParam++;
                    }
                }
                for (int i = 0; i < s.length() - integerParam; ++i) {
                    if (i < 0) {
                        break;
                    }
                    if (Character.isDigit(s.charAt(i))) {
                        sb.append(rnd.nextInt(9));
                    } else {
                        sb.append(s.charAt(i));
                    }
                }
                sb.append(end.reverse());
                return sb.toString();
            }
        }
        return EMPTY_STRING;
    }

}
