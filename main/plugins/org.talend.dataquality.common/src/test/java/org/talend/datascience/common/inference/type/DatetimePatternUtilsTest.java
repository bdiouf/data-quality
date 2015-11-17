package org.talend.datascience.common.inference.type;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatetimePatternUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDatePattern() {
        DatetimePatternManager patternUtil = DatetimePatternManager.getInstance();
        // No pattern found from predefined list of patterns
        Assert.assertFalse(patternUtil.isDate("6/18/09 21:30"));
        // No pattern found from predefined list of patterns
        Assert.assertEquals("6/18/09 21:30", patternUtil.datePatternReplace("6/18/09 21:30"));
        Assert.assertFalse(patternUtil.isDatePattern("M/d/yy H:m"));

        // Set a customized pattern
        patternUtil.addCustomizedDatePattern("M/d/yy H:m");
        Assert.assertTrue(patternUtil.isDate("6/18/09 21:30"));
        Assert.assertEquals("M/d/yy H:m", patternUtil.datePatternReplace("2/3/09 21:30"));
        Assert.assertFalse(patternUtil.isDate("6/18/09 21:30:00"));
        Assert.assertTrue(patternUtil.isDatePattern("M/d/yy H:m"));
    }

}
