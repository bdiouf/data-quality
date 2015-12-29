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
package org.talend.dataquality.semantic.validator.impl;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.pojava.datetime.DateTime;
import org.pojava.datetime.DateTimeConfig;
import org.pojava.datetime.DateTimeConfigBuilder;
import org.pojava.datetime.IDateTimeConfig;
import org.talend.dataquality.semantic.validator.AbstractRegexSemanticValidator;

/**
 * Created by sizhaoliu on 16.03.15.
 */
public class DateSemanticValidator extends AbstractRegexSemanticValidator {

    private IDateTimeConfig dtcUS;

    private IDateTimeConfig dtcFR;

    public DateSemanticValidator() {
        initDateTimeConfigEN();
        initDateTimeConfigFR();
    }

    public void initDateTimeConfigEN() {
        DateTimeConfigBuilder builder = DateTimeConfigBuilder.newInstance();
        builder.setLocale(new Locale("en", "US"));
        builder.setDmyOrder(false);
        dtcUS = DateTimeConfig.fromBuilder(builder);
        dtcUS.validate();
    }

    public void initDateTimeConfigFR() {
        DateTimeConfigBuilder builder = DateTimeConfigBuilder.newInstance();
        builder.setLocale(new Locale("fr", "FR"));
        builder.setDmyOrder(true);
        dtcFR = DateTimeConfig.fromBuilder(builder);
        dtcFR.validate();
    }

    @Override
    public boolean isValid(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        if (value.length() < 6) {
            return false;
        }
        try {
            DateTime.parse(value, dtcFR);
            return true;
        } catch (Exception e1) {
            if (!e1.getMessage().startsWith("Could not determine Year")) {
                // continue to parse with US locale. otherwise, dates like 2-19-2016 can not be recognized.
                try {
                    DateTime.parse(value, dtcUS);
                    return true;
                } catch (Exception e2) {
                    // the value is not recognized as a date when a second exception is thrown
                }
            }
        }
        return false;
    }
}
