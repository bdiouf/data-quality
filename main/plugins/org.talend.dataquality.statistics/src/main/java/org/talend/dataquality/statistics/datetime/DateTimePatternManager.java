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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Date and time patterns manager with system default definitions.
 * 
 * @author mzhao
 */
public class DateTimePatternManager {

    // private static final Locale DEFAULT_LOCALE = Locale.US;

    private static Map<DateTimeFormatter, String> DATE_PARSERS = new LinkedHashMap<DateTimeFormatter, String>();

    private static Map<DateTimeFormatter, String> TIME_PARSERS = new LinkedHashMap<DateTimeFormatter, String>();

    // public static Set<String> DATE_PATTERN_NAMES = new HashSet<String>();
    //
    // private static Set<String> TIME_PATTERN_NAMES = new HashSet<String>();

    static {
        try {
            // Load date patterns
            // DATE_PATTERN_NAMES =
            loadPatterns("DateTimePatterns.txt");
        } catch (IOException e) {
            System.err.println("Unable to get date patterns.");
        }

    }

    private static void loadPatterns(String patternFileName) throws IOException {
        InputStream stream;
        List<String> lines;
        stream = DateTimePatternManager.class.getResourceAsStream(patternFileName);
        lines = IOUtils.readLines(stream);
        // Set<String> patternNames = new ConcurrentSkipListSet<String>();
        for (String line : lines) {
            if (!"".equals(line.trim())) {
                String[] localePatternText = line.trim().split("\t");
                DATE_PARSERS.put(DateTimeFormatter.ofPattern(localePatternText[1], getLocaleFromStr(localePatternText[0])),
                        localePatternText[1]);// ???
                // Locale
                // patternNames.add(localePatternText);
            }
        }
        stream.close();
        // return patternNames;
    }

    private static Locale getLocaleFromStr(String localeStr) {
        return Locale.US;
    }
    /**
     * Whether the given string pattern a date pattern or not.
     * 
     * @param pattern
     * @return true if the pattern string is a date pattern.
     */
    // public static boolean isDatePattern(String pattern) {
    // return DATE_PATTERN_NAMES.contains(pattern);
    // }

    /**
     * Whether given string pattern is a time pattern or not.
     * 
     * @param pattern
     * @return
     */
    // public static boolean isTimePattern(String pattern) {
    // return TIME_PATTERN_NAMES.contains(pattern);
    // }

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
            try {// firstly, try with user-defined locale
                DateTimeFormatter.ofPattern(datePattern, locale).parse(value);
                return true;
            } catch (DateTimeParseException | IllegalArgumentException e) {
                if (!Locale.US.equals(locale)) {
                    try {// try with LOCALE_US if user defined locale is not US
                        DateTimeFormatter.ofPattern(datePattern, Locale.US).parse(value);
                        return true;
                    } catch (DateTimeParseException | IllegalArgumentException e1) {
                        // continue
                    }
                }
                if (!Locale.getDefault().equals(locale) && !Locale.getDefault().equals(Locale.US)) {
                    try {// try with LOCALE_JVM if none of the above matches
                        DateTimeFormatter.ofPattern(datePattern, Locale.getDefault()).parse(value);
                        return true;
                    } catch (DateTimeParseException | IllegalArgumentException e2) {
                        // no more try
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

    private static boolean isDateTime(Map<DateTimeFormatter, String> parsers, String value) {
        if (StringUtils.isNotEmpty(value)) {
            // 1. The length of date characters should not exceed 30.
            if (value.trim().length() > 30) {
                return false;
            }
            // 2. Check it by list of patterns
            for (DateTimeFormatter formatter : parsers.keySet()) {
                try {
                    if (formatter.parse(value) != null) {
                        return true;
                    }
                } catch (Exception e) {
                    continue;
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

    private static String dateTimePatternReplace(Map<DateTimeFormatter, String> parsers, String value) {

        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }
        // Parse the value given list of date regex in pattern file.
        for (DateTimeFormatter formatter : parsers.keySet()) {
            try {
                if (formatter.parse(value) != null) {
                    return parsers.get(formatter);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        return value;

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
