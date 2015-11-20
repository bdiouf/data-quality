package org.talend.dataquality.statistics.type;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.talend.datascience.common.inference.type.SystemDatetimePatternManager;

public class CustomizeDatetimePatternManagerTest {

    @Test
    public void testDatePattern() {
        // No pattern found from predefined list of patterns
        Assert.assertFalse(SystemDatetimePatternManager.isDate("6/18/09 21:30"));

        // Set a customized pattern
        Assert.assertTrue(CustomDatetimePatternManager.isDate("6/18/09 21:30", "M/d/yy H:m", Locale.US));
        Assert.assertEquals("M/d/yy H:m",
                CustomDatetimePatternManager.datePatternReplace("6/18/09 21:30", "M/d/yy H:m", Locale.US));

        // Add a bad pattern but valid
        Assert.assertFalse(CustomDatetimePatternManager.isDate("6/18/09 21:30", "m-d-y hh:MM", Locale.US));

        // Add an invalid pattern
        // Add a bad pattern but valid
        Assert.assertFalse(CustomDatetimePatternManager.isDate("6/18/09 21:30", "d/m/y**y hh:mm zzzzzzz", Locale.US));

    }
}
