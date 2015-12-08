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

    private static Set<String> getBaseDateTimePatternsByLocales() {

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
        return dateTimePatternsSet;

    }

    private static Set<String> getAdditionalDateTimePatternsByLocales() {

        Set<String> dateTimePatternsSet = new LinkedHashSet<String>();

        for (String lang : Locale.getISOLanguages()) {
            getFormatsOfLocale(dateTimePatternsSet, new Locale(lang), false);
        }

        return dateTimePatternsSet;

    }

    private static void getFormatsOfLocale(Set<String> dateTimePatternsSet, Locale locale, boolean keepLongMonth) {
        System.out.println("------------------------------------------------------");
        System.out.println("Locale: " + locale);
        getFormatByStyle(true, false, dateTimePatternsSet, locale, keepLongMonth);
        getFormatByStyle(true, true, dateTimePatternsSet, locale, keepLongMonth);
    }

    private static void getFormatByStyle(boolean isDateRequired, boolean isTimeRequired, Set<String> dateTimePatternsSet,
            Locale locale, boolean keepLongMonth) {
        for (FormatStyle style : FORMAT_STYLES) {
            String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(//
                    isDateRequired ? style : null, isTimeRequired ? style : null, IsoChronology.INSTANCE, locale);//

            // pattern = pattern.replace("dd", "d").replace("MMMM", "4444").replace("MMM", "333").replace("MM", "M")
            // .replace("333", "MMM").replace("4444", "MMMM").replace("HH", "H").replace("hh", "h").replace("mm", "m")
            // .replace("ss", "s");

            if (!dateTimePatternsSet.contains(pattern)) {
                if (!keepLongMonth && (pattern.contains("MMMM") || pattern.contains("MMM") || pattern.contains("a"))) {
                    continue;
                }
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern, locale);

                System.out.println(pattern + " \t" + ZONED_DATE_TIME.format(dtf));
                dateTimePatternsSet.add(pattern);
            }
        }
    }

    private static List<String> calculateISOList() {
        List<String> list = new ArrayList<String>(getBaseDateTimePatternsByLocales());
        sortDatePattern(list);

        System.out.println("---------------------Base Patterns----------------------");
        for (String pattern : list) {
            System.out.println(pattern);
        }

        List<String> list2 = new ArrayList<String>(getAdditionalDateTimePatternsByLocales());
        sortDatePattern(list2);

        System.out.println("---------------------Additional Patterns----------------------");
        for (String pattern : list2) {
            if (!list.contains(pattern)) {
                System.out.println(pattern);
            }
        }

        list.addAll(list2);
        return list;

    }

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

    private static List<String> getNonExistentPatternsInOldFile(Set<String> old, List<String> lookup) {
        List<String> list = new ArrayList<String>(old);

        List<String> nonExistentPatterns = new ArrayList<String>();

        sortDatePattern(list);
        for (String pattern : list) {
            if (!lookup.contains(pattern)) {
                nonExistentPatterns.add(pattern);
                System.out.println("OLD >> " + pattern + " \t" + getFormattedDateTime(pattern));
            }
        }
        return nonExistentPatterns;

    }

    private static List<String> getOtherISOFormats(Set<String> formattedDateTimeSet) {

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

        System.out.println("-------------Other ISO formats-------------");
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

        return new ArrayList<String>();
    }

    private static String getFormattedDateTime(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            String formattedDateTime = ZONED_DATE_TIME.format(formatter);
            return formattedDateTime;
        } catch (Throwable t) {
            return t.getMessage();
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        List<String> patternList = calculateISOList();
        System.out.println("ISO count: " + patternList.size());

        List<String> oldPatterns = getNonExistentPatternsInOldFile(SystemDatetimePatternManager.DATE_PATTERN_NAMES, patternList);
        System.out.println("OLD count: " + oldPatterns.size());
        patternList.addAll(oldPatterns);

        System.out.println("\nTotal count: " + patternList.size());

        patternList.add("yyyyMMddZ");// 1
        patternList.add("yyyy-MM-ddZZZZZ");// 2
        patternList.add("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ'['VV']'");// 3
        patternList.add("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");// 4

        patternList.add("yyyy-MM-dd'T'HH:mm:ss.SSS");// 6

        patternList.add("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");// 8
        patternList.add("yyyy-DZZZZZ");// 9
        patternList.add("yyyy-'W'w-WZZZZZ");// 10
        patternList.add("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ'['VV']'");// 11
        patternList.add("EEE, d MMM yyyy HH:mm:ss Z");// 12

        Set<String> formattedDateTimeSet = new HashSet<String>();
        for (String pattern : patternList) {
            formattedDateTimeSet.add(getFormattedDateTime(pattern));
            System.out.println(pattern);
        }

        List<String> isoPatterns = getOtherISOFormats(formattedDateTimeSet);

    }
}
