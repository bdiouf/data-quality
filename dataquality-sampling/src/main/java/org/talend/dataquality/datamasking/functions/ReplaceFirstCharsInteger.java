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
 * created by jgonzalez on 22 juin 2015. See ReplaceFirstChars.
 *
 */
public class ReplaceFirstCharsInteger extends ReplaceFirstChars<Integer> {

    private static final long serialVersionUID = 2117713944314991179L;

    private int parameter = 0;

    @Override
    protected Integer doGenerateMaskedField(Integer i) {
        if (i != null && integerParam > 0) {
            if (i == 0) {
                return rnd.nextInt(9);
            } else {
                parameter = (int) Math.log10(i) + 1 <= integerParam ? (int) Math.log10(i) + 1 : integerParam;
                StringBuilder sbu = new StringBuilder(i.toString());
                StringBuilder remp = new StringBuilder(EMPTY_STRING);
                for (int j = 0; j < parameter; ++j) {
                    remp.append(rnd.nextInt(9));
                }
                sbu.replace(0, parameter, remp.toString());
                return Integer.parseInt(sbu.toString());
            }
        } else {
            return 0;
        }
    }
}
