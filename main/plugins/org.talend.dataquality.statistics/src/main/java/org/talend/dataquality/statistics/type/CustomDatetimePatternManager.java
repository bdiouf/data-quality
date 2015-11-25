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

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import org.talend.datascience.common.inference.type.SystemDatetimePatternManager;

/**
 * Customized date time pattern manager.
 * 
 * @author mzhao
 *
 */
public final class CustomDatetimePatternManager {

    /**
     * This method call the system date pattern manager to valid it again after the custom pattern does not not match.
     * 
     * @param value
     * @param customDatePattern
     * @param locale
     * @return
     */
    public static boolean isDate(String value, String customDatePattern) {
        boolean isMatch = isMatchCustomPattern(value, customDatePattern);
        if (isMatch) {
            return true;
        }
        // validate using system pattern manager
        return SystemDatetimePatternManager.isDate(value);
    }

    public static boolean isTime(String value, String customTimePattern) {
        boolean isMatch = isMatchCustomPattern(value, customTimePattern);
        if (isMatch) {
            return true;
        }
        // validate using system pattern manager
        return SystemDatetimePatternManager.isTime(value);
    }

    private static boolean isMatchCustomPattern(String value, String customDateTimePattern) {
        if (customDateTimePattern == null) {
            return false;
        }
        try {
            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(customDateTimePattern);
            dtFormatter.parse(value);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            // Cannot create DateTimeFormatter, or input data cannot match user defined pattern.
            return false;
        }
        return true;
    }

    public static String replaceByDateTimePattern(String value, String customPattern) {
        return replaceByDateTimePattern(value, Collections.singletonList(customPattern));
    }

    public static String replaceByDateTimePattern(String value, List<String> customPatterns) {
        for (String customPattern : customPatterns) {
            try {
                DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(customPattern);
                dtFormatter.parse(value);
                return customPattern;
            } catch (DateTimeParseException | IllegalArgumentException e) {
                // Cannot create DateTimeFormatter, or input data cannot match user defined pattern.
                continue;
            }
        }
        // replace with system date pattern manager.
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
