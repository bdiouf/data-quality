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
 * created by jgonzalez on 24 juin 2015. See GgenerateFromFileHash.
 *
 */
public class GenerateFromFileHashString extends GenerateFromFileHash<String> {

    private static final long serialVersionUID = -7769720419054349753L;

    @Override
    protected void init() {
        super.init();
        genericTokens = StringTokens;
    }

    @Override
    protected String getDefaultOutput() {
        return EMPTY_STRING;
    }
}
