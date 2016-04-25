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
 * created by jgonzalez on 18 juin 2015. See NumericVariance.
 *
 */
public class NumericVarianceInteger extends NumericVariance<Integer> implements Serializable {

    private static final long serialVersionUID = -5691096627763244343L;

    @Override
    protected Integer doGenerateMaskedField(Integer i) {
        if (i == null) {
            return 0;
        } else {
            super.init();
            int value = i * (rate + 100) / 100;
            return value;
        }
    }
}
