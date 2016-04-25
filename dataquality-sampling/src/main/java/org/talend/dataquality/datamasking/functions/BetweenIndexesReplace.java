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
 * created by jgonzalez on 22 juin 2015. This class is used when the requested function is BetweenIndexesKeep. It will
 * return a new String where all the elements between the indexes are replaced.
 *
 */
public class BetweenIndexesReplace extends BetweenIndexes {

    private static final long serialVersionUID = 1440323544625986870L;

    private String s = EMPTY_STRING;

    private String replace(String str, boolean isThird) {
        char ch = ' ';
        StringBuilder sb = new StringBuilder(str);
        if (!isThird) {
            for (int i = begin - 1; i < end; ++i) {
                if (Character.isDigit(str.charAt(i))) {
                    sb.setCharAt(i, Character.forDigit(rnd.nextInt(9), 10));
                } else if (Character.isUpperCase(str.charAt(i))) {
                    sb.setCharAt(i, UPPER.charAt(rnd.nextInt(26)));
                } else if (Character.isLowerCase(str.charAt(i))) {
                    sb.setCharAt(i, LOWER.charAt(rnd.nextInt(26)));
                } else {
                    sb.setCharAt(i, str.charAt(i));
                }
            }
        } else {
            ch = s.toCharArray()[0];
            for (int i = begin - 1; i < end; ++i) {
                sb.setCharAt(i, ch);
            }
        }
        return sb.toString();
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (super.check(str, 2) || super.check(str, 3)) {
            boolean isThird = true;
            try {
                s = parameters[2].trim();
                if (!patternLetterOrDigit.matcher(s).matches()) { // $NON-NLS-1$
                    isThird = false;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                isThird = false;
            }
            super.setBounds(str);
            s = replace(str, isThird);
        } else {
            return EMPTY_STRING;
        }
        return s;
    }

}
