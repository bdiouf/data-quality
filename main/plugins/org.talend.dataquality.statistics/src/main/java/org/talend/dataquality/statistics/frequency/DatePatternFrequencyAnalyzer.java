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
package org.talend.dataquality.statistics.frequency;

import org.talend.datascience.common.inference.type.DatetimePatternUtils;

/**
 * Date Pattern frequency analyzer for date values.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class DatePatternFrequencyAnalyzer extends PatternFrequencyAnalyzer {

    private static final long serialVersionUID = 6706569873932749363L;

    @Override
    protected String getValuePattern(String originalValue) {
        return DatetimePatternUtils.getInstance().datePatternReplace(originalValue);
    }

}
