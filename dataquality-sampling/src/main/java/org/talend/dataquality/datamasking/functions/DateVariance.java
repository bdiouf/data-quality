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
import java.util.Date;

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 18 juin 2015. This function will modify the input date by adding or retieving a number of
 * days lower than the parameter.
 *
 */
public class DateVariance extends Function<Date> implements Serializable {

    private static final long serialVersionUID = 7723968828358381315L;

    private static final Long nb_ms_per_day = 86400000L;

    @Override
    protected Date doGenerateMaskedField(Date date) {
        if (date != null) {
            int variation = 0;
            if (integerParam < 0) {
                integerParam *= -1;
            } else if (integerParam == 0) {
                integerParam = 31;
            }
            do {
                variation = rnd.nextInt(2 * integerParam) - integerParam;
            } while (variation == 0);
            Long originalDate = date.getTime();
            Date newDate = new Date(originalDate + variation * nb_ms_per_day);
            return newDate;
        } else {
            return new Date(System.currentTimeMillis());
        }
    }
}
