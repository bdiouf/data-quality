package org.talend.dataquality.datamasking.functions;

import java.util.regex.Pattern;

public final class CharactersOperationUtils {

    protected static final Pattern patternNumber = Pattern.compile("[0-9]+");

    protected static final Pattern patternCharacter = Pattern.compile(".");

    protected static final Pattern patternDigit = Pattern.compile("[0-9]");

    public final static boolean validParameters2Indexes(String[] parameters) {
        return parameters.length == 2 && patternNumber.matcher(parameters[0]).matches()
                && patternNumber.matcher(parameters[1]).matches();
    }

    public final static boolean validParameters2Indexes1CharReplace(String[] parameters) {
        return (parameters.length == 2 || (parameters.length == 3 && patternCharacter.matcher(parameters[2]).matches()))
                && patternNumber.matcher(parameters[0]).matches() && patternNumber.matcher(parameters[1]).matches();
    }

    public final static boolean validParameters1Number1DigitReplace(String[] parameters) {
        return (parameters.length == 1 || (parameters.length == 2 && patternDigit.matcher(parameters[1]).matches()))
                && patternNumber.matcher(parameters[0]).matches();
    }

    public final static boolean validParameters1Number1CharReplace(String[] parameters) {
        return (parameters.length == 1 || (parameters.length == 2 && patternCharacter.matcher(parameters[1]).matches()))
                && patternNumber.matcher(parameters[0]).matches();
    }

    public final static boolean validParameters1Number(String[] parameters) {
        return parameters.length == 1 && patternNumber.matcher(parameters[0]).matches();
    }

    public final static boolean validParameters1DigitReplace(String[] parameters) {
        return parameters.length == 0 || (parameters.length == 1 && patternDigit.matcher(parameters[0]).matches());
    }

    public final static boolean validParameters1CharReplace(String[] parameters) {
        return parameters.length == 0 || (parameters.length == 1 && patternCharacter.matcher(parameters[0]).matches());
    }

}
