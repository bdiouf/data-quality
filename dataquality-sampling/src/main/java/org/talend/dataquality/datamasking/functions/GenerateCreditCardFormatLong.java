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
 * created by jgonzalez on 19 juin 2015. See GenerateCreditCardFormat.
 *
 */
public class GenerateCreditCardFormatLong extends GenerateCreditCardFormat<Long> implements Serializable {

    private static final long serialVersionUID = 4432818921989956298L;

    @Override
    protected Long doGenerateMaskedField(Long l) {
        CreditCardType cct_format = null;
        if (l == null) {
            cct_format = super.chooseCreditCardType();
            return super.generateCreditCard(cct_format);
        } else {
            cct_format = super.getCreditCardType(l);
            if (cct_format != null) {
                return super.generateCreditCardFormat(cct_format, l);
            } else {
                cct_format = super.chooseCreditCardType();
                return super.generateCreditCard(cct_format);
            }
        }
    }
}
