package org.talend.dataquality.statistics.frequency;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PatternFrequencyAnalyzerTest {

    PatternFrequencyAnalyzer patternFreqAnalyzer = null;

    @Before
    public void setUp() throws Exception {
        patternFreqAnalyzer = new PatternFrequencyAnalyzer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyze() {
        String[] data = new String[] { "John", "", "123Code", "111", "Zhao", "2015-08-20", "2012-02-12", "12/2/99", "Hois",
                "2001年" };
        for (String value : data) {
            patternFreqAnalyzer.analyze(value);
        }
        Map<String, Long> freqTable = patternFreqAnalyzer.getResult().get(0).getTopK(10);
        Iterator<Entry<String, Long>> entrySet = freqTable.entrySet().iterator();
        int idx = 0;
        boolean isAtLeastOneAsssert = false;
        while (entrySet.hasNext()) {
            Entry<String, Long> e = entrySet.next();
            if (idx == 0) {
                Assert.assertEquals("Aaaa", e.getKey());
                Assert.assertEquals(3, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            } else if (idx == 1) {
                Assert.assertEquals("yyyy-M-d", e.getKey());
                Assert.assertEquals(2, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            if (e.getKey().equals("999Aaaa")) {
                Assert.assertEquals(1, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            idx++;
        }
        Assert.assertTrue(isAtLeastOneAsssert);
    }

    @Test
    public void testAnalyzerTwoColumns() {

        String[][] data = new String[][] { { "John", "filx" }, { "", "a" }, { "123Code", "3649273" }, { "111", "100" },
                { "Zhao", "silL" }, { "2015-08-20", "2015-08-21" }, { "2012-02-12", "2022-9-12" }, { "12/2/99", "12/2/99" },
                { "Hois", "*^2lii" }, { "2001年", "4445-" } };
        for (String[] value : data) {
            patternFreqAnalyzer.analyze(value);
        }
        Map<String, Long> freqTable = patternFreqAnalyzer.getResult().get(0).getTopK(10);
        Map<String, Long> freqTable2 = patternFreqAnalyzer.getResult().get(1).getTopK(10);
        Iterator<Entry<String, Long>> entrySet = freqTable.entrySet().iterator();
        Iterator<Entry<String, Long>> entrySet2 = freqTable2.entrySet().iterator();
        int idx = 0;
        boolean isAtLeastOneAsssert = false;
        while (entrySet.hasNext()) {
            Entry<String, Long> e = entrySet.next();
            if (idx == 0) {
                Assert.assertEquals("Aaaa", e.getKey());
                Assert.assertEquals(3, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            } else if (idx == 1) {
                Assert.assertEquals("yyyy-M-d", e.getKey());
                Assert.assertEquals(2, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            if (e.getKey().equals("999Aaaa")) {
                Assert.assertEquals(1, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            idx++;
        }
        Assert.assertTrue(isAtLeastOneAsssert);
        
        isAtLeastOneAsssert=false;        
        while (entrySet2.hasNext()) {
            Entry<String, Long> e = entrySet2.next();
            if (idx == 0) {
                Assert.assertEquals("yyyy-M-d", e.getKey());
                Assert.assertEquals(2, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            } 
            if (e.getKey().equals("9999999")) {
                Assert.assertEquals(1, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            if (e.getKey().equals("a")) {
                Assert.assertEquals(1, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            if (e.getKey().equals("d/M/yy")) {
                Assert.assertEquals(1, e.getValue(), 0);
                isAtLeastOneAsssert = true;
            }
            idx++;
        }
        Assert.assertTrue(isAtLeastOneAsssert);

    }
}
