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

import java.util.ArrayList;

/**
 * DOC qzhao class global comment. Detailled comment<br>
 * 
 * This class masks the top-level domain name by the character X with the realistic format, for example, the email
 * address <i>"example@talend.com"</i> will be masked to <i>"example@XXXXXX.com"</i><br>
 */
public class MaskTopEmailDomainByX extends MaskEmailByX {

    private static final long serialVersionUID = -3171431436372092807L;

    /**
     * 
     * DOC qzhao Comment method "maskTopLevelDomainByX".<br>
     * 
     * Masks the top-level domain name by X
     * 
     * @param address
     * @return masked address
     */
    @Override
    protected String maskEmailByX(String address) {
        StringBuilder sb = new StringBuilder(address);
        int splitAddress = address.indexOf('@');
        ArrayList<Integer> indexes = getPointPostions(address, splitAddress);

        Character maskingCrct = getMaskingCharacter();

        for (Integer index : indexes) {
            for (int i = splitAddress + 1; i < index; i++)
                sb.setCharAt(i, maskingCrct);
            splitAddress = index;
        }
        return sb.toString();
    }
}
