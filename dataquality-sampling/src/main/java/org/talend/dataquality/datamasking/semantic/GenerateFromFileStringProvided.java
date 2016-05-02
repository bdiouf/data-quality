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
package org.talend.dataquality.datamasking.semantic;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.talend.dataquality.datamasking.functions.GenerateFromFileString;
import org.talend.dataquality.datamasking.functions.KeysLoader;

public class GenerateFromFileStringProvided extends GenerateFromFileString {

    private static final long serialVersionUID = 8936060786451303843L;

    private final static Logger log = Logger.getLogger(GenerateFromFileStringProvided.class);

    protected void init() {
        try {
            substituteList = KeysLoader.loadKeys(this.getClass().getResource(parameters[0]).getPath());
        } catch (IOException | NullPointerException e) {
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public String generateMaskedRow(String t) {
        if (t == null && keepNull) {
            return null;
        }
        return doGenerateMaskedField(t);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        return super.doGenerateMaskedField(str);
    }
}
