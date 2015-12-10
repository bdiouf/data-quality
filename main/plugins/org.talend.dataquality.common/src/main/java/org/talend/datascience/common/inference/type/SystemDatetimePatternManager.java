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
package org.talend.datascience.common.inference.type;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Pattern;

/**
 * Date and time patterns manager with system default definitions.
 * 
 * @author mzhao
 */
public class SystemDatetimePatternManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemDatetimePatternManager.class);

    private static final Locale DEFAULT_LOCALE = Locale.US;

    private static Map<Pattern, String> DATE_PARSERS = new LinkedHashMap<Pattern, String>();

    private static Map<Pattern, String> TIME_PARSERS = new LinkedHashMap<Pattern, String>();

    private static Set<String> DATE_PATTERN_NAMES = new HashSet<String>();

    private static Set<String> TIME_PATTERN_NAMES = new HashSet<String>();

    static {
        try {
            // Load date patterns
            DATE_PATTERN_NAMES = loadPatterns("datePatterns.txt", DATE_PARSERS);
            // Load time patterns
            TIME_PATTERN_NAMES = loadPatterns("timePatterns.txt", TIME_PARSERS);
        } catch (IOException e) {
            LOGGER.error("Unable to get date patterns.", e);
        }

    }

    private static Set<String> loadPatterns(String patternFileName, Map<Pattern, String> patternParsers) throws IOException {
        InputStream stream;
        List<String> lines;
        stream = TypeInferenceUtils.class.getResourceAsStream(patternFileName);
        lines = IOUtils.readLines(stream);
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
     * Whether the given string pattern a date pattern or not.
     * 
     * @param pattern
     * @return true if the pattern string is a date pattern.
     */
    public static boolean isDatePattern(String pattern) {
        return DATE_PATTERN_NAMES.contains(pattern);
    }

    /**
     * Whether given string pattern is a time pattern or not.
     * 
     * @param pattern
     * @return
     */
    public static boolean isTimePattern(String pattern) {
        return TIME_PATTERN_NAMES.contains(pattern);
    }

    /**
     * Whether the given string value is a date or not using the default jvm locale.
     *
     * @param value the value to check if it's a date.
     * @param customDatePatterns the list of custom date patterns.
     * @return true if the value is a date.
     */
    public static boolean isDate(String value, List<String> customDatePatterns) {
        return isDate(value, customDatePatterns, Locale.getDefault());
    }

    /**
     * Whether the given string value is a date or not.
     *
     * @param value the value to check if it's a date.
     * @param customDatePatterns the list of custom date patterns.
     * @param locale the locale to use.
     * @return true if the value is a date.
     */
    public static boolean isDate(String value, List<String> customDatePatterns, Locale locale) {

        // try the custom patterns first
        for (String datePattern : customDatePatterns) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(datePattern, locale);
                format.parse(value);
                return true;
            } catch (Exception e) {

                // try the default locale if not already used
                if (!DEFAULT_LOCALE.equals(locale)) {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat(datePattern, DEFAULT_LOCALE);
                        format.parse(value);
                        return true;
                    } catch (Exception e1) {
                        // use next custom pattern
                    }
                }
            }
        }

        // fall back on registered ones
        return isDateTime(DATE_PARSERS, value);
    }

    /**
     * Whether the given string value is a date or not.
     * 
     * @param value
     * @return true if the value is a date.
     */
    public static boolean isDate(String value) {
        boolean isDate = isDateTime(DATE_PARSERS, value);
        return isDate;
    }

    private static boolean isDateTime(Map<Pattern, String> parsers, String value) {
        if (StringUtils.isNotEmpty(value)) {
            // 1. The length of date characters should not exceed 30.
            if (value.trim().length() > 30) {
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
     * Check if the value passed is a time or not.
     * 
     * @param value
     * @return true if the value is type "Time", false otherwise.
     */
    public static boolean isTime(String value) {
        boolean isTime = isDateTime(TIME_PARSERS, value);
        return isTime;
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
