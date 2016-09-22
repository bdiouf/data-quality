package org.talend.dataquality.datamasking.functions;

import java.util.List;
import java.util.Random;

/**
 * Created by jteuladedenantes on 21/09/16.
 */
public abstract class AbstractGenerateUniquePhoneNumber extends Function<String> {

    private static final long serialVersionUID = -2459692854626505777L;

    protected GenerateUniqueRandomPatterns phoneNumberPattern;

    public AbstractGenerateUniquePhoneNumber() {
        List<AbstractField> fields = createFieldsListFromPattern();
        phoneNumberPattern = new GenerateUniqueRandomPatterns(fields);
    }

    @Override
    public void setRandom(Random rand) {
        super.setRandom(rand);
        phoneNumberPattern.setKey(rand.nextInt() % 10000 + 1000);
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

    /**
     * @return the list of patterns for each field
     */
    protected abstract List<AbstractField> createFieldsListFromPattern();

    protected abstract StringBuilder doValidGenerateMaskedField(String str);
}
