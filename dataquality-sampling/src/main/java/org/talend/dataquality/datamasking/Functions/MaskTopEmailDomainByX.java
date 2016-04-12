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
package org.talend.dataquality.datamasking.Functions;

import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * DOC qzhao class global comment. Detailled comment<br>
 * 
 * This class masks the top-level domain name by the character X with the realistic format, for example, the email
 * address <i>"example@talend.com"</i> will be masked to <i>"example@XXXXXX.com"</i><br>
 */
public class MaskTopEmailDomainByX extends MaskEmailDomain {

    private static final long serialVersionUID = 1L;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, RandomWrapper rand) {
        super.parse(extraParameter, keepNullValues, rand);
    }

    @Override
    public String generateMaskedRow(String str) {
        if (str == null && keepNull) {
            return null;
        }
        if (str == null || str.isEmpty() || !parameters[0].isEmpty()) {
            return EMPTY_STRING;
        }

        if (isValidEmailAddress(str)) {
            return maskTopLevelDomainByX(str);
        }

        return str;
    }

}
