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

import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * DOC qzhao class global comment. Detailled comment<br>
 * 
 * This class provides a random replacement of the email address from a list of common domains<br>
 * 
 * If a specified domain is provided, see the class {@link MaskSpecifiedEmailDomain}
 * 
 * <b>See also:</b> {@link MaskEmailDomain}
 */
public class MaskFullEmailDomainRandomly extends MaskEmailDomain {

    private static final long serialVersionUID = -7030194827250476071L;

    /**
     * Conditions in masking full email domain randomly:<br>
     * <ul>
     * <li>When user gives a space, masks the full domain with X</li>
     * <li>When user gives a list of parameters, chooses from the list randomly</li>
     * <li>When user gives a list of parameters with one or more space in the list, removes the spaces directly</li>
     * <li>when user gives a local file, gets the choices from the file</li>
     * </ul>
     */
    @Override
    protected String doGenerateMaskedField(String str) {

        if (str == null || str.isEmpty()) {
            return EMPTY_STRING;
        }

        if (isValidEmailAddress(str)) {
            rnd = new RandomWrapper();
            int splitAddress = str.indexOf('@');
            int domainIndex = 0;
            if (replacements.size() == 1) {
                if (replacements.get(0).isEmpty())
                    return maskFullDomainByX(str, splitAddress);
            } else {
                String originalDomain = str.substring(splitAddress);
                domainIndex = chooseAppropriateDomainIndex(originalDomain);
            }

            return maskFullDomainRandomly(str, replacements.get(domainIndex), splitAddress);
        }

        return str;
    }

}
