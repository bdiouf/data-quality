package org.talend.dataquality.statistics.type;

import org.junit.Assert;
import org.junit.Test;
import org.talend.datascience.common.inference.type.SystemDatetimePatternManager;

public class CustomDatetimePatternManagerTest {

    @Test
    public void testDateMatchingCustomPattern() {
        // invalid with system time pattern
        Assert.assertFalse(SystemDatetimePatternManager.isDate("6/18/09 21:30"));

        // valid with custom pattern
        Assert.assertTrue(CustomDatetimePatternManager.isDate("6/18/09 21:30", "M/d/yy H:m"));
        Assert.assertEquals("M/d/yy H:m", CustomDatetimePatternManager.replaceByDateTimePattern("6/18/09 21:30", "M/d/yy H:m"));
    }

    @Test
    public void testValidDateNotMatchingCustomPattern() {
        Assert.assertTrue(CustomDatetimePatternManager.isDate("6/18/2009 21:30", "m-d-y hh:MM"));
        Assert.assertEquals("M/d/yyyy H:m", CustomDatetimePatternManager.replaceByDateTimePattern("6/18/2009 21:30", "m-d-y hh:MM"));
    }

    @Test
    public void testInvalidDateNotMatchingCustomPattern() {
        Assert.assertFalse(CustomDatetimePatternManager.isDate("6/18/09 21:30", "m-d-y hh:MM"));
        Assert.assertEquals("6/18/09 21:30", CustomDatetimePatternManager.replaceByDateTimePattern("6/18/09 21:30", "m-d-y hh:MM"));
    }

    @Test
    public void testValidDateWithInvalidPattern() {
        Assert.assertTrue(CustomDatetimePatternManager.isDate("6/18/2009 21:30", "d/m/y**y hh:mm zzzzzzz"));
        Assert.assertEquals("M/d/yyyy H:m",
                CustomDatetimePatternManager.replaceByDateTimePattern("6/18/2009 21:30", "d/m/y**y hh:mm zzzzzzz"));
    }

    @Test
    public void testTimeMatchingCustomPattern() {
        // invalid with system time pattern
        Assert.assertFalse(SystemDatetimePatternManager.isTime("21?30"));

        // valid with custom pattern
        Assert.assertTrue(CustomDatetimePatternManager.isTime("21?30", "H?m"));
        Assert.assertEquals("H?m", CustomDatetimePatternManager.replaceByDateTimePattern("21?30", "H?m"));
    }

    @Test
    public void testValidTimeNotMatchingCustomPattern() {
        Assert.assertTrue(CustomDatetimePatternManager.isTime("21:30", "H-m"));
        Assert.assertEquals("H:m", CustomDatetimePatternManager.replaceByDateTimePattern("21:30", "H-m"));
    }

    @Test
    public void testInvalidTimeNotMatchingCustomPattern() {
        Assert.assertFalse(CustomDatetimePatternManager.isTime("21?30", "H-m"));
        Assert.assertEquals("21?30", CustomDatetimePatternManager.replaceByDateTimePattern("21?30", "H-m"));
    }

    @Test
    public void testValidTimeWithInvalidPattern() {
        Assert.assertTrue(CustomDatetimePatternManager.isTime("21:30", "d/m/y**y hh:mm zzzzzzz"));
        Assert.assertEquals("H:m", CustomDatetimePatternManager.replaceByDateTimePattern("21:30", "d/m/y**y hh:mm zzzzzzz"));
    }
}
