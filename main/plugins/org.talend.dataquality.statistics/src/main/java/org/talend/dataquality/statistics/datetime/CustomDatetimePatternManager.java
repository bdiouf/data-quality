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
package org.talend.dataquality.statistics.datetime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Customized date time pattern manager.
 * 
 * @author mzhao
 *
 */
public final class CustomDatetimePatternManager {

    public static boolean isDate(String value, List<String> customPatterns) {
        return isDate(value, customPatterns, Locale.getDefault());
    }

    public static boolean isDate(String value, List<String> customPatterns, Locale locale) {
        // use custom patterns first
        if(isMatchCustomPatterns(value, customPatterns, locale)) {
            return true;
        }
        // validate using system pattern manager
        return SystemDatetimePatternManager.isDate(value);
    }

    public static boolean isTime(String value, List<String> customPatterns) {
        return isTime(value, customPatterns, Locale.getDefault());
    }

    public static boolean isTime(String value, List<String> customPatterns, Locale locale) {
        // use custom patterns first
        if (isMatchCustomPatterns(value, customPatterns, locale)) {
            return true;
        }
        // validate using system pattern manager
        return SystemDatetimePatternManager.isTime(value);
    }

    private static boolean isMatchCustomPatterns(String value, List<String> customPatterns, Locale locale) {
        for (String pattern : customPatterns) {
            if (isMatchCustomPattern(value, pattern, locale)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMatchCustomPattern(String value, String customPattern, Locale locale) {
        if (customPattern == null) {
            return false;
        }
        try {// firstly, try with user-defined locale
            DateTimeFormatter.ofPattern(customPattern, locale).parse(value);
            return true;
        } catch (DateTimeParseException | IllegalArgumentException e) {
            if (!Locale.US.equals(locale)) {
                try {// try with LOCALE_US if user defined locale is not US
                    DateTimeFormatter.ofPattern(customPattern, Locale.US).parse(value);
                    return true;
                } catch (DateTimeParseException | IllegalArgumentException e1) {
                    // continue
                }
            }
            if (!Locale.getDefault().equals(locale) && !Locale.getDefault().equals(Locale.US)) {
                try {// try with LOCALE_JVM it none of the above matches
                    DateTimeFormatter.ofPattern(customPattern, Locale.getDefault()).parse(value);
                    return true;
                } catch (DateTimeParseException | IllegalArgumentException e2) {
                    // no more try
                }
            }
        }
        return false;
    }

    public static String replaceByDateTimePattern(String value, String customPattern) {
        return replaceByDateTimePattern(value, customPattern, Locale.getDefault());
    }

    public static String replaceByDateTimePattern(String value, String customPattern, Locale locale) {
        return replaceByDateTimePattern(value, Collections.singletonList(customPattern), locale);
    }

    public static String replaceByDateTimePattern(String value, List<String> customPatterns) {
        return replaceByDateTimePattern(value, customPatterns, Locale.getDefault());
    }

    public static String replaceByDateTimePattern(String value, List<String> customPatterns, Locale locale) {
        for (String customPattern : customPatterns) {
            if (isMatchCustomPattern(value, customPattern, locale)) {
                return customPattern;
            }
        }
        // otherwise, replace with system date pattern manager.
        return systemPatternReplace(value);
    }

    private static String systemPatternReplace(String value) {
        String pattern = SystemDatetimePatternManager.datePatternReplace(value);
        if (pattern.equals(value)) {
            pattern = SystemDatetimePatternManager.timePatternReplace(value);
        }
        return pattern;
    }
}
