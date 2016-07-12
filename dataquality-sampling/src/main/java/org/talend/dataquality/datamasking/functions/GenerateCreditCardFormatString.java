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
 * created by jgonzalez on 19 juin 2015. See GenerateCreditCardFormat.
 *
 */
public class GenerateCreditCardFormatString extends GenerateCreditCardFormat<String> {

    private static final long serialVersionUID = 3682663337119470753L;

    @Override
    protected String doGenerateMaskedField(String str) {
        String strWithoutSpaces = replaceSpacesInString(str);
        CreditCardType cct_format = null;
        StringBuilder res = new StringBuilder();
        if (strWithoutSpaces == null || EMPTY_STRING.equals(strWithoutSpaces)) {
            cct_format = chooseCreditCardType();
            res.append(generateCreditCard(cct_format));
        } else {
            try {
                cct_format = getCreditCardType(Long.parseLong(strWithoutSpaces)); // $NON-NLS-1$
            } catch (NumberFormatException e) {
                cct_format = chooseCreditCardType();
            }
            if (cct_format != null) {
                res.append(generateCreditCardFormat(cct_format, strWithoutSpaces));
            } else {
                cct_format = chooseCreditCardType();
                res.append(generateCreditCard(cct_format));
            }
        }
        if (keepFormat)
            return insertSpacesInString(str, res);
        else
            return res.toString();
    }
}
