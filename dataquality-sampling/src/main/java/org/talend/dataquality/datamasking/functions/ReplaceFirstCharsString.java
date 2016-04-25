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

import java.io.Serializable;

/**
 * created by jgonzalez on 22 juin 2015. See ReplaceFirstChars.
 *
 */
public class ReplaceFirstCharsString extends ReplaceFirstChars<String> implements Serializable {

    private static final long serialVersionUID = 7856254797887338192L;

    private int parameter = 0;

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
            parameter = integerParam > str.length() ? str.length() : integerParam;
            StringBuilder sb = new StringBuilder(str);
            StringBuilder repl = new StringBuilder(EMPTY_STRING);
            for (int i = 0; i < parameter; ++i) {
                if (Character.isDigit(str.charAt(i))) {
                    repl.append(rnd.nextInt(9));
                } else if (Character.isUpperCase(str.charAt(i))) {
                    repl.append(UPPER.charAt(rnd.nextInt(26)));
                } else if (Character.isLowerCase(str.charAt(i))) {
                    repl.append(LOWER.charAt(rnd.nextInt(26)));
                } else {
                    repl.append(str.charAt(i));
                }
            }
            sb.replace(0, parameter, repl.toString());
            return sb.toString();
        } else {
            return EMPTY_STRING;
        }
    }
}
