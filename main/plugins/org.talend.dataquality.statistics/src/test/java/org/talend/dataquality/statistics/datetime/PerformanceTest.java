package org.talend.dataquality.statistics.datetime;

import java.io.IOException;
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
    public void testIsDateLegacy() throws Exception {
        int countOfDates = 0;

        SystemDatetimePatternManager.isDate("12/02/99");// init DateTimeFormatters
        Date begin = new Date();
        LOGGER.debug("Detect date start at: " + begin);
        // Assert total count.
        Assert.assertEquals(10000, DATE_VALUES.size());
        for (String value : DATE_VALUES) {
            if (SystemDatetimePatternManager.isDate(value)) {
                countOfDates++;
            }
        }
        Date end = new Date();
        LOGGER.debug("Detect date end at: " + end);
        // Assert count of matches.
        Assert.assertEquals(5000, countOfDates);
        double difference = end.getTime() - begin.getTime();

        LOGGER.debug("Detect date time diff: " + difference + " ms.");
        System.out.println("Legacy System: " + difference);
        // assertTrue(difference < 0.43);
    }

    @Test
    public void testIsDateNew() throws Exception {
        int countOfDates = 0;

        DateTimePatternManager.isDate("12/02/99");// init DateTimeFormatters
        Date begin = new Date();
        LOGGER.debug("Detect date start at: " + begin);
        // Assert total count.
        Assert.assertEquals(10000, DATE_VALUES.size());
        for (String value : DATE_VALUES) {
            if (DateTimePatternManager.isDate(value)) {
                countOfDates++;
            }
        }
        Date end = new Date();
        LOGGER.debug("Detect date end at: " + end);
        // Assert count of matches.
        Assert.assertEquals(5000, countOfDates);
        double difference = end.getTime() - begin.getTime();

        LOGGER.debug("Detect date time diff: " + difference + " ms.");
        System.out.println("New System: " + difference);
        // assertTrue(difference < 0.43);
    }

}
