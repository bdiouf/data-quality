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
 * created by jgonzalez on 22 juil. 2015 Detailled comment
 *
 */
public class GenerateSsnJapan extends Function<String> {

    private static final long serialVersionUID = -8621894245597689328L;

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        for (int i = 0; i < 12; ++i) {
            result.append(rnd.nextInt(9));
        }
        return result.toString();
    }

}
