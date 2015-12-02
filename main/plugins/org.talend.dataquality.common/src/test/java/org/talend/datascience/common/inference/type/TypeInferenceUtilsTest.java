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
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class TypeInferenceUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeInferenceUtilsTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIsBoolean() throws Exception {
        List<String> values = loadData("org/talend/datascience/common/inference/testBoolean.csv");
        int countOfBooleans = 0;
        String timeStart = getCurrentTimeStamp();
        LOGGER.debug("Detect boolean start at: " + timeStart);
        // Assert total count.
        Assert.assertEquals(10000, values.size());
        for (String value : values) {
            if (TypeInferenceUtils.isBoolean(value)) {
                countOfBooleans++;
            }
        }
        String timeEnd = getCurrentTimeStamp();
        LOGGER.debug("Detect boolean end at: " + timeEnd);
        // Assert count of matches.
        Assert.assertEquals(2000, countOfBooleans);
        double difference = getTimeDifference(timeStart, timeEnd);

        LOGGER.debug("Detect boolean time diff: " + difference + " s.");
        // Assert.assertTrue(difference < 0.005); //This assert depends on machine performance , so won't assert it.
    }

    @Test
    public void testIsEmpty() throws Exception {
        List<String> values = loadData("org/talend/datascience/common/inference/testString.csv");
        int countOfEmpties = 0;
        String timeStart = getCurrentTimeStamp();
        LOGGER.debug("Detect empty start at: " + timeStart);
        // Assert total count.
        Assert.assertEquals(10000, values.size());
        for (String value : values) {
            if (TypeInferenceUtils.isEmpty(value)) {
                countOfEmpties++;
            }
        }
        String timeEnd = getCurrentTimeStamp();
        LOGGER.debug("Detect empty end at: " + timeEnd);
        // Assert count of matches.
        Assert.assertEquals(2000, countOfEmpties);
        double difference = getTimeDifference(timeStart, timeEnd);

        LOGGER.debug("Detect empty time diff: " + difference + " s.");
        assertTrue(difference < 0.006);
    }

    @Test
    public void testIsInteger() throws Exception {
        List<String> values = loadData("org/talend/datascience/common/inference/testInteger.csv");
        int countOfIntegers = 0;
        String timeStart = getCurrentTimeStamp();
        LOGGER.debug("Detect integer start at: " + timeStart);
        // Assert total count.
        Assert.assertEquals(10000, values.size());
        for (String value : values) {
            if (TypeInferenceUtils.isInteger(value)) {
                countOfIntegers++;
            }
        }
        String timeEnd = getCurrentTimeStamp();
        LOGGER.debug("Detect integer end at: " + timeEnd);
        // Assert count of matches.
        Assert.assertEquals(3000, countOfIntegers);
        // Assert time span.
        double difference = getTimeDifference(timeStart, timeEnd);

        LOGGER.debug("Detect integer time diff: " + difference + " s.");
        assertTrue(difference < 0.08);
    }

    @Test
    public void testIsDouble() throws Exception {
        String[] validEnDoubleValues = { "0.8", "1.2", "100", "100.0", "-2.0", "1.0e-04", "1.0e+4", "1E-4" };
        String[] validFrDoubleValues = { "0,9", "1,0e-4" };
        String[] invalidDoubleValues = { "NaN", "3.4d", "123L", "123l", " 0.8", "0.8 ", "0. 8", "1.0 e-4", "1. 0e-4", "1.0e -4" };

        int valideCount = 0;
        for (String value : (String[]) ArrayUtils.addAll(validEnDoubleValues, validFrDoubleValues)) {
            if (TypeInferenceUtils.isDouble(value))
                valideCount++;
        }
        Assert.assertEquals(valideCount, validEnDoubleValues.length);

        int invalideCount = 0;
        for (String value : invalidDoubleValues) {
            if (!TypeInferenceUtils.isDouble(value))
                invalideCount++;
        }
        Assert.assertEquals(invalideCount, invalidDoubleValues.length);

        // TODO Currently, we support only English locale, but we may support other locale(eg. FR) later
        // count = 0;
        // for (String value : (String[]) ArrayUtils.addAll(enDoubleValues, frDoubleValues)) {
        // if (TypeInferenceUtils.isDouble(value))
        // count++;
        // }
        // Assert.assertEquals(count, frDoubleValues.length);
    }

    @Test
    public void testPerformanceIsDouble() throws Exception {
        // test the performance of TypeInferenceUtils.isDouble method
        List<String> values = loadData("org/talend/datascience/common/inference/testDouble.csv");
        int countOfDoubles = 0;
        String timeStart = getCurrentTimeStamp();
        LOGGER.debug("Detect double start at: " + timeStart);
        // Assert total count.
        Assert.assertEquals(10000, values.size());
        for (String value : values) {
            if (TypeInferenceUtils.isDouble(value)) {
                countOfDoubles++;
            }
        }
        String timeEnd = getCurrentTimeStamp();
        LOGGER.debug("Detect double end at: " + timeEnd);
        // Assert count of matches.
        Assert.assertEquals(5000, countOfDoubles);
        // Assert time span.
        double difference = getTimeDifference(timeStart, timeEnd);

        LOGGER.debug("Detect double time diff: " + difference + " s.");
        assertTrue(difference < 0.09);

    }

    @Test
    public void testIsDate() throws Exception {
        List<String> values = loadData("org/talend/datascience/common/inference/testDate.csv");
        int countOfDates = 0;
        String timeStart = getCurrentTimeStamp();
        LOGGER.debug("Detect date start at: " + timeStart);
        // Assert total count.
        Assert.assertEquals(10000, values.size());
        for (String value : values) {
            if (TypeInferenceUtils.isDate(value)) {
                countOfDates++;
            }
        }
        String timeEnd = getCurrentTimeStamp();
        LOGGER.debug("Detect date end at: " + timeEnd);
        // Assert count of matches.
        Assert.assertEquals(4001, countOfDates);
        double difference = getTimeDifference(timeStart, timeEnd);

        LOGGER.debug("Detect date time diff: " + difference + " s.");
        assertTrue(difference < 0.43);
    }

    @Test
    public void testIsDateWithCustom() throws Exception {
        String date = "Feb.12.2014";
        assertFalse(TypeInferenceUtils.isDate(date));
        assertTrue(TypeInferenceUtils.isDate(date, Collections.singletonList("MMM.dd.yyyy")));
    }

    @Test
    public void testIsDateWithCustomAndLocale() throws Exception {
        String date = "févr..12.2014";
        assertFalse(TypeInferenceUtils.isDate(date));
        assertTrue(TypeInferenceUtils.isDate(date, Collections.singletonList("MMM.dd.yyyy"), Locale.FRANCE));
    }

    @Test
    public void testIsDateWithCustomAndWrongLocale() throws Exception {
        String date = "Feb.12.2014";
        assertFalse(TypeInferenceUtils.isDate(date));
        assertTrue(TypeInferenceUtils.isDate(date, Collections.singletonList("MMM.dd.yyyy"), Locale.FRANCE));
    }

    @Test
    public void testIsDateddMMMyyyy() {
        String dateStr = "15-Sep-2014";
        assertTrue(TypeInferenceUtils.isDate(dateStr));
    }

    private List<String> loadData(String path) throws IOException {
        List<String> values = IOUtils.readLines(this.getClass().getClassLoader().getResourceAsStream(path));
        return values;
    }

    public static double getTimeDifference(String timeStart, String timeEnd) {
        String startMin = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(timeStart, ":"), ":");
        String endMin = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(timeEnd, ":"), ":");

        String lEnd = StringUtils.substringAfterLast(timeEnd, ".");
        String lStart = StringUtils.substringAfterLast(timeStart, ".");
        double endTimeInSecond = Double
                .valueOf(StringUtils.substringAfterLast(StringUtils.substringBeforeLast(timeEnd, "."), ":") + "." + lEnd);
        double startTimeInSecond = Double.valueOf(StringUtils.substringAfterLast(StringUtils.substringBeforeLast(timeStart, "."),
                ":") + "." + lStart);
        double difference = 0;
        difference = endTimeInSecond - startTimeInSecond;
        return difference + (Integer.valueOf(endMin) - Integer.valueOf(startMin)) * 60;
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

}
