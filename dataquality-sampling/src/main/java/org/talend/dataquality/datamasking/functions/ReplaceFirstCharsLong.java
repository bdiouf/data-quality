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
 * created by jgonzalez on 22 juin 2015. See ReplaceFirstChars.
 *
 */
public class ReplaceFirstCharsLong extends ReplaceFirstChars<Long> implements Serializable {

    private static final long serialVersionUID = 4462142503829372818L;

    private int parameter = 0;

    @Override
    protected Long doGenerateMaskedField(Long l) {
        if (l != null && integerParam > 0) {
            if (l == 0L) {
                return (long) rnd.nextInt(9);
            } else {
                parameter = (int) Math.log10(l) + 1 <= integerParam ? (int) Math.log10(l) + 1 : integerParam;
                StringBuilder sbu = new StringBuilder(l.toString());
                StringBuilder remp = new StringBuilder(EMPTY_STRING);
                for (int i = 0; i < parameter; ++i) {
                    remp.append(rnd.nextInt(9));
                }
                sbu.replace(0, parameter, remp.toString());
                return Long.parseLong(sbu.toString());
            }
        } else {
            return 0L;
        }
    }
}
