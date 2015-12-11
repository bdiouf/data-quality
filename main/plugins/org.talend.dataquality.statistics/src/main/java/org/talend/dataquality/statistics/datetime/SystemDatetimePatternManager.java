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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.statistics.type.TypeInferenceUtils;

/**
 * Date and time patterns manager with system default definitions.
 * 
 * @author mzhao
 */
public class SystemDatetimePatternManager {

    private static final Logger LOGGER = Logger.getLogger(SystemDatetimePatternManager.class);

    private static final Locale DEFAULT_LOCALE = Locale.US;

    private static Map<Pattern, String> DATE_PARSERS = new LinkedHashMap<Pattern, String>();

    private static Map<Pattern, String> TIME_PARSERS = new LinkedHashMap<Pattern, String>();

    static {
        try {
            // Load date patterns
            loadPatterns("DateRegexes.txt", DATE_PARSERS);
            // Load time patterns
            loadPatterns("TimeRegexes.txt", TIME_PARSERS);
        } catch (IOException e) {
            LOGGER.error("Unable to get date patterns.", e);
        }

    }

    private static Set<String> loadPatterns(String patternFileName, Map<Pattern, String> patternParsers) throws IOException {
        InputStream stream = TypeInferenceUtils.class.getResourceAsStream(patternFileName);
        List<String> lines = IOUtils.readLines(stream);
        Set<String> patternNames = new ConcurrentSkipListSet<String>();
        for (String line : lines) {
            if (!"".equals(line.trim())) {
                String[] lineArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "=");
                String patternName = StringUtils.removeEnd(StringUtils.removeStart(lineArray[0], "\""), "\"");
                patternParsers.put(Pattern.compile(StringUtils.removeEnd(StringUtils.removeStart(lineArray[1], "\""), "\"")),
                        patternName);
                patternNames.add(patternName);
            }
        }
        stream.close();
        return patternNames;
    }

    /**
     * Whether the given string value is a date or not.
     * 
     * @param value
     * @return true if the value is a date.
     */
    public static boolean isDate(String value) {
        return isDateTime(DATE_PARSERS, value);
    }

    /**
     * Check if the value passed is a time or not.
     * 
     * @param value
     * @return true if the value is type "Time", false otherwise.
     */
    public static boolean isTime(String value) {
        return isDateTime(TIME_PARSERS, value);
    }

    private static boolean isDateTime(Map<Pattern, String> parsers, String value) {
        if (StringUtils.isNotEmpty(value)) {
            // 1. The length of date characters should not exceed 64.
            if (value.length() > 64) {
                return false;
            }
            // 2. Check it by list of patterns
            for (Pattern parser : parsers.keySet()) {
                try {
                    if (parser.matcher(value).find()) {
                        return true;
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return false;
    }

    /**
     * Replace the value with date pattern string.
     * 
     * @param value
     * @return date pattern string.
     */
    public static String datePatternReplace(String value) {
        return dateTimePatternReplace(DATE_PARSERS, value);
    }

    /**
     * Replace the value with time pattern string.
     * 
     * @param value
     * @return
     */
    public static String timePatternReplace(String value) {
        return dateTimePatternReplace(TIME_PARSERS, value);
    }

    private static String dateTimePatternReplace(Map<Pattern, String> parsers, String value) {
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }
        // Parse the value given list of date regex in pattern file.
        for (Pattern parser : parsers.keySet()) {
            try {
                if (parser.matcher(value).find()) {
                    return parsers.get(parser);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        return value;
    }

}
