package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jteuladedenantes on 21/09/16.
 */
public class GenerateUniquePhoneNumberUs extends AbstractGenerateUniquePhoneNumber {

    private static final long serialVersionUID = 3821280930509201884L;

    @Override
    protected int getDigitsNumberToMask() {
        return 6;
    }

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(str.length() - 6, str.length() - 4));
        strs.add(str.substring(str.length() - 4, str.length()));

        StringBuilder result = phoneNumberPattern.generateUniqueString(strs);
        if (result == null) {
            return null;
        }
        result.insert(0, str.substring(0, str.length() - 6));
        return result;
    }

    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();

        List<String> secondAndThirdDigits = new ArrayList<String>();
        String tmp;
        for (int i = 0; i <= 9; i++) {
            tmp = String.valueOf(i);
            for (int j = 0; j <= 9; j++)
                if (!(i == 1 && j == 1))
                    secondAndThirdDigits.add(tmp + j);
        }

        fields.add(new FieldEnum(secondAndThirdDigits, 2));

        fields.add(new FieldInterval(0, 9999));

        return fields;
    }
}
