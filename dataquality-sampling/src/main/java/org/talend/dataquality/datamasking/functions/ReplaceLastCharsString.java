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
 * created by jgonzalez on 22 juin 2015. See ReplaceLastChars.
 *
 */
public class ReplaceLastCharsString extends ReplaceLastChars<String> {

    private static final long serialVersionUID = 3894256715739085888L;

    private int parameter = 0;

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
            parameter = integerParam > str.length() ? str.length() : integerParam;
            StringBuilder sb = new StringBuilder(str);
            StringBuilder repla = new StringBuilder(EMPTY_STRING);
            for (int i = sb.length() - parameter; i < sb.length(); ++i) {
                if (Character.isDigit(str.charAt(i))) {
                    repla.append(rnd.nextInt(9));
                } else if (Character.isUpperCase(str.charAt(i))) {
                    repla.append(UPPER.charAt(rnd.nextInt(26)));
                } else if (Character.isLowerCase(str.charAt(i))) {
                    repla.append(LOWER.charAt(rnd.nextInt(26)));
                } else {
                    repla.append(str.charAt(i));
                }
            }
            sb.replace(str.length() - parameter, str.length(), repla.toString());
            return sb.toString();
        } else {
            return EMPTY_STRING;
        }
    }
}
