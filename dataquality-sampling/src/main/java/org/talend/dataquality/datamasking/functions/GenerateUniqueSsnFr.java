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
 * 
 * @author jteuladedenantes
 * 
 * The french SSN has 6 fields : [1, 2], [1, 99], [1, 12], {[1, 19] U {2A, 2B} U [20, 99]}, [1, 990], [1, 999]
 */
public class GenerateUniqueSsnFr extends Function<String> {

    private static final long serialVersionUID = 4514471121590047091L;

    private static final int MOD97 = 97; // $NON-NLS-1$

    private GenerateUniqueRandomPatterns frenchSsnPattern;

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str == null)
            return null;

        String strWithoutSpaces = str.replace(" ", "");

        // check if the pattern is valid with french ssn number
        if (strWithoutSpaces.isEmpty() || strWithoutSpaces.length() != 15) {
            if (keepInvalidPattern)
                return strWithoutSpaces;
            else
                return null;
        }

        // read the input strWithoutSpaces
        List<String> strs = new ArrayList<String>();
        strs.add(strWithoutSpaces.substring(0, 1));
        strs.add(strWithoutSpaces.substring(1, 3));
        strs.add(strWithoutSpaces.substring(3, 5));
        strs.add(strWithoutSpaces.substring(5, 7));
        strs.add(strWithoutSpaces.substring(7, 10));
        strs.add(strWithoutSpaces.substring(10, 13));

        if (frenchSsnPattern == null) {
            List<Field> fields = createFieldsListFromFrPattern();
            this.frenchSsnPattern = new GenerateUniqueRandomPatterns(fields, this.rnd.nextInt(9000) + 1000);
        }

        StringBuilder result = frenchSsnPattern.generateUniqueString(strs);
        if (result == null) {
            if (keepInvalidPattern)
                return strWithoutSpaces;
            else
                return null;
        }

        // add the security key specified for french SSN
        StringBuilder keyResult = new StringBuilder(result);
        if (keyResult.charAt(5) == '2') {
            keyResult.setCharAt(5, '1');
            keyResult.setCharAt(6, (keyResult.charAt(6) == 'A') ? '9' : '8');
        }
        int controlKey = 97 - (int) (Long.valueOf(keyResult.toString()) % MOD97);
        result.append(controlKey);
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) == ' ')
                result.insert(i, ' ');
        return result.toString();
    }

    /**
     * 
     * @return the list of each field
     */
    private List<Field> createFieldsListFromFrPattern() {
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
