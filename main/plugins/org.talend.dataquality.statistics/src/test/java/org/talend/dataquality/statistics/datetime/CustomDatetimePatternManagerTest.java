package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

public class CustomDatetimePatternManagerTest {

    @Test
    public void testDateMatchingCustomPattern() {
        // invalid with system time pattern
        assertFalse(SystemDatetimePatternManager.isDate("6/18/09 21:30"));

        // valid with custom pattern
        assertTrue(CustomDatetimePatternManager.isDate("6/18/09 21:30", Collections.<String> singletonList("M/d/yy H:m")));
        assertEquals("M/d/yy H:m", CustomDatetimePatternManager.replaceByDateTimePattern("6/18/09 21:30", "M/d/yy H:m"));

    }

    @Test
    public void testValidDateNotMatchingCustomPattern() {
        assertTrue(CustomDatetimePatternManager.isDate("6/18/2009 21:30", Collections.<String> singletonList("m-d-y hh:MM")));
        assertEquals("M/d/yyyy H:mm", CustomDatetimePatternManager.replaceByDateTimePattern("6/18/2009 21:30", "m-d-y hh:MM"));
    }

    @Test
    public void testInvalidDateNotMatchingCustomPattern() {
        assertFalse(CustomDatetimePatternManager.isDate("6/18/09 21:30", Collections.<String> singletonList("m-d-y hh:MM")));
        assertEquals("6/18/09 21:30", CustomDatetimePatternManager.replaceByDateTimePattern("6/18/09 21:30", "m-d-y hh:MM"));
    }

    @Test
    public void testValidDateWithInvalidPattern() {
        assertTrue(CustomDatetimePatternManager.isDate("6/18/2009 21:30",
                Collections.<String> singletonList("d/m/y**y hh:mm zzzzzzz")));
        assertEquals("M/d/yyyy H:mm",
                CustomDatetimePatternManager.replaceByDateTimePattern("6/18/2009 21:30", "d/m/y**y hh:mm zzzzzzz"));
    }

    @Test
    public void testTimeMatchingCustomPattern() {
        // invalid with system time pattern
        assertFalse(SystemDatetimePatternManager.isTime("21?30"));

        // valid with custom pattern
        assertTrue(CustomDatetimePatternManager.isTime("21?30", Collections.<String> singletonList("H?m")));
        assertEquals("H?m", CustomDatetimePatternManager.replaceByDateTimePattern("21?30", "H?m"));
    }

    @Test
    public void testValidTimeNotMatchingCustomPattern() {
        assertTrue(CustomDatetimePatternManager.isTime("21:30", Collections.<String> singletonList("H-m")));
        assertEquals("HH:mm", CustomDatetimePatternManager.replaceByDateTimePattern("21:30", "H-m"));
    }

    @Test
    public void testInvalidTimeNotMatchingCustomPattern() {
        assertFalse(CustomDatetimePatternManager.isTime("21?30", Collections.<String> singletonList("H-m")));
        assertEquals("21?30", CustomDatetimePatternManager.replaceByDateTimePattern("21?30", "H-m"));
    }

    @Test
    public void testValidTimeWithInvalidPattern() {
        assertTrue(CustomDatetimePatternManager.isTime("21:30", Collections.<String> singletonList("d/m/y**y hh:mm zzzzzzz")));
        assertEquals("HH:mm", CustomDatetimePatternManager.replaceByDateTimePattern("21:30", "d/m/y**y hh:mm zzzzzzz"));
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
            sb.append(CustomDatetimePatternManager.isDate(dates[i], pattern)).append("\t");
            sb.append(CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.US)).append("\t");
            sb.append(CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.FRANCE)).append("\t");
            sb.append(CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.GERMANY)).append("\t");
            sb.append(CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.CHINA)).append("\t");
            sb.append("\n");

            assertEquals(EXPECTED_IS_DATE_DEFAULT[i], CustomDatetimePatternManager.isDate(dates[i], pattern));
            assertEquals(EXPECTED_IS_DATE_US[i], CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.US));
            assertEquals(EXPECTED_IS_DATE_FR[i], CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.FRANCE));
            assertEquals(EXPECTED_IS_DATE_DE[i], CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.GERMANY));
            assertEquals(EXPECTED_IS_DATE_CN[i], CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.CHINA));
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
            sb.append(CustomDatetimePatternManager.isDate(dates[i], pattern)).append("\t");
            sb.append(CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.US)).append("\t");
            sb.append(CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.FRANCE)).append("\t");
            sb.append(CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.GERMANY)).append("\t");
            sb.append(CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.CHINA)).append("\t");
            sb.append("\n");

            assertEquals(EXPECTED_IS_DATE_DEFAULT[i], CustomDatetimePatternManager.isDate(dates[i], pattern));
            assertEquals(EXPECTED_IS_DATE_US[i], CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.US));
            assertEquals(EXPECTED_IS_DATE_FR[i], CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.FRANCE));
            assertEquals(EXPECTED_IS_DATE_DE[i], CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.GERMANY));
            assertEquals(EXPECTED_IS_DATE_CN[i], CustomDatetimePatternManager.isDate(dates[i], pattern, Locale.CHINA));
        }

        System.out.println(sb.toString());
    }
}
