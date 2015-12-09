package org.talend.dataquality.statistics.type;

import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.talend.datascience.common.inference.type.SystemDatetimePatternManager;

public class DateTimePatternListGenerator {

    private final static ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.now();

    private final static FormatStyle[] FORMAT_STYLES = new FormatStyle[] { FormatStyle.SHORT, FormatStyle.MEDIUM,
            FormatStyle.LONG, FormatStyle.FULL };

    private static List<String> getBaseDateTimePatternsByLocales(List<String> knownPatternList) {

        Locale[] localeArray = new Locale[] { Locale.US, //
                Locale.FRANCE, //
                Locale.GERMANY, //
                Locale.UK,//
                Locale.ITALY, //
                Locale.CANADA, Locale.CANADA_FRENCH, //
                Locale.JAPAN, //
                Locale.CHINA, //
        };

        Set<String> dateTimePatternsSet = new LinkedHashSet<String>();
        for (Locale l : localeArray) {
            getFormatsOfLocale(dateTimePatternsSet, l, true);
        }
        dateTimePatternsSet.removeAll(knownPatternList);
        return new ArrayList<String>(dateTimePatternsSet);

    }

    private static List<String> getAdditionalDateTimePatternsByLocales(List<String> knownPatternList) {

        Set<String> dateTimePatternsSet = new LinkedHashSet<String>();

        for (String lang : Locale.getISOLanguages()) {
            getFormatsOfLocale(dateTimePatternsSet, new Locale(lang), false);
        }
        dateTimePatternsSet.removeAll(knownPatternList);

        return new ArrayList<String>(dateTimePatternsSet);

    }

    private static List<String> getISOAndRFCDateTimePatternList(List<String> knownPatternList) {

        List<String> patternList = new ArrayList<String>();

        patternList.add("yyyyMMddZ");// 1. BASIC_ISO_DATE
        patternList.add("yyyy-MM-ddZZZZZ");// 2. ISO_DATE
        patternList.add("yyyy-MM-dd'T'HH:mm:ss.SSS");// 3. ISO_DATE_TIME
        patternList.add("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");// 4. ISO_INSTANT
        patternList.add("yyyy-MM-dd");// 5. ISO_LOCAL_DATE
        patternList.add("yyyy-MM-dd'T'HH:mm:ss.SSS");// 6. ISO_LOCAL_DATE_TIME
        patternList.add("yyyy-MM-ddZZZZZ");// 7. ISO_OFFSET_DATE
        patternList.add("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");// 8. ISO_OFFSET_DATE_TIME
        patternList.add("yyyy-DZZZZZ");// 9. ISO_ORDINAL_DATE
        patternList.add("yyyy-'W'w-WZZZZZ");// 10. ISO_WEEK_DATE
        patternList.add("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ'['VV']'");// 11. ISO_ZONED_DATE_TIME
        patternList.add("EEE, d MMM yyyy HH:mm:ss Z");// 12. RFC_1123_DATE_TIME

        patternList.removeAll(knownPatternList);

        return patternList;
    }


    private static List<String> getNonExistentPatternsInOldFile(List<String> knownPatternList) {
        List<String> list = new ArrayList<String>(SystemDatetimePatternManager.DATE_PATTERN_NAMES);

        List<String> nonExistentPatterns = new ArrayList<String>();

        // sortDatePattern(list);
        for (String pattern : list) {
            if (!knownPatternList.contains(pattern)) {
                nonExistentPatterns.add(pattern);// TODO: add the pattern according to the regex not "pattern text"
                System.out.println(pattern + " \t" + getFormattedDateTime(pattern, Locale.US));
            }
        }
        return nonExistentPatterns;

    }

