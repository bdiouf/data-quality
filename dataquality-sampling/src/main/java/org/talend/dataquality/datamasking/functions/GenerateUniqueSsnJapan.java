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
 * @author jteuladedenantes class global comment. Detailled comment
 * 
 * The japan SSN has 1 fields : [1, 1000000000000 - 1]
 */

public class GenerateUniqueSsnJapan extends Function<String> {

    private static final long serialVersionUID = -2321693247791991249L;

    private GenerateUniqueRandomPatterns japanSsnPattern;

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str == null)
            return null;

        String strWithoutSpaces = str.replace(" ", "");

        // check if the pattern is valid with french ssn number
        if (strWithoutSpaces.isEmpty() || strWithoutSpaces.length() != 12) {
            if (keepInvalidPattern)
                return strWithoutSpaces;
            else
                return null;
        }

        // read the input str
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(0, 12));

        if (this.japanSsnPattern == null) {
            List<Field> fields = createFieldsListFromFrPattern();
            this.japanSsnPattern = new GenerateUniqueRandomPatterns(fields, this.rnd.nextInt(9000) + 1000);
        }

        StringBuilder result = japanSsnPattern.generateUniqueString(strs);
        if (result == null) {
            if (keepInvalidPattern)
                return strWithoutSpaces;
            else
                return null;
        }
        return result.toString();
    }

    List<Field> createFieldsListFromFrPattern() {
        List<Field> fields = new ArrayList<Field>();
        fields.add(new FieldInterval(1, 1000000000000L - 1));
        return fields;
    }
}
