package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.statistics.type.TypeInferenceUtilsTest;

public class PerformanceTest {

    private static final Logger LOGGER = Logger.getLogger(PerformanceTest.class);

    private static final String DATE_FILE_NAME = "testDate.csv";

    private static List<String> DATE_VALUES;

    @BeforeClass
    public static void loadDates() throws IOException {
        DATE_VALUES = IOUtils.readLines(TypeInferenceUtilsTest.class.getResourceAsStream(DATE_FILE_NAME));
    }

    @Test
    public void testIsDateUsingRegex() throws Exception {
        SystemDateTimePatternManager.isDate("12/02/99");// init DateTimeFormatters
        Date begin = new Date();
        LOGGER.debug("Detect date start at: " + begin);
        // Assert total count.
        Assert.assertEquals(10000, DATE_VALUES.size());
        for (String value : DATE_VALUES) {
            SystemDateTimePatternManager.isDate(value);
        }
        Date end = new Date();
        LOGGER.debug("Detect date end at: " + end);
        long difference = end.getTime() - begin.getTime();

        LOGGER.debug("Detect date time diff: " + difference + " ms.");
        // System.out.println("Total duration IsDate using regexes: " + difference);
        assertTrue(difference < 450);
    }

    @Test
    public void testGetPatternsUsingRegex() throws Exception {

        SystemDateTimePatternManager.isDate("12/02/99");// init DateTimeFormatters
        Date begin = new Date();
        LOGGER.debug("Detect date start at: " + begin);
        // Assert total count.
        for (String value : DATE_VALUES) {
            CustomDateTimePatternManager.replaceByDateTimePattern(value, Collections.emptyList());
        }
        Date end = new Date();
        LOGGER.debug("Detect date end at: " + end);
        long difference = end.getTime() - begin.getTime();

        LOGGER.debug("Detect date time diff: " + difference + " ms.");
        // System.out.println("Total duration GetPatterns with regexes: " + difference);
        assertTrue(difference < 900);
    }

    @Test
    public void testIsDateUsingRegexesOnAllSamples() throws IOException {

        InputStream stream = SystemDateTimePatternManager.class.getResourceAsStream("DateSampleTable.txt");
        List<String> lines = IOUtils.readLines(stream);

        SystemDateTimePatternManager.isDate("12/02/99");// init DateTimeFormatters
        Date begin = new Date();
        LOGGER.debug("Detect date start at: " + begin);

        long currentMilliseconds = begin.getTime();

        int count = 0;
        for (int i = 1; i < lines.size(); i++) {
            for (int n = 0; n < 333; n++) {
                String line = lines.get(i);
                if (!"".equals(line.trim())) {
                    String[] sampleLine = line.trim().split("\t");
                    String sample = sampleLine[0];
                    CustomDateTimePatternManager.isDate(sample, Collections.emptyList());
                    count++;
                    if (count % 5000 == 0) {
                        Date after = new Date();
                        long difference = after.getTime() - currentMilliseconds;
                        currentMilliseconds = after.getTime();
                        LOGGER.debug("Detect date end at: " + after);
                        System.out.println("count: " + count + " inteval: " + difference + "ms");
                    }
                }
            }
        }

        Date end = new Date();
        LOGGER.debug("Detect date end at: " + end);
        // Assert count of matches.
        long difference = end.getTime() - begin.getTime();

        LOGGER.debug("Detect date time diff: " + difference + " ms.");
        System.out.println("Total duration IsDates with regexes on " + count + " samples: " + difference + "ms");

        assertTrue(difference < 3000);
    }

    @Test
    public void testGetPatternsUsingRegexesOnAllSamples() throws IOException {

        InputStream stream = SystemDateTimePatternManager.class.getResourceAsStream("DateSampleTable.txt");
        List<String> lines = IOUtils.readLines(stream);

        SystemDateTimePatternManager.isDate("12/02/99");// init DateTimeFormatters
        Date begin = new Date();
        LOGGER.debug("Detect date start at: " + begin);

        long currentMilliseconds = begin.getTime();

        int count = 0;
        for (int i = 1; i < lines.size(); i++) {
            for (int n = 0; n < 333; n++) {
                String line = lines.get(i);
                if (!"".equals(line.trim())) {
                    String[] sampleLine = line.trim().split("\t");
                    String sample = sampleLine[0];
                    CustomDateTimePatternManager.replaceByDateTimePattern(sample, Collections.emptyList());
                    count++;
                    if (count % 5000 == 0) {
                        Date after = new Date();
                        long difference = after.getTime() - currentMilliseconds;
                        currentMilliseconds = after.getTime();
                        LOGGER.debug("Detect date end at: " + after);
                        System.out.println("count: " + count + " inteval: " + difference + "ms");
                    }
                }
            }
        }

        Date end = new Date();
        LOGGER.debug("Detect date end at: " + end);
        // Assert count of matches.
        long difference = end.getTime() - begin.getTime();

        LOGGER.debug("Detect date time diff: " + difference + " ms.");
        System.out.println("Total duration GetAllPatterns with regexes on " + count + " samples: " + difference + "ms");

        assertTrue(difference < 6000);
    }
}
