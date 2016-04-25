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

/**
 * created by jgonzalez on 22 juin 2015. See ReplaceNumeric.
 *
 */
public class ReplaceNumericString extends ReplaceNumeric<String> {

    private static final long serialVersionUID = 8707035612963121276L;

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str != null && !EMPTY_STRING.equals(str)) {
            if (patternSpaceOrLetterOrDigit.matcher(parameters[0]).matches()) { // $NON-NLS-1$
                if ((" ").equals(parameters[0])) { //$NON-NLS-1$
                    return replaceSpacesInString(replacePattern(str, parameters[0]));
                } else {
                    return replacePattern(str, parameters[0]); // $NON-NLS-1$
                }
            } else {
                throw new IllegalArgumentException(
                        "The parameter for \"replace all digits\" function must be a digit or a letter"); //$NON-NLS-1$
            }
        } else {
            return EMPTY_STRING;
        }
    }
}
