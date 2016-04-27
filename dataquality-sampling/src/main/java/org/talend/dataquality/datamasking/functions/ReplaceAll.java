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

import java.util.regex.Pattern;

/**
 * created by jgonzalez on 22 juin 2015. This function will replace every character by the parameter.
 *
 */
public class ReplaceAll extends Function<String> {

    private static final long serialVersionUID = -6755455022090241272L;

    private Pattern pattern = Pattern.compile(".");

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str != null && !EMPTY_STRING.equals(str) && patternLetterOrDigit.matcher(parameters[0]).matches()) { // $NON-NLS-1$
            return pattern.matcher(str).replaceAll(parameters[0]);
        } else {
            return EMPTY_STRING;
        }
    }
}
