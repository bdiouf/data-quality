package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class SampleTest {

    @Test
    public void testAllSupportedPatterns() throws IOException {
        InputStream stream = DateTimePatternManager.class.getResourceAsStream("DateSampleTable.txt");
        List<String> lines = IOUtils.readLines(stream);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                assertTrue(DateTimePatternManager.isDate(sample));
            }
        }
    }

    @Test
    public void testAllSupportedDatePatternsWithRegex() throws IOException {
        InputStream stream = DateTimePatternManager.class.getResourceAsStream("DateSampleTable.txt");
        List<String> lines = IOUtils.readLines(stream);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                // String expectedPattern = sampleLine[1];
                // String locale = sampleLine[2];
                // System.out.println(SystemDatetimePatternManager.isDate(sample) + "\t" + locale + "\t" + sample + "\t"
                // + expectedPattern);
                assertTrue(SystemDatetimePatternManager.isDate(sample));
            }
        }
    }

    @Test
    public void testAllSupportedTimePatternsWithRegex() throws IOException {
        InputStream stream = DateTimePatternManager.class.getResourceAsStream("TimeSampleTable.txt");
        List<String> lines = IOUtils.readLines(stream);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                // String expectedPattern = sampleLine[1];
                // String locale = sampleLine[2];
                // System.out.println(SystemDatetimePatternManager.isTime(sample) + "\t" + locale + "\t" + sample + "\t"
                // + expectedPattern);
                assertTrue(SystemDatetimePatternManager.isTime(sample));
            }
        }
    }
}
