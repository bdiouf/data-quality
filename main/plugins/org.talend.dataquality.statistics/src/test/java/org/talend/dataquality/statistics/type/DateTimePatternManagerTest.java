package org.talend.dataquality.statistics.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.talend.datascience.common.inference.type.SystemDatetimePatternManager;

public class DateTimePatternManagerTest {

    @Test
    public void testNew() {
        // US invalid date
        assertFalse(DateTimePatternManager.isDate("6/18/2009 21:30"));

        // US valid date
        assertTrue(DateTimePatternManager.isDate("6/18/09 9:30 PM"));
        assertTrue(DateTimePatternManager.isDate("6/18/09 09:30 PM"));

        assertFalse(DateTimePatternManager.isDate("18-NOV-86 01.00.00.000000000 AM"));
        assertTrue(DateTimePatternManager.isDate("6/18/09"));
        assertFalse(DateTimePatternManager.isDate("Jan.12.2010"));
        assertFalse(DateTimePatternManager.isDate("14/Feb/2013:13:40:51 +0100"));
        assertTrue(DateTimePatternManager.isDate("1970-01-01T00:32:43"));

    }

    @Test
    public void testDateMatchingCustomPattern() {
        // invalid with system time pattern
        assertFalse(SystemDatetimePatternManager.isDate("6/18/09 21:30"));
        assertTrue(SystemDatetimePatternManager.isDate("6/18/2009 21:30"));

        // valid with custom pattern
        assertTrue(DateTimePatternManager.isDate("6/18/09 21:30", Collections.<String> singletonList("M/d/yy H:m")));
        assertEquals("M/d/yy H:m", DateTimePatternManager.replaceByDateTimePattern("6/18/09 21:30", "M/d/yy H:m"));

    }

    @Test
    public void testValidDateNotMatchingCustomPattern() {
        assertTrue(DateTimePatternManager.isDate("6/18/2009 21:30", Collections.<String> singletonList("m-d-y hh:MM")));
        assertEquals("M/d/yyyy H:m", DateTimePatternManager.replaceByDateTimePattern("6/18/2009 21:30", "m-d-y hh:MM"));
    }

    @Test
    public void testInvalidDateNotMatchingCustomPattern() {
        assertFalse(DateTimePatternManager.isDate("6/18/09 21:30", Collections.<String> singletonList("m-d-y hh:MM")));
        assertEquals("6/18/09 21:30", DateTimePatternManager.replaceByDateTimePattern("6/18/09 21:30", "m-d-y hh:MM"));
    }

    @Test
    public void testValidDateWithInvalidPattern() {
        assertTrue(DateTimePatternManager.isDate("6/18/2009 21:30", Collections.<String> singletonList("d/m/y**y hh:mm zzzzzzz")));
        assertEquals("M/d/yyyy H:m", DateTimePatternManager.replaceByDateTimePattern("6/18/2009 21:30", "d/m/y**y hh:mm zzzzzzz"));
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
        final boolean[] EXPECTED_IS_DATE_DEFAULT = new boolean[] { true, true, false, false };
        final boolean[] EXPECTED_IS_DATE_US = new boolean[] { true, true, false, false };
        final boolean[] EXPECTED_IS_DATE_FR = new boolean[] { true, true, false, false };
        final boolean[] EXPECTED_IS_DATE_DE = new boolean[] { true, true, true, false };
        final boolean[] EXPECTED_IS_DATE_CN = new boolean[] { true, true, false, true };
        // final String[] EXPECTED_PATTERN_STRING = new String[] { "", };

        StringBuilder sb = new StringBuilder();
        sb.append("-------------- JVM Locale: " + Locale.getDefault().toString() + " ------\n");
        sb.append("Input \\ UserLocale\tN/A\tEN\tFR\tDE\tCN\n");
        for (int i = 0; i < dates.length; i++) {
            sb.append(dates[i]).append("   \t");
            sb.append(DateTimePatternManager.isDate(dates[i], pattern)).append("\t");
            sb.append(DateTimePatternManager.isDate(dates[i], pattern, Locale.US)).append("\t");
            sb.append(DateTimePatternManager.isDate(dates[i], pattern, Locale.FRANCE)).append("\t");
            sb.append(DateTimePatternManager.isDate(dates[i], pattern, Locale.GERMANY)).append("\t");
            sb.append(DateTimePatternManager.isDate(dates[i], pattern, Locale.CHINA)).append("\t");
            sb.append("\n");

            assertEquals(EXPECTED_IS_DATE_DEFAULT[i], DateTimePatternManager.isDate(dates[i], pattern));
            assertEquals(EXPECTED_IS_DATE_US[i], DateTimePatternManager.isDate(dates[i], pattern, Locale.US));
            assertEquals(EXPECTED_IS_DATE_FR[i], DateTimePatternManager.isDate(dates[i], pattern, Locale.FRANCE));
            assertEquals(EXPECTED_IS_DATE_DE[i], DateTimePatternManager.isDate(dates[i], pattern, Locale.GERMANY));
            assertEquals(EXPECTED_IS_DATE_CN[i], DateTimePatternManager.isDate(dates[i], pattern, Locale.CHINA));
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
        final boolean[] EXPECTED_IS_DATE_DEFAULT = new boolean[] { true, false, true, false };
        final boolean[] EXPECTED_IS_DATE_US = new boolean[] { true, false, true, false };
        final boolean[] EXPECTED_IS_DATE_FR = new boolean[] { true, true, true, false };
        final boolean[] EXPECTED_IS_DATE_DE = new boolean[] { true, false, true, false };
        final boolean[] EXPECTED_IS_DATE_CN = new boolean[] { true, false, true, true };
        // final String[] EXPECTED_PATTERN_STRING = new String[] { "", };

        StringBuilder sb = new StringBuilder();
        sb.append("-------------- JVM Locale: " + Locale.getDefault().toString() + " ------\n");
        sb.append("Input \\ UserLocale\tN/A\tEN\tFR\tDE\tCN\n");
        for (int i = 0; i < dates.length; i++) {
            sb.append(dates[i]).append("   \t");
            sb.append(DateTimePatternManager.isDate(dates[i], pattern)).append("\t");
            sb.append(DateTimePatternManager.isDate(dates[i], pattern, Locale.US)).append("\t");
            sb.append(DateTimePatternManager.isDate(dates[i], pattern, Locale.FRANCE)).append("\t");
            sb.append(DateTimePatternManager.isDate(dates[i], pattern, Locale.GERMANY)).append("\t");
            sb.append(DateTimePatternManager.isDate(dates[i], pattern, Locale.CHINA)).append("\t");
            sb.append("\n");

            assertEquals(EXPECTED_IS_DATE_DEFAULT[i], DateTimePatternManager.isDate(dates[i], pattern));
            assertEquals(EXPECTED_IS_DATE_US[i], DateTimePatternManager.isDate(dates[i], pattern, Locale.US));
            assertEquals(EXPECTED_IS_DATE_FR[i], DateTimePatternManager.isDate(dates[i], pattern, Locale.FRANCE));
            assertEquals(EXPECTED_IS_DATE_DE[i], DateTimePatternManager.isDate(dates[i], pattern, Locale.GERMANY));
            assertEquals(EXPECTED_IS_DATE_CN[i], DateTimePatternManager.isDate(dates[i], pattern, Locale.CHINA));
        }

        System.out.println(sb.toString());
    }
}
