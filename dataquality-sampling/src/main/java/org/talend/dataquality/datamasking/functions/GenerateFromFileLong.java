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
 * created by jgonzalez on 19 juin 2015. See GenerateFromFile.
 *
 */
public class GenerateFromFileLong extends GenerateFromFile<Long> {

    private static final long serialVersionUID = -2510960686417569211L;

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < substituteList.size(); ++i) {
            long tmp = 0L;
            try {
                tmp = Long.parseLong(substituteList.get(i));
                genericTokens.add(tmp);
            } catch (NumberFormatException e) {
            }
        }
    }

    @Override
    protected Long getDefaultOutput() {
        return 0L;
    }
}
