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
 * created by jgonzalez on 24 juin 2015. See GenerateFromList.
 *
 */
public class GenerateFromListInteger extends GenerateFromList<Integer> {

    private static final long serialVersionUID = -6564132506763703813L;

    @Override
    protected void init() {
        for (String tmp : parameters) {
            int intTmp = 0;
            try {
                intTmp = Integer.parseInt(tmp.trim());
                genericTokens.add(intTmp);
            } catch (NumberFormatException e) {
                // Do Nothing
            }
        }
    }

    @Override
    protected Integer getDefaultOutput() {
        return 0;
    }
}
