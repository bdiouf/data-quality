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
 * <b>See also:</b> {@link MaskEmail}
 */
public class MaskTopEmailDomainRandomly extends MaskEmailRandomly {

    private static final long serialVersionUID = 4725759790417755993L;

    @Override
    protected String maskEmailRandomly(String address, int splitAddress) {
        int splitDomain = address.lastIndexOf('.');
        return address.substring(0, splitAddress + 1)
                + parameters[chooseAppropriateDomainIndex(address.substring(splitAddress + 1, splitDomain))]
                + address.substring(splitDomain);
    }

}
