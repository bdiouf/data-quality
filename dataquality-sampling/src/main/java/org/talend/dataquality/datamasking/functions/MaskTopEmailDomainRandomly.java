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
 * DOC qzhao class global comment. Detailled comment<br>
 * 
 * This class provides a random replacement of the top-level email address from a list of common domains<br>
 * 
 * If a specified domain is provided, see the class {@link MaskSpecifiedEmailDomain}
 * 
 * <b>See also:</b> {@link MaskEmailDomain}
 */
public class MaskTopEmailDomainRandomly extends MaskEmailDomain {

    private static final long serialVersionUID = 4725759790417755993L;

    /**
     * Conditions in masking top-level email domain randomly:<br>
     * <ul>
     * <li>When user gives a space, masks the top-level domain with X</li>
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
            int splitAddress = str.indexOf('@');
            int splitDomain = str.lastIndexOf('.');

            if (replacements.size() == 1) {
                if (replacements.get(0).isEmpty())
                    return maskTopLevelDomainByX(str, splitAddress, splitDomain);
                else
                    return maskTopLevelDomainRandomly(str, replacements.get(0), splitAddress, splitDomain);
            } else {
                String originalDomain = str.substring(splitAddress + 1, splitDomain);
                int domainIndex = chooseAppropriateDomainIndex(originalDomain);
                return maskTopLevelDomainRandomly(str, replacements.get(domainIndex), splitAddress, splitDomain);
            }

        }

        return str;
    }

}
