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
public class GenerateFromFileHashInteger extends GenerateFromFileHash<Integer> {

    private static final long serialVersionUID = 4299740430046381222L;

    @Override
    protected Integer getOutput(String string) {
        return Integer.valueOf(string);
    }

    @Override
    protected Integer getDefaultOutput() {
        return 0;
    }
}
