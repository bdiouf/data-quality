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
 * created by jgonzalez on 22 juin 2015. See KeepLastAndGenerate.
 *
 */
public class KeepLastAndGenerateLong extends KeepLastAndGenerate<Long> implements Serializable {

    private static final long serialVersionUID = -4367992150535472987L;

    @Override
    protected Long doGenerateMaskedField(Long l) {
        if (l != null && integerParam > 0 && integerParam > 0) {
            if ((int) Math.log10(l) + 1 < integerParam) {
                return l;
            }
            StringBuilder val = new StringBuilder(EMPTY_STRING);
            for (int i = 0; i < l.toString().length() - integerParam; ++i) {
                val.append(rnd.nextInt(9));
            }
            val.append(l.toString().substring(l.toString().length() - integerParam, l.toString().length()));
            return Long.parseLong(val.toString());
        }
        return 0L;
    }
}
