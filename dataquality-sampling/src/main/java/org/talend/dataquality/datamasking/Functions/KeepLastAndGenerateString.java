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
package org.talend.dataquality.datamasking.Functions;

import java.io.Serializable;

/**
 * created by jgonzalez on 22 juin 2015. See KeepLastAndGenerate.
 *
 */
public class KeepLastAndGenerateString extends KeepLastAndGenerate<String> implements Serializable {

    private static final long serialVersionUID = 5714789810163265429L;

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null) || EMPTY_STRING.equals(str) && keepNull) {
            return str;
        } else {
            if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
                String s = str.trim();
                StringBuilder sb = new StringBuilder(EMPTY_STRING);
                if (integerParam > s.length()) {
                    return str;
                }
                if (integerParam < s.length()) {
                    StringBuilder end = new StringBuilder(EMPTY_STRING);
                    for (int i = s.length() - 1; i >= s.length() - integerParam; --i) {
                        end.append(s.charAt(i));
                        if (!Character.isDigit(s.charAt(i))) {
                            integerParam++;
                        }
                    }
                    for (int i = 0; i < s.length() - integerParam; ++i) {
                        if (Character.isDigit(s.charAt(i))) {
                            sb.append(rnd.nextInt(9));
                        } else {
                            sb.append(s.charAt(i));
                        }
                    }
                    sb.append(end.reverse());
                    return sb.toString();
                }
            }
            return EMPTY_STRING;
        }
    }
}
