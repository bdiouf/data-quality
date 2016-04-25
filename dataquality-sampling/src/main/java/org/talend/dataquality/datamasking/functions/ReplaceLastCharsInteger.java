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
 * created by jgonzalez on 22 juin 2015. See ReplaceLastChars.
 *
 */
public class ReplaceLastCharsInteger extends ReplaceLastChars<Integer> implements Serializable {

    private static final long serialVersionUID = -57357829426399512L;

    private int parameter = 0;

    @Override
    protected Integer doGenerateMaskedField(Integer i) {
        if (i != null && integerParam > 0) {
            if (i == 0) {
                return rnd.nextInt(9);
            } else {
                parameter = (int) Math.log10(i) + 1 <= integerParam ? (int) Math.log10(i) + 1 : integerParam;
                StringBuilder sbui = new StringBuilder(i.toString());
                StringBuilder rempl = new StringBuilder(EMPTY_STRING);
                for (int j = 0; j < parameter; ++j) {
                    rempl.append(rnd.nextInt(9));
                }
                sbui.replace(sbui.length() - parameter, sbui.length(), rempl.toString());
                return Integer.parseInt(sbui.toString());
            }
        } else {
            return 0;
        }
    }
}
