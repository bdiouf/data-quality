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

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 22 juin 2015. This function will replace every letter by the parameter.
 *
 */
public class ReplaceCharacters extends Function<String> {

    private static final long serialVersionUID = 368348491822287354L;

    /**
     * Replaces input by the pattern and replace it by the parameter
     * 
     * @param input
     * @param parameter
     * @return
     */
    private String replaceByParameter(String input, String parameter) {
        return patternLetter.matcher(input).replaceAll(parameter);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str != null && !EMPTY_STRING.equals(str)) {
            if (patternSpaceOrLetterOrDigit.matcher(parameters[0]).matches()) { // $NON-NLS-1$
                if ((" ").equals(parameters[0])) { //$NON-NLS-1$
                    return replaceSpacesInString(replaceByParameter(str, parameters[0]));
                } else {
                    return replaceByParameter(str, parameters[0]); // $NON-NLS-1$
                }
            } else {
                return replaceByParameter(str, String.valueOf(UPPER.charAt(rnd.nextInt(26))));
            }
        } else {
            return EMPTY_STRING;
        }
    }
}
