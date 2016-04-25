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
import java.math.BigInteger;

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 21 juil. 2015 Detailled comment
 *
 */
public class GenerateSsnFr extends Function<String> implements Serializable {

    private static final long serialVersionUID = 8845031997964609626L;

    private static final BigInteger MOD97 = new BigInteger("97"); //$NON-NLS-1$

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        result.append(rnd.nextInt(2) + 1);
        int yy = rnd.nextInt(99) + 1;
        if (yy < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        result.append(yy);
        int mm = rnd.nextInt(12) + 1;
        if (mm < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        result.append(mm);
        int ll = rnd.nextInt(95) + 1;
        if (ll < 10) {
            result.append("0"); //$NON-NLS-1$
        }
        result.append(ll);
        for (int i = 0; i < 6; ++i) {
            result.append(rnd.nextInt(9));
        }

        BigInteger ssn = new BigInteger(result.toString());
        int controlKey = 97 - ssn.mod(MOD97).intValue();

        result.append(" "); //$NON-NLS-1$
        result.append(controlKey);

        return result.toString();
    }
}
