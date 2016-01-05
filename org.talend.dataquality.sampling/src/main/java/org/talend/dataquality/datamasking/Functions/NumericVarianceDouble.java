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

/**
 * created by jgonzalez on 18 juin 2015. See NumericVariance.
 *
 */
public class NumericVarianceDouble extends NumericVariance<Double> implements Serializable {

    private static final long serialVersionUID = 3652667602304768170L;

    @Override
    public Double generateMaskedRow(Double d) {
        if (d == null && keepNull) {
            return null;
        } else {
            if (d == null) {
                return 0.0;
            } else {
                super.init();
                double value = d * ((double) rate + 100) / 100;
                return value;
            }
        }
    }
}