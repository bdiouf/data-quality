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

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 21 juil. 2015 Detailled comment
 *
 */
public class GenerateSsnUs extends Function<String> implements Serializable {

    private static final long serialVersionUID = -7651076296534530622L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        result.append(rnd.nextInt(8));
        result.append(rnd.nextInt(9));
        if (result.charAt(0) == '0' && result.charAt(1) == '0' || result.charAt(0) == '6' && result.charAt(1) == '6') {
            int tmp = 0;
            do {
                tmp = rnd.nextInt(9);
            } while ((char) tmp == result.charAt(0));
            result.append(tmp);
        } else {
            result.append(rnd.nextInt(9));
        }
        result.append("-"); //$NON-NLS-1$
        result.append(rnd.nextInt(9));
        if (result.charAt(4) == '0') {
            result.append(rnd.nextInt(8) + 1);
        } else {
            result.append(rnd.nextInt(9));
        }
        result.append("-"); //$NON-NLS-1$
        for (int i = 0; i < 3; ++i) {
            result.append(rnd.nextInt(9));
        }
        if (result.charAt(7) == '0' && result.charAt(8) == '0' && result.charAt(9) == '0') {
            result.append(rnd.nextInt(8) + 1);
        } else {
            result.append(rnd.nextInt(9));
        }
        return result.toString();
    }

}
