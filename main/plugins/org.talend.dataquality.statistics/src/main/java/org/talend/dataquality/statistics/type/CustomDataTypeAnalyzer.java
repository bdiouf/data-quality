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
package org.talend.dataquality.statistics.type;

import org.talend.datascience.common.inference.type.DataTypeAnalyzer;

/**
 * Date type analyzer with customized extention such as the date time pattern
 * 
 * @since 1.3.3
 * @author zhao
 *
 */
public class CustomDataTypeAnalyzer extends DataTypeAnalyzer {

    private static final long serialVersionUID = -9188435209256600268L;

    private String customDateTimePattern = null;

    public void setCustomDateTimePattern(String customDateTimePattern) {
        this.customDateTimePattern = customDateTimePattern;
    }

    public String getCustomDateTimePattern() {
        return customDateTimePattern;
    }

    @Override
    protected boolean isDate(String value) {
        return CustomDatetimePatternManager.isDate(value, customDateTimePattern);
    }

    @Override
    protected boolean isTime(String value) {
        return CustomDatetimePatternManager.isTime(value, customDateTimePattern);
    }
}
