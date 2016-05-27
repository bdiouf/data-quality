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
package org.talend.dataquality.semantic.index.utils.optimizer;

import java.util.Collections;
import java.util.Set;

public class FrCommuneOptimizer implements CategoryOptimizer {

    @Override
    public Set<String> optimize(String[] values) {
        if (values.length == 1) {
            return Collections.singleton(values[0]);
        } else if (values.length == 2) {
            final String article = values[0].substring(1, values[0].length() - 1);
            final String cityName = values[1];
            if (article.charAt(1) == '\'') {
                return Collections.singleton(article + cityName);
            } else {
                return Collections.singleton(article + " " + cityName);
            }
        }
        return Collections.emptySet();
    }

}
