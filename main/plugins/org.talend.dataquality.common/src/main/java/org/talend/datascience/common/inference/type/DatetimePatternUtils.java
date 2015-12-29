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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhao Date time pattern utils.
 */
public class DatetimePatternUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatetimePatternUtils.class);

    private Pattern digits = Pattern.compile("[0-9]");

    private Pattern lowerAlph = Pattern.compile("[a-z]");

    private Pattern lowerAlphSpec = Pattern.compile("çâêîôûéèùïöü");

    private Pattern upperAlph = Pattern.compile("[A-Z]");

    private Pattern upperAlphSpec = Pattern.compile("ÇÂÊÎÔÛÉÈÙÏÖÜ");

    private Map<Pattern, String> dateParsers = new LinkedHashMap<Pattern, String>();

    private Map<Pattern, String> timeParsers = new LinkedHashMap<Pattern, String>();

    private static DatetimePatternUtils instance = null;

    private static String[] DATE_PATTERN_NAMES = new String[0];

    private static String[] TIME_PATTERN_NAMES = new String[0];

    private DatetimePatternUtils() {
        try {
            // Load date patterns
            DATE_PATTERN_NAMES = loadPatterns("datePatterns.txt", dateParsers);
            // Load time patterns
            TIME_PATTERN_NAMES= loadPatterns("timePatterns.txt", timeParsers);
        } catch (IOException e) {
            LOGGER.error("Unable to get date patterns.", e);
        }
    }

    private String[] loadPatterns(String patternFileName, Map<Pattern, String> patternParsers) throws IOException {
        InputStream stream;
        List<String> lines;
        int idx;
        stream = TypeInferenceUtils.class.getResourceAsStream(patternFileName);
        lines = IOUtils.readLines(stream);

        String[] patternNames = new String[lines.size()];
        idx = 0;
        for (String line : lines) {
            if (!"".equals(line.trim())) {
                String[] lineArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "=");
                String patternName = StringUtils.removeEnd(StringUtils.removeStart(lineArray[0], "\""), "\"");
                patternParsers.put(Pattern.compile(StringUtils.removeEnd(StringUtils.removeStart(lineArray[1], "\""), "\"")),
                        patternName);
                patternNames[idx++] = patternName;
            }
        }
        stream.close();
        return patternNames;
    }

    public static DatetimePatternUtils getInstance() {
        if (instance == null) {
            instance = new DatetimePatternUtils();
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
        return ArrayUtils.contains(DATE_PATTERN_NAMES, pattern);
    }

    /**
     * Whether given string pattern is a time pattern or not.
     * 
     * @param pattern
     * @return
     */
    public boolean isTimePattern(String pattern) {
        return ArrayUtils.contains(TIME_PATTERN_NAMES, pattern);
    }

    /**
     * Whether the given string value is a date or not.
     * 
     * @param value
     * @return true if the value is a date.
     */
    public boolean isDate(String value) {
        return isDateTime(dateParsers, value);
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
        return isDateTime(timeParsers, value);
    }

    /**
     * Replace the value with date pattern string.
     * 
     * @param value
     * @return date pattern string.
     */
    public String datePatternReplace(String value) {
        return dateTimePatternReplace(dateParsers, value);
    }

    /**
     * Replace the value with time pattern string.
     * 
     * @param value
     * @return
     */
    public String timePatternReplace(String value) {
        return dateTimePatternReplace(timeParsers, value);
    }

    private String dateTimePatternReplace(Map<Pattern, String> parsers, String value) {

        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }
        // Parse the value given list of date regex in pattern file.
        String patternToFind = "";
        for (Pattern parser : parsers.keySet()) {
            try {
                if (parser.matcher(value).find()) {
                    return parsers.get(parser);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        if (patternToFind.equals("")) {
            patternToFind = patternReplace(value);
        }
        return patternToFind;

    }

    /**
     * Replace the character in value with the predefined pattern character.
     * 
     * @param value
     * @return pattern string.
     */
    public String patternReplace(String value) {
        String replacedValue = digits.matcher(value).replaceAll("9");
        replacedValue = lowerAlph.matcher(replacedValue).replaceAll("a");
        replacedValue = upperAlph.matcher(replacedValue).replaceAll("A");
        replacedValue = lowerAlphSpec.matcher(replacedValue).replaceAll("a");
        replacedValue = upperAlphSpec.matcher(replacedValue).replaceAll("A");
        return replacedValue;
    }

    /**
     * Whether the patternString contains the predefined alpha character.
     * 
     * @param patternString
     * @return
     */
    public boolean containsAlphabetic(String patternString) {
        boolean containsLowerAhpa = lowerAlph.matcher(patternString).find();
        if (!containsLowerAhpa) {
            containsLowerAhpa = upperAlph.matcher(patternString).find();
            if (!containsLowerAhpa) {
                containsLowerAhpa = lowerAlphSpec.matcher(patternString).find();
                if (!containsLowerAhpa) {
                    containsLowerAhpa = upperAlphSpec.matcher(patternString).find();
                }
            }
        }
        return containsLowerAhpa;
    }

}
