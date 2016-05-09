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
package org.talend.dataquality.datamasking.semantic;

import org.talend.dataquality.datamasking.functions.Function;

/**
 * created by jgonzalez on 22 juin 2015. This function will replace every letter by the parameter.
 *
 */
public class ReplaceCharactersWithGeneration extends Function<String> {

    private static final long serialVersionUID = 368348491822287354L;

    @Override
    protected String doGenerateMaskedField(String input) {
        if (input == null || EMPTY_STRING.equals(input.trim())) {
            return input;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                char ch = input.charAt(i);
                if (Character.isUpperCase(ch)) {
                    sb.append(UPPER.charAt(rnd.nextInt(26)));
                } else if (Character.isLowerCase(ch)) {
                    sb.append(LOWER.charAt(rnd.nextInt(26)));
                } else if (Character.isDigit(ch)) {
                    sb.append(rnd.nextInt(10));
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
    }
}
