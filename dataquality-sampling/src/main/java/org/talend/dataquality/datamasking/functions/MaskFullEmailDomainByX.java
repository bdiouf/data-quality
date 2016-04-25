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

import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * DOC qzhao class global comment. Detailled comment<br>
 * 
 * This class masks the full domain name by the character X with the realistic format, for example, the email address
 * <i>"example@talend.com"</i> will be masked to <i>"example@XXXXXX.XXX"</i><br>
 * 
 * <b>See also:</b> {@link MaskEmailDomain}
 */
public class MaskFullEmailDomainByX extends MaskEmailDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, RandomWrapper rand) {
        super.parse(extraParameter, keepNullValues, rand);
    }

    /**
     * Three conditions in masking-email domain by x<br>
     * <ul>
     * <li>if the user inputs nothing, the full email domain will be masked by character X</li>
     * <li>if the user inputs a character, the full email domain will be masked by this character</li>
     * <li>if the user's inputs something inappropriate, the full email domain will be masked by character X</li>
     * </ul>
     */
    @Override
    protected String doGenerateMaskedField(String str) {

        if (str == null || str.isEmpty()) {
            return EMPTY_STRING;
        }

        if (isValidEmailAddress(str)) {
            int count = str.indexOf('@');
            return maskFullDomainByX(str, count);
        }

        return str;
    }

}
