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
 * created by jgonzalez on 19 juin 2015. This function will modify the input data by multiplying it by a number between
 * the parameter and its opposite.
 *
 */
public abstract class NumericVariance<T2> extends Function<T2> {

    private static final long serialVersionUID = -9042942041517353551L;

    protected int rate = 0;

    protected void init() {
        if (integerParam == 0) {
            integerParam = 10;
        } else if (integerParam < 0) {
            integerParam *= -1;
        }
        do {
            rate = rnd.nextInt(2 * integerParam) - integerParam;
        } while (rate == 0);
    }

    @Override
    protected abstract T2 doGenerateMaskedField(T2 t);
}
