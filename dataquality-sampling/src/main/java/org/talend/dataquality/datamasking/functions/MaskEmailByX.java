package org.talend.dataquality.datamasking.functions;

public abstract class MaskEmailByX extends MaskEmail {

    /**
     * Three conditions in masking-email domain by x<br>
     * <ul>
     * <li>if the user inputs nothing, the full email domain will be masked by character X</li>
     * <li>if the user inputs a character, the full email domain will be masked by this character</li>
     * <li>if the user's inputs something inappropriate, the full email domain will be masked by character X</li>
     * </ul>
     */
    @Override
    protected String doGenerateMaskedField(String str) {

        if (str == null || str.isEmpty()) {
            return EMPTY_STRING;
        }
        if (isValidEmailAddress(str)) {
            return maskEmailByX(str);
        }
        return str;
    }

    protected Character getMaskingCharacter() {
        String replacement = (parameters.length == 1) ? parameters[0] : null;
        return (replacement != null && replacement.length() == 1 && Character.isLetter(replacement.charAt(0)))
                ? replacement.charAt(0) : 'X';
    }

    protected abstract String maskEmailByX(String str);
}
