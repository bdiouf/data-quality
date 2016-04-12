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
 * This class masks the full domain name by the character X with the realistic format, for example, the email address
 * <i>"example@talend.com"</i> will be masked to <i>"example@XXXXXX.XXX"</i><br>
 * 
 * <b>See also:</b> {@link MaskEmailDomain}
 */
public class MaskFullEmailDomainByX extends MaskEmailDomain {

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

        if (str == null) {
            return EMPTY_STRING;
        }

        if (str.isEmpty() || !parameters[0].isEmpty()) {
            return str;
        }

        if (isValidEmailAddress(str)) {
            int count = str.indexOf('@');
            return maskFullDomainByX(str, count);
        }

        return str;
    }

}
