package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberGermany extends AbstractGenerateUniquePhoneNumber {

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();

        strs.add(str.substring(str.length() - 8, str.length()));

        StringBuilder result = phoneNumberPattern.generateUniqueString(strs);
        if (result == null) {
            return null;
        }
        result.insert(0, str.substring(0, str.length() - 8));
        return result;
    }

    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<AbstractField>();
        fields.add(new FieldInterval(0, 99999999));
        return fields;
    }

}
