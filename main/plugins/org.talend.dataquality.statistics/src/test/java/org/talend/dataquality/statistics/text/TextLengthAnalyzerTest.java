package org.talend.dataquality.statistics.text;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TextLengthAnalyzerTest {

    TextLengthAnalyzer analyzer = new TextLengthAnalyzer();

    @Before
    public void setUp() throws Exception {
        analyzer.init();
    }

    @After
    public void tearDown() throws Exception {
        analyzer.end();
    }

    @Test
    public void testAnalyze() {
        String[] data = new String[] { "Brayan", "Ava", " ", "" };
        for (String value : data) {
            analyzer.analyze(value);
        }
        TextLengthStatistics stats = analyzer.getResult().get(0);
        // Min
        Assert.assertEquals(0, stats.getMinTextLength(), 0);
        Assert.assertEquals(3, stats.getMinTextLengthIgnoreBlank(), 0);
        // Max
        Assert.assertEquals(6, stats.getMaxTextLength(), 0);
        Assert.assertEquals(6, stats.getMaxTextLengthIgnoreBlank(), 0);
        // Avg
        Assert.assertEquals(2.5, stats.getAvgTextLength(), 0);
        Assert.assertEquals(4.5, stats.getAvgTextLengthIgnoreBlank(), 0);

    }

    @Test
    public void testAnalyzeWithNullValue() {
        String[] data = new String[] { "          ", "Brayan",  "Ava", " ", null };
        for (String value : data) {
            analyzer.analyze(value);
        }
        TextLengthStatistics stats = analyzer.getResult().get(0);
        // Min
        Assert.assertEquals(1, stats.getMinTextLength(), 0);
        Assert.assertEquals(3, stats.getMinTextLengthIgnoreBlank(), 0);
        // Max
        Assert.assertEquals(10, stats.getMaxTextLength(), 0);
        Assert.assertEquals(6, stats.getMaxTextLengthIgnoreBlank(), 0);
        // Avg
        Assert.assertEquals(5, stats.getAvgTextLength(), 0);
        Assert.assertEquals(4.5, stats.getAvgTextLengthIgnoreBlank(), 0);

    }

    @Test
    public void testEmpties() {
        String[] data = new String[] { "  gmail.", "  " };
        for (String value : data) {
            analyzer.analyze(value);
        }
        TextLengthStatistics stats = analyzer.getResult().get(0);
        Assert.assertEquals(5, stats.getAvgTextLength(), 0);
        Assert.assertEquals(8, stats.getAvgTextLengthIgnoreBlank(), 0);
    }
}
