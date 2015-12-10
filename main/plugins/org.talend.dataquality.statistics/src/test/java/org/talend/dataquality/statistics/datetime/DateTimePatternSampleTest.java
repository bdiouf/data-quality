package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class DateTimePatternSampleTest {

    @Test
    public void testAllSupportedPatterns() throws IOException {
        InputStream stream = DateTimePatternManager.class.getResourceAsStream("DateTimeSampleTable.txt");
        List<String> lines = IOUtils.readLines(stream);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!"".equals(line.trim())) {
                String[] patternSample = line.trim().split("\t");
                String sample = patternSample[0];
                assertTrue(DateTimePatternManager.isDate(sample));
            }
        }
    }
}