    @SuppressWarnings("unused")
    private static void validateISOPattens(List<String> isoPatternList) {

        Set<String> formattedDateTimeSet = new HashSet<String>();
        for (String pattern : isoPatternList) {
            formattedDateTimeSet.add(getFormattedDateTime(pattern, Locale.US));
        }

        DateTimeFormatter[] formatters = new DateTimeFormatter[] { DateTimeFormatter.BASIC_ISO_DATE, // 1
                DateTimeFormatter.ISO_DATE, // 2
                DateTimeFormatter.ISO_DATE_TIME, // 3
                // DateTimeFormatter.ISO_TIME, //
                DateTimeFormatter.ISO_INSTANT, // 4
                DateTimeFormatter.ISO_LOCAL_DATE, // 5
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,// 6
                // DateTimeFormatter.ISO_LOCAL_TIME, //
                DateTimeFormatter.ISO_OFFSET_DATE, // 7
                DateTimeFormatter.ISO_OFFSET_DATE_TIME, // 8
                // DateTimeFormatter.ISO_OFFSET_TIME, //
                DateTimeFormatter.ISO_ORDINAL_DATE, // 9
                DateTimeFormatter.ISO_WEEK_DATE, // 10
                DateTimeFormatter.ISO_ZONED_DATE_TIME, // 11
                DateTimeFormatter.RFC_1123_DATE_TIME, // 12
        };

        System.out.println("-------------Validate ISO PattenText-------------");
        for (int i = 0; i < formatters.length; i++) {

            System.out.print((i + 1) + "\t");
            try {
                String formattedDateTime = ZONED_DATE_TIME.format(formatters[i]);
                System.out.print(formattedDateTimeSet.contains(formattedDateTime) ? "YES\t" : "NO\t");
                System.out.println(formattedDateTime);
            } catch (Throwable t) {
                System.out.println(t.getMessage());
            }
        }

    }

    // TODO Sort not only by length but also by other conditions: DecimalStyle, hasZone? etc.
    private static void sortDatePattern(List<String> list) {
        Collections.sort(list, new Comparator<String>() {

            @Override
            public int compare(String s1, String s2) {

                Integer s1Length = s1.length();
                Integer s2length = s2.length();
                int lComp = s1Length.compareTo(s2length);

                if (lComp != 0) {
                    return lComp;
                } else {
                    int sComp = ("_" + s1).compareTo("_" + s2);
                    return sComp;
                }
            }
        });
    }

    private static void getFormatsOfLocale(Set<String> dateTimePatternsSet, Locale locale, boolean keepLongMonth) {
        getFormatByStyle(true, false, dateTimePatternsSet, locale, keepLongMonth);
        getFormatByStyle(true, true, dateTimePatternsSet, locale, keepLongMonth);

    }

    private static void getFormatByStyle(boolean isDateRequired, boolean isTimeRequired, Set<String> dateTimePatternsSet,
            Locale locale, boolean keepLongMonth) {
        for (FormatStyle style : FORMAT_STYLES) {
            String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(//
                    isDateRequired ? style : null, isTimeRequired ? style : null, IsoChronology.INSTANCE, locale);//

            if (!dateTimePatternsSet.contains(pattern)) {
                if (!keepLongMonth && (pattern.contains("MMMM") || pattern.contains("MMM") || pattern.contains("a"))) {
                    continue;
                }

                dateTimePatternsSet.add(pattern);
            }
        }
    }

    private static String getFormattedDateTime(String pattern, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
        try {
            String formattedDateTime = ZONED_DATE_TIME.format(formatter);
            return formattedDateTime;
        } catch (Throwable t) {
            return t.getMessage();
        }
    }

    public static void main(String[] args) {

        List<String> knownPatternList = new ArrayList<String>();

        // 1. Base Localized DateTimePatterns (java8 DateTimeFormatterBuilder)
        List<String> basePatternList = getBaseDateTimePatternsByLocales(knownPatternList);
        for (String pattern : basePatternList) {
            System.out.println(pattern);
        }
        knownPatternList.addAll(basePatternList);
        System.out.println("#basePatterns = " + basePatternList.size() + "\n");

        // 2. Additional Localized DateTimePatterns (java8 DateTimeFormatterBuilder)
        List<String> additionalPatternList = getAdditionalDateTimePatternsByLocales(knownPatternList);
        for (String pattern : additionalPatternList) {
            System.out.println(pattern);
        }
        knownPatternList.addAll(additionalPatternList);
        System.out.println("#additionalPatternList = " + additionalPatternList.size() + "\n");

        // 3. ISO and RFC DateTimePatterns
        List<String> isoPatternList = getISOAndRFCDateTimePatternList(knownPatternList);
        // validateISOPattens(isoPatternList);
        for (String pattern : isoPatternList) {
            System.out.println(pattern);
        }
        knownPatternList.addAll(isoPatternList);
        System.out.println("#DateTimePattern(ISO&RFC) = " + isoPatternList.size() + "\n");

        // TODO 4. add old DateTimePatterns
        // getNonExistentPatternsInOldFile(knownPatternList);

        System.out.println("#Total = " + knownPatternList.size() + //
                " (#basePatterns = " + basePatternList.size() + //
                ", #additionalPatterns = " + additionalPatternList.size() + //
                ", #isoPatterns = " + isoPatternList.size() + ")\n");//

    }

}