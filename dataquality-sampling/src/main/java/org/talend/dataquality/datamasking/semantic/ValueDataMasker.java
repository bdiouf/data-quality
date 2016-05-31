// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.io.Serializable;
import java.util.List;

import org.talend.dataquality.datamasking.functions.Function;

public class ValueDataMasker implements Serializable {

    private static final long serialVersionUID = 7071792900542293289L;

    private Function<String> function;

    Function<String> getFunction() {
        return function;
    }

    public ValueDataMasker(String semanticCategory, String dataType) {
        function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory(semanticCategory, dataType);
    }

    public ValueDataMasker(String semanticCategory, String dataType, List<String> params) {
        function = SemanticMaskerFunctionFactory.createMaskerFunctionForSemanticCategory(semanticCategory, dataType, params);
    }

    public String maskValue(String v) {
        return function.generateMaskedRow(v);
    }

}
