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
 * created by jgonzalez on 22 juin 2015. See KeepLastAndGenerate.
 *
 */
public class KeepLastAndGenerateInteger extends KeepLastAndGenerate<Integer> {

    private static final long serialVersionUID = -5034779122669578348L;

    @Override
    protected Integer doGenerateMaskedField(Integer i) {
        if (i != null && integerParam > 0) {
            if ((int) Math.log10(i) + 1 < integerParam) {
                return i;
            }
            StringBuilder sb = new StringBuilder(EMPTY_STRING);
            for (int j = 0; j < i.toString().length() - integerParam; ++j) {
                sb.append(rnd.nextInt(9));
            }
            sb.append(i.toString().substring(i.toString().length() - integerParam, i.toString().length()));
            return Integer.parseInt(sb.toString());
        }
        return 0;
    }
}
