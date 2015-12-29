// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 21 juil. 2015 Detailled comment
 *
 */
public class GenerateSsnUk extends Function<String> implements Serializable {

    private static final long serialVersionUID = 4664211523958436354L;

    private static String first = "AZERTYOPSGHJKLMWXCBN"; //$NON-NLS-1$

    private static String second = "AZERTYPSGHJKLMWXCBN"; //$NON-NLS-1$

    private static List<String> forbid = new ArrayList<>(Arrays.asList("BG", "GB", "NK", "KN", "TN", "NT", "ZZ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            StringBuilder result = new StringBuilder(EMPTY_STRING);
            StringBuilder prefix;
            char tmp;
            do {
                prefix = new StringBuilder(EMPTY_STRING);
                tmp = first.charAt(rnd.nextInt(20));
                prefix.append(tmp);
                tmp = second.charAt(rnd.nextInt(19));
                prefix.append(tmp);
            } while (forbid.contains(prefix));
            result.append(prefix);
            result.append(" "); //$NON-NLS-1$
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    result.append(rnd.nextInt(9));
                }
                result.append(" "); //$NON-NLS-1$
            }
            result.append(UPPER.charAt(rnd.nextInt(4)));
            return result.toString();
        }
    }
}
