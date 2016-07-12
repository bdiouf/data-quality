package org.talend.dataquality.datamasking.functions;

import java.util.regex.Pattern;

public final class CharactersOperationUtils {

    private CharactersOperationUtils() {
    }

    protected static final Pattern patternNumber = Pattern.compile("[0-9]+");

    protected static final Pattern patternCharacter = Pattern.compile(".");

    protected static final Pattern patternDigit = Pattern.compile("[0-9]");

    private final static boolean factorise(String[] parameters, int length, Pattern pattern) {
        return parameters.length == length && pattern.matcher(parameters[length - 1]).matches();
    }

    private final static boolean factorise2Indexes(String[] parameters) {
        return patternNumber.matcher(parameters[0]).matches() && patternNumber.matcher(parameters[1]).matches();
    }

    public final static boolean validParameters2Indexes(String[] parameters) {
        return parameters.length == 2 && factorise2Indexes(parameters);
    }

    public final static boolean validParameters2Indexes1CharReplace(String[] parameters) {
        return (parameters.length == 2 || factorise(parameters, 3, patternCharacter)) && factorise2Indexes(parameters);
    }

    public final static boolean validParameters1Number1DigitReplace(String[] parameters) {
        return (parameters.length == 1 || factorise(parameters, 2, patternDigit))
                && patternNumber.matcher(parameters[0]).matches();
    }

    public final static boolean validParameters1Number1CharReplace(String[] parameters) {
        return (parameters.length == 1 || factorise(parameters, 2, patternCharacter))
                && patternNumber.matcher(parameters[0]).matches();
    }

    public final static boolean validParameters1Number(String[] parameters) {
        return factorise(parameters, 1, patternNumber);
    }

    public final static boolean validParameters1DigitReplace(String[] parameters) {
        return parameters.length == 0 || factorise(parameters, 1, patternDigit);
    }

    public final static boolean validParameters1CharReplace(String[] parameters) {
        return parameters.length == 0 || factorise(parameters, 1, patternCharacter);
    }

}
