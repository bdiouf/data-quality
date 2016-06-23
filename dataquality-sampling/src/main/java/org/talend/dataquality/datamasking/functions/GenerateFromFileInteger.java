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
public class GenerateFromFileInteger extends GenerateFromFile<Integer> {

    private static final long serialVersionUID = 1896675901231975008L;

    @Override
    protected Integer getOutput(String string) {
        return Integer.parseInt(string);
    }

    @Override
    protected Integer getDefaultOutput() {
        return 0;
    }
}
