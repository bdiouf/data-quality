// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * DOC jteuladedenantes class global comment. Detailled comment
 */
public class GenerateUniqueSsnFr extends Function<String> {

    private static final long serialVersionUID = 4514471121590047091L;

    private static final int MOD97 = 97; // $NON-NLS-1$

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str == null)
            return null;

        // TODO, is it useful ? to check with the studio
        str = str.replace(" ", "");

        // check if the pattern is valid with french ssn number
        if (str.isEmpty() || str.length() != 15) {
            if (keepUnvalidPattern)
                return str;
            else
                return null;
        }

        // fill each field according to the pattern
        List<Field> fields = createFieldsListFromFrPattern();
        // read the input str
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(0, 1));
        strs.add(str.substring(1, 3));
        strs.add(str.substring(3, 5));
        strs.add(str.substring(5, 7));
        strs.add(str.substring(7, 10));
        strs.add(str.substring(10, 13));

        StringBuilder result = GenerateUniqueRandomNumbers.generateUniqueString(strs, fields, this.rnd.nextInt(9000) + 1000);
        if (result == null) {
            if (keepUnvalidPattern)
                return str;
            else
                return null;
        }

        // add the security key specified for french SSN
        String keyResult = (new String(result)).replaceAll("2A", "19").replaceAll("2B", "18");
        int controlKey = 97 - (int) (Long.valueOf(keyResult) % MOD97);

        result.append(" " + controlKey);
        return result.toString();
    }

    List<Field> createFieldsListFromFrPattern() {
        List<Field> fields = new ArrayList<Field>();
        fields.add(new FieldInterval(1, 2));
        fields.add(new FieldInterval(0, 99));
        fields.add(new FieldInterval(1, 12));
        List<String> departments = new ArrayList<String>();
        for (int department = 1; department <= 99; department++) {
            if (department < 10)
                departments.add("0" + String.valueOf(department));
            else if (department == 20) {
                departments.add("2A");
                departments.add("2B");
            } else
                departments.add(String.valueOf(department));
        }
        fields.add(new FieldEnum(departments));
        fields.add(new FieldInterval(1, 990));
        fields.add(new FieldInterval(1, 999));
        return fields;
    }

}
