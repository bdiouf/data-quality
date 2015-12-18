package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class SampleTest {

    private static List<String> DATE_SAMPLES;

    private static List<String> TIME_SAMPLES;

    private final Map<String, Set<String>> EXPECTED_FORMATS = new LinkedHashMap<String, Set<String>>() {

        private static final long serialVersionUID = 1L;

        {
            put("3/11/22", new HashSet<String>(Arrays.asList(new String[] //
                    { "M/d/yy", "d/MM/yy" })));
            put("11/03/22", new HashSet<String>(Arrays.asList(new String[] //
                    { "yy/MM/dd", "d/MM/yy", "dd/MM/yy" })));
            put("11.03.22", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd.MM.yy", "d.MM.yy" })));
            put("22-03-11", new HashSet<String>(Arrays.asList(new String[] //
                    { "yy-MM-dd", "dd-MM-yy" })));
            put("22/03/11", new HashSet<String>(Arrays.asList(new String[] //
                    { "yy/MM/dd", "d/MM/yy", "dd/MM/yy" })));
            put("22-3-11", new HashSet<String>(Arrays.asList(new String[] //
                    { "d-M-yy", "yy-M-d" })));
            put("11 mars 2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "d MMMM yyyy", "d MMM yyyy", "dd MMMM yyyy" })));
            put("11.03.2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "d.MM.yyyy", "dd.MM.yyyy" })));
            put("11-Mar-2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd-MMM-yyyy", "d-MMM-yyyy" })));
            put("11-mar-2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd-MMM-yyyy", "d-MMM-yyyy" })));
            put("11-Mar-2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd-MMM-yyyy", "d-MMM-yyyy" })));
            put("11 mars 2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "d MMMM yyyy", "d MMM yyyy", "dd MMMM yyyy" })));
            put("11 March 2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "d MMMM yyyy", "dd MMMM yyyy" })));
            put("11 marzo 2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "d MMMM yyyy", "dd MMMM yyyy" })));
            put("11 mars 2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "d MMMM yyyy", "d MMM yyyy", "dd MMMM yyyy" })));
            put("11/03/22 05:06", new HashSet<String>(Arrays.asList(new String[] //
                    { "yy/MM/dd HH:mm", "dd/MM/yy HH:mm" })));
            put("22-03-11 05:06", new HashSet<String>(Arrays.asList(new String[] //
                    { "yy-MM-dd HH:mm", "dd-MM-yy HH:mm" })));
            put("22/03/11 5:06", new HashSet<String>(Arrays.asList(new String[] //
                    { "d/MM/yy H:mm", "yy/MM/dd H:mm" })));
            put("11 mars 2222 05:06:07 CET", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd MMMM yyyy HH:mm:ss z", "d MMMM yyyy HH:mm:ss z" })));
            put("11 March 2222 05:06:07 CET", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd MMMM yyyy HH:mm:ss z", "d MMMM yyyy HH:mm:ss z" })));
            put("11 mars 2222 05:06:07 CET", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd MMMM yyyy HH:mm:ss z", "d MMMM yyyy HH:mm:ss z" })));
            put("11/03/2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "MM/dd/yyyy", "dd/MM/yyyy" })));
            put("03/11/2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "MM/dd/yyyy", "dd/MM/yyyy" })));
            put("3/11/2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "d/M/yyyy", "M/d/yyyy" })));
            put("11.3.22", new HashSet<String>(Arrays.asList(new String[] //
                    { "d.M.yy", "yy.M.d" })));
            put("11-03-22", new HashSet<String>(Arrays.asList(new String[] //
                    { "yy-MM-dd", "dd-MM-yy" })));
            put("11/3/2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "d/M/yyyy", "M/d/yyyy" })));
            put("11/03/22", new HashSet<String>(Arrays.asList(new String[] //
                    { "yy/MM/dd", "d/MM/yy", "dd/MM/yy" })));
            put("11.03.22", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd.MM.yy", "d.MM.yy" })));
            put("22.3.11", new HashSet<String>(Arrays.asList(new String[] //
                    { "d.M.yy", "yy.M.d", "yy.d.M" })));
            put("22.11.3", new HashSet<String>(Arrays.asList(new String[] //
                    { "yy.M.d", "yy.d.M" })));
            put("11-3-22", new HashSet<String>(Arrays.asList(new String[] //
                    { "d-M-yy", "yy-M-d" })));
            put("11.03.2222", new HashSet<String>(Arrays.asList(new String[] //
                    { "d.MM.yyyy", "dd.MM.yyyy" })));
            put("11-03-22 05:06", new HashSet<String>(Arrays.asList(new String[] //
                    { "yy-MM-dd HH:mm", "dd-MM-yy HH:mm" })));
            put("11/03/22 5:06", new HashSet<String>(Arrays.asList(new String[] //
                    { "d/MM/yy H:mm", "yy/MM/dd H:mm" })));
            put("11.03.22 5:06", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd.MM.yy H:mm", "d.MM.yy H:mm" })));
            put("22/03/11 05:06", new HashSet<String>(Arrays.asList(new String[] //
                    { "yy/MM/dd HH:mm", "dd/MM/yy HH:mm" })));
            put("11.03.22 5:06", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd.MM.yy H:mm", "d.MM.yy H:mm" })));
            put("11.03.2222 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd.MM.yyyy H:mm:ss", "d.MM.yyyy H:mm:ss" })));
            put("11.03.2222 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
                    { "dd.MM.yyyy H:mm:ss", "d.MM.yyyy H:mm:ss" })));

        }
    };

    @BeforeClass
    public static void loadTestData() throws IOException {

        InputStream dateInputStream = SystemDateTimePatternManager.class.getResourceAsStream("DateSampleTable.txt");
        DATE_SAMPLES = IOUtils.readLines(dateInputStream);
        InputStream timeInputStream = SystemDateTimePatternManager.class.getResourceAsStream("TimeSampleTable.txt");
        TIME_SAMPLES = IOUtils.readLines(timeInputStream);
    }

    @Test
    public void testDatesWithMultipleFormats() throws IOException {

        for (String sample : EXPECTED_FORMATS.keySet()) {
            Set<String> patternSet = SystemDateTimePatternManager.datePatternReplace(sample);
            assertEquals("Unexpected Format Set on sample <" + sample + ">", EXPECTED_FORMATS.get(sample), patternSet);
        }
    }

    @Test
    @Ignore
    public void prepareDatesWithMultipleFormats() throws IOException {
        Set<String> datesWithMultipleFormats = new HashSet<String>();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < DATE_SAMPLES.size(); i++) {
            String line = DATE_SAMPLES.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                Set<String> patternSet = SystemDateTimePatternManager.datePatternReplace(sample);

                if (patternSet.size() > 1) {
                    sb.append("put(\"").append(sample).append("\", new HashSet<String>(Arrays.asList(new String[] //\n\t{ ");
                    datesWithMultipleFormats.add(sample);
                    for (String p : patternSet) {
                        sb.append("\"").append(p).append("\",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(" })));\n");
                }
            }
        }
        System.out.println(sb);
    }

    @Test
    public void testAllSupportedDatesWithRegexes() throws IOException {

        for (int i = 1; i < DATE_SAMPLES.size(); i++) {
            String line = DATE_SAMPLES.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                // String expectedPattern = sampleLine[1];
                // String locale = sampleLine[2];
                // System.out.println(SystemDateTimePatternManager.isDate(sample) + "\t" + locale + "\t" + sample + "\t"
                // + expectedPattern);
                // System.out.println(SystemDateTimePatternManager.datePatternReplace(sample));
                assertTrue(SystemDateTimePatternManager.isDate(sample));
            }
        }
    }

    @Test
    public void testAllSupportedTimesWithRegexes() throws IOException {

        for (int i = 1; i < TIME_SAMPLES.size(); i++) {
            String line = TIME_SAMPLES.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                // String expectedPattern = sampleLine[1];
                // String locale = sampleLine[2];
                // System.out.println(SystemDateTimePatternManager.isTime(sample) + "\t" + locale + "\t" + sample + "\t"
                // + expectedPattern);
                assertTrue(SystemDateTimePatternManager.isTime(sample));
            }
        }
    }
}
