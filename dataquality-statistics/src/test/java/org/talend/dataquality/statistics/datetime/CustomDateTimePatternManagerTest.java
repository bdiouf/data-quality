package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class CustomDateTimePatternManagerTest {

    @Test
    public void testNewPatterns_TDQ11229() {

        assertTrue(SystemDateTimePatternManager.isDate("18-NOV-86 01.00.00.000000000 AM"));
        assertTrue(SystemDateTimePatternManager.isDate("6/18/09"));
        assertTrue(SystemDateTimePatternManager.isDate("Jan.12.2010"));
        assertTrue(SystemDateTimePatternManager.isDate("14/Feb/2013 13:40:51 +0100"));
        assertTrue(SystemDateTimePatternManager.isDate("1970-01-01T00:32:43"));
        assertTrue(SystemDateTimePatternManager.isDate("05/15/1962"));

    }

    private List<String> readLineContentsFromFile(String path) throws IOException {
        InputStream dateInputStream = SystemDateTimePatternManager.class.getResourceAsStream(path);
        List<String> contents = new ArrayList<String>();

        List<String> lines = IOUtils.readLines(dateInputStream);
        for (String line : lines) {
            int indexComment1 = line.indexOf("#");
            if (indexComment1 >= 0) {
                line = line.substring(0, indexComment1);
            }
            int indexComment2 = line.indexOf("//");
            if (indexComment2 >= 0) {
                line = line.substring(0, indexComment2);
            }
            if (line.trim().length() > 0) {
                contents.add(line.trim());
            }
        }

        return contents;
    }

    @Test
    public void testValidDatesFromFile() throws IOException {

        List<String> contents = readLineContentsFromFile("ListOfValidDates.txt");

        for (String line : contents) {
            if (!line.isEmpty()) {
                assertTrue("Unexpected Invalid Date: " + line, SystemDateTimePatternManager.isDate(line));
            }
        }
    }

    @Test
    public void testInvalidDatesFromFile() throws IOException {

        List<String> contents = readLineContentsFromFile("ListOfInvalidDates.txt");

        for (String line : contents) {
            if (!line.isEmpty()) {
                assertFalse("Unexpected Valid Date: " + line, SystemDateTimePatternManager.isDate(line));
            }
        }
    }

    @Test
    public void testDateMatchingCustomPattern() {
        // invalid with system time pattern
        assertFalse(SystemDateTimePatternManager.isDate("6/18/09 21:30"));

        // valid with custom pattern
        assertTrue(CustomDateTimePatternManager.isDate("6/18/09 21:30", Collections.<String> singletonList("M/d/yy H:m")));
        assertEquals(Collections.singleton("M/d/yy H:m"),
                CustomDateTimePatternManager.replaceByDateTimePattern("6/18/09 21:30", "M/d/yy H:m"));

    }

    @Test
    public void testValidDateNotMatchingCustomPattern() {
        assertTrue(CustomDateTimePatternManager.isDate("6/18/2009 21:30", Collections.<String> singletonList("m-d-y hh:MM")));
        assertEquals(new HashSet<String>(Arrays.asList(new String[] { "M/d/yyyy H:mm", "M/d/yyyy HH:mm" })),
                CustomDateTimePatternManager.replaceByDateTimePattern("6/18/2009 21:30", "m-d-y hh:MM"));
    }

    @Test
    public void testInvalidDateNotMatchingCustomPattern() {
        assertFalse(CustomDateTimePatternManager.isDate("6/18/09 21:30", Collections.<String> singletonList("m-d-y hh:MM")));
        assertEquals(Collections.EMPTY_SET, CustomDateTimePatternManager.replaceByDateTimePattern("6/18/09 21:30", "m-d-y hh:MM"));
    }

    @Test
    public void testValidDateWithInvalidPattern() {
        assertTrue(CustomDateTimePatternManager.isDate("6/18/2009 21:30",
                Collections.<String> singletonList("d/m/y**y hh:mm zzzzzzz")));
        assertEquals(new HashSet<String>(Arrays.asList(new String[] { "M/d/yyyy H:mm", "M/d/yyyy HH:mm" })),
                CustomDateTimePatternManager.replaceByDateTimePattern("6/18/2009 21:30", "d/m/y**y hh:mm zzzzzzz"));
    }

    @Test
    public void testTimeMatchingCustomPattern() {
        // invalid with system time pattern
        assertFalse(SystemDateTimePatternManager.isTime("21?30"));

        // valid with custom pattern
        assertTrue(CustomDateTimePatternManager.isTime("21?30", Collections.<String> singletonList("H?m")));
        assertEquals(Collections.singleton("H?m"), CustomDateTimePatternManager.replaceByDateTimePattern("21?30", "H?m"));
    }

    @Test
    public void testValidTimeNotMatchingCustomPattern() {
        assertTrue(CustomDateTimePatternManager.isTime("21:30", Collections.<String> singletonList("H-m")));
        assertEquals(new HashSet<String>(Arrays.asList(new String[] { "HH:mm", "H:mm" })),
                CustomDateTimePatternManager.replaceByDateTimePattern("21:30", "H-m"));
    }

    @Test
    public void testInvalidTimeNotMatchingCustomPattern() {
        assertFalse(CustomDateTimePatternManager.isTime("21?30", Collections.<String> singletonList("H-m")));
        assertEquals(Collections.EMPTY_SET, CustomDateTimePatternManager.replaceByDateTimePattern("21?30", "H-m"));
    }

    @Test
    public void testValidTimeWithInvalidPattern() {
        assertTrue(CustomDateTimePatternManager.isTime("21:30", Collections.<String> singletonList("d/m/y**y hh:mm zzzzzzz")));
        assertEquals(new HashSet<String>(Arrays.asList(new String[] { "HH:mm", "H:mm" })),
                CustomDateTimePatternManager.replaceByDateTimePattern("21:30", "d/m/y**y hh:mm zzzzzzz"));
    }

    @Test
    public void testDateWithLocaleFR() {
        // simulate a JVM
        Locale.setDefault(Locale.FRANCE);

        final List<String> pattern = Collections.<String> singletonList("MMMM d ?? yyyy");
        final String[] dates = new String[] { "January 9 ?? 1970",// EN
                "janvier 9 ?? 1970", // FR
                "Januar 9 ?? 1970", // DE
                "一月 9 ?? 1970", // CN
        };
        final boolean[] EXPECTED_IS_DATE_DEFAULT = new boolean[] { true, false, false, false };
        final boolean[] EXPECTED_IS_DATE_US = new boolean[] { true, false, false, false };
        final boolean[] EXPECTED_IS_DATE_FR = new boolean[] { true, true, false, false };
        final boolean[] EXPECTED_IS_DATE_DE = new boolean[] { true, false, true, false };
        final boolean[] EXPECTED_IS_DATE_CN = new boolean[] { true, false, false, true };
        // final String[] EXPECTED_PATTERN_STRING = new String[] { "", };

        StringBuilder sb = new StringBuilder();
        sb.append("-------------- JVM Locale: " + Locale.getDefault().toString() + " ------\n");
        sb.append("Input \\ UserLocale\tN/A\tEN\tFR\tDE\tCN\n");
        for (int i = 0; i < dates.length; i++) {
            sb.append(dates[i]).append("   \t");
            sb.append(CustomDateTimePatternManager.isDate(dates[i], pattern)).append("\t");
            sb.append(CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.US)).append("\t");
            sb.append(CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.FRANCE)).append("\t");
            sb.append(CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.GERMANY)).append("\t");
            sb.append(CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.CHINA)).append("\t");
            sb.append("\n");

            assertEquals(EXPECTED_IS_DATE_DEFAULT[i], CustomDateTimePatternManager.isDate(dates[i], pattern));
            assertEquals(EXPECTED_IS_DATE_US[i], CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.US));
            assertEquals(EXPECTED_IS_DATE_FR[i], CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.FRANCE));
            assertEquals(EXPECTED_IS_DATE_DE[i], CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.GERMANY));
            assertEquals(EXPECTED_IS_DATE_CN[i], CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.CHINA));
        }

        System.out.println(sb.toString());
    }

    @Test
    public void testDateWithLocaleDE() {
        // simulate a JVM
        Locale.setDefault(Locale.GERMANY);

        final List<String> pattern = Collections.<String> singletonList("MMMM d ?? yyyy");
        final String[] dates = new String[] { "January 9 ?? 1970",// EN
                "janvier 9 ?? 1970", // FR
                "Januar 9 ?? 1970", // DE
                "一月 9 ?? 1970", // CN
        };
        final boolean[] EXPECTED_IS_DATE_DEFAULT = new boolean[] { true, false, false, false };
        final boolean[] EXPECTED_IS_DATE_US = new boolean[] { true, false, false, false };
        final boolean[] EXPECTED_IS_DATE_FR = new boolean[] { true, true, false, false };
        final boolean[] EXPECTED_IS_DATE_DE = new boolean[] { true, false, true, false };
        final boolean[] EXPECTED_IS_DATE_CN = new boolean[] { true, false, false, true };
        // final String[] EXPECTED_PATTERN_STRING = new String[] { "", };

        StringBuilder sb = new StringBuilder();
        sb.append("-------------- JVM Locale: " + Locale.getDefault().toString() + " ------\n");
        sb.append("Input \\ UserLocale\tN/A\tEN\tFR\tDE\tCN\n");
        for (int i = 0; i < dates.length; i++) {
            sb.append(dates[i]).append("   \t");
            sb.append(CustomDateTimePatternManager.isDate(dates[i], pattern)).append("\t");
            sb.append(CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.US)).append("\t");
            sb.append(CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.FRANCE)).append("\t");
            sb.append(CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.GERMANY)).append("\t");
            sb.append(CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.CHINA)).append("\t");
            sb.append("\n");

            assertEquals(EXPECTED_IS_DATE_DEFAULT[i], CustomDateTimePatternManager.isDate(dates[i], pattern));
            assertEquals(EXPECTED_IS_DATE_US[i], CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.US));
            assertEquals(EXPECTED_IS_DATE_FR[i], CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.FRANCE));
            assertEquals(EXPECTED_IS_DATE_DE[i], CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.GERMANY));
            assertEquals(EXPECTED_IS_DATE_CN[i], CustomDateTimePatternManager.isDate(dates[i], pattern, Locale.CHINA));
        }

        System.out.println(sb.toString());
    }

    @Test
    public void testSpecialCases() {
        assertEquals(Collections.singleton("MMMM d yyyy"),
                CustomDateTimePatternManager.replaceByDateTimePattern("July 14 2015", "M/d/yy H:m"));

    }
}
