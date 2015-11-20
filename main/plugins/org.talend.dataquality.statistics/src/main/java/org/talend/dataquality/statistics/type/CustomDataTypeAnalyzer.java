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

import java.util.Locale;

import org.talend.datascience.common.inference.type.DataTypeAnalyzer;
import org.talend.datascience.common.parameter.ParameterUtils;

/**
 * Date type analyzer with customized extention such as the date time pattern
 * 
 * @since 1.3.3
 * @author zhao
 *
 */
public class CustomDataTypeAnalyzer extends DataTypeAnalyzer {

    private static final long serialVersionUID = -9188435209256600268L;

    private String customizedPattern = null;

    private Locale locale = Locale.getDefault();

    @Override
    public void init() {
        customizedPattern = ParameterUtils.getCustomizedPattern(parameters);
        Locale newLocale = ParameterUtils.getLocale(parameters);
        if (newLocale != null) {
            locale = newLocale;
        }
        super.init();
    }

    @Override
    protected boolean isDate(String value) {
        return CustomDatetimePatternManager.isDate(value, customizedPattern, locale);
    }

    @Override
    protected boolean isTime(String value) {
        return CustomDatetimePatternManager.isTime(value, customizedPattern, locale);
    }
}
