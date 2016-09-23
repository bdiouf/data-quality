package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jteuladedenantes on 21/09/16.
 */
public abstract class AbstractGenerateUniquePhoneNumber extends Function<String> {

    private static final long serialVersionUID = -2459692854626505777L;

    private GenerateUniqueRandomPatterns phoneNumberPattern;

    public AbstractGenerateUniquePhoneNumber() {
        List<AbstractField> fields = createFieldsListFromPattern();
        phoneNumberPattern = new GenerateUniqueRandomPatterns(fields);
    }

    @Override
    public void setRandom(Random rand) {
        super.setRandom(rand);
        phoneNumberPattern.setKey(Math.abs(rand.nextInt()) % 10000 + 1000);
    }

    @Override
    protected String doGenerateMaskedField(String str) {

        if (str == null)
            return null;

        String strWithoutSpaces = super.removeFormatInString(str);
        // check if the pattern is valid
        if (strWithoutSpaces.isEmpty() || strWithoutSpaces.length() < phoneNumberPattern.getFieldsCharsLength()) {
            if (keepInvalidPattern)
                return str;
            else
                return null;
        }

        StringBuilder result = doValidGenerateMaskedField(strWithoutSpaces);
        if (result == null) {
            if (keepInvalidPattern)
                return str;
            else
                return null;
        }
        if (keepFormat)
            return insertFormatInString(str, result);
        else
            return result.toString();
    }

    private List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();
        long max = (long) Math.pow(10, getDigitsNumberToMask()) - 1;
        fields.add(new FieldInterval(0, max));
        return fields;
    }

    private StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();

        strs.add(str.substring(str.length() - getDigitsNumberToMask(), str.length()));

        StringBuilder result = phoneNumberPattern.generateUniqueString(strs);
        if (result == null) {
            return null;
        }
        result.insert(0, str.substring(0, str.length() - getDigitsNumberToMask()));
        return result;
    }

    protected abstract int getDigitsNumberToMask();
}
