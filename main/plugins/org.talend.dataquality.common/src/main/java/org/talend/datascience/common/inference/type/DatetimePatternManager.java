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

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage date and time patterns. it is thread-safe.
 * <p>
 * This class provides the main date and pattern management entry point for loading predefined patterns from file
 * datePatterns.txt and timePatterns.txt. <br>
 * For the patterns that are not defined, user provides customized patterns with method
 * {@link #addCustomizedDatePattern(String)} and {@link #addCustomizedTimePattern(String)}
 * <p>
 * For example: <blockquote>
 * 
 * <pre>
 * 
 * 
 * DatetimePatternManager patternUtil = DatetimePatternManager.getInstance();
 * 
 * // Set a customized pattern
 * patternUtil.addCustomizedDatePattern(&quot;M/d/yy H:m&quot;);
 * patternUtil.isDate(&quot;6/18/09 21:30&quot;); // return true
 * 
 * </pre>
 * 
 * </blockquote>
 * <p>
 * This class is a singleton which is used across application level.
 * 
 * @author zhao
 */
public final class DatetimePatternManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatetimePatternManager.class);

    private Map<Pattern, String> dateParsers = new LinkedHashMap<Pattern, String>();

    private Map<Pattern, String> timeParsers = new LinkedHashMap<Pattern, String>();

    /**
     * A date formatter instance to pattern map storing all customized date patterns.
     */
    private Map<DateTimeFormatter, String> dateFormatter2pattern = new ConcurrentHashMap<DateTimeFormatter, String>();

    private Map<DateTimeFormatter, String> timeFormatter2pattern = new ConcurrentHashMap<DateTimeFormatter, String>();

    private static DatetimePatternManager instance = null;

    private static Set<String> DATE_PATTERN_NAMES = new ConcurrentSkipListSet<String>();

    private static Set<String> TIME_PATTERN_NAMES = new ConcurrentSkipListSet<String>();

    /**
     * Locale used to created the date time formatter.
     */
    private Locale locale = Locale.getDefault();

    private DatetimePatternManager() {
        try {
            // Load date patterns
            DATE_PATTERN_NAMES = loadPatterns("datePatterns.txt", dateParsers);
            // Load time patterns
            TIME_PATTERN_NAMES = loadPatterns("timePatterns.txt", timeParsers);
        } catch (IOException e) {
            LOGGER.error("Unable to get date patterns.", e);
        }
    }

    /**
     * Set user defined locale.
     * 
     * @param newLocale
     */
    public void setLocale(Locale newLocale) {
        this.locale = newLocale;
    }

    /**
     * Append customized date pattern to in-memory map.
     * 
     * @param pattern
     */
    public void addCustomizedDatePattern(String pattern) {
        addCustomizedDateTimePattern(pattern, dateFormatter2pattern, DATE_PATTERN_NAMES);
    }

    /**
     * Append customized time pattern to in-memory map.
     * 
     * @param pattern
     */
    public void addCustomizedTimePattern(String pattern) {
        addCustomizedDateTimePattern(pattern, timeFormatter2pattern, TIME_PATTERN_NAMES);
    }

    private void addCustomizedDateTimePattern(String pattern, Map<DateTimeFormatter, String> datetime2pattern,
            Set<String> patternNames) {
        if (StringUtils.isEmpty(pattern)) {
            return;
        }
        if (!datetime2pattern.values().contains(pattern.trim())) {
            datetime2pattern.put(DateTimeFormatter.ofPattern(pattern, locale), pattern);
            patternNames.add(pattern);
        }
    }

    private Set<String> loadPatterns(String patternFileName, Map<Pattern, String> patternParsers) throws IOException {
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

    public static DatetimePatternManager getInstance() {
        if (instance == null) {
            instance = new DatetimePatternManager();
        }
        return instance;
    }

    /**
     * Whether the given string pattern a date pattern or not.
     * 
     * @param pattern
     * @return true if the pattern string is a date pattern.
     */
    public boolean isDatePattern(String pattern) {
        return DATE_PATTERN_NAMES.contains(pattern);
    }

    /**
     * Whether given string pattern is a time pattern or not.
     * 
     * @param pattern
     * @return
     */
    public boolean isTimePattern(String pattern) {
        return TIME_PATTERN_NAMES.contains(pattern);
    }

    /**
     * Whether the given string value is a date or not.
     * 
     * @param value
     * @return true if the value is a date.
     */
    public boolean isDate(String value) {
        boolean isDate = isDateTime(dateParsers, value);
        if (!isDate) {
            // Find from customized date time formatters
            isDate = isCustomizedDateTime(value, dateFormatter2pattern);
        }
        return isDate;
    }

    private boolean isDateTime(Map<Pattern, String> parsers, String value) {
        for (Pattern parser : parsers.keySet()) {
            try {
                if (parser.matcher(value).find()) {
                    return true;
                }
            } catch (Exception e) {
                // ignore
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
    public boolean isTime(String value) {
        boolean isTime = isDateTime(timeParsers, value);
        if (!isTime) {
            // Find from customized date time formatters
            isTime = isCustomizedDateTime(value, timeFormatter2pattern);
        }
        return isTime;
    }

    private boolean isCustomizedDateTime(String value, Map<DateTimeFormatter, String> dateTimeFormatter2pattern) {
        boolean isDateTime = false;
        for (DateTimeFormatter df : dateTimeFormatter2pattern.keySet()) {
            try {
                df.parse(value);
                // find the pattern
                isDateTime = true;
                break;
            } catch (DateTimeParseException e) {
                // parse exception, no action to do.
                continue;
            }
        }
        return isDateTime;
    }

    /**
     * Replace the value with date pattern string.
     * 
     * @param value
     * @return date pattern string.
     */
    public String datePatternReplace(String value) {
        return dateTimePatternReplace(dateParsers, value, dateFormatter2pattern);
    }

    /**
     * Replace the value with time pattern string.
     * 
     * @param value
     * @return
     */
    public String timePatternReplace(String value) {
        return dateTimePatternReplace(timeParsers, value, timeFormatter2pattern);
    }

    private String dateTimePatternReplace(Map<Pattern, String> parsers, String value,
            Map<DateTimeFormatter, String> customizedFormatter2pattern) {

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
        // Find from customized pattern
        for (DateTimeFormatter df : customizedFormatter2pattern.keySet()) {
            try {
                df.parse(value);
                // find the pattern
                return customizedFormatter2pattern.get(df);
            } catch (DateTimeParseException e) {
                // parse exception, no action to do.
                continue;
            }
        }
        return value;

    }

}
