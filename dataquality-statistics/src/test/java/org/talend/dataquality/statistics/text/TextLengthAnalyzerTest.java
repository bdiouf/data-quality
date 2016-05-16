// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.statistics.text;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.common.inference.Analyzer;

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
        String[] data = new String[] { "          ", "Brayan", "Ava", " ", null };
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
    public void testMerge() {
        String[] data = new String[] { "          ", "Brayan", "Ava", " ", null };
        String[] data2 = new String[] { "          ", "Brayan", "Ava", " ", null };
        Analyzer<TextLengthStatistics> analyzer1 = new TextLengthAnalyzer();
        Runnable r1 = new Runnable() {

            @Override
            public void run() {
                analyzer1.init();
                for (String record : data) {
                    analyzer1.analyze(record);
                }
                analyzer1.end();
            };
        };
        try {
            analyzer1.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Analyzer<TextLengthStatistics> analyzer2 = new TextLengthAnalyzer();
        Runnable r2 = new Runnable() {

            @Override
            public void run() {
                analyzer2.init();
                for (String record : data2) {
                    analyzer2.analyze(record);
                }
                analyzer2.end();
            };
        };
        List<Thread> workers = new ArrayList<>();
        workers.add(new Thread(r1));
        workers.add(new Thread(r2));
        for (Thread worker : workers) {
            worker.start();
        }
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Analyzer<TextLengthStatistics> mergedAnalyzer = analyzer1.merge(analyzer2);
        TextLengthStatistics stats = mergedAnalyzer.getResult().get(0);
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
    public void testMoreAnalyzersMerge() {
        Analyzer<TextLengthStatistics> analyzer1 = new TextLengthAnalyzer();
        Analyzer<TextLengthStatistics> analyzer2 = new TextLengthAnalyzer();
        Analyzer<TextLengthStatistics> analyzer3 = new TextLengthAnalyzer();

        // Data set 1 with length 6
        String[] data1 = new String[] { " ", "France", "Asia", "A long string", "", null };
        Runnable r1 = new Runnable() {

            @Override
            public void run() {
                analyzer1.init();
                for (String record : data1) {
                    analyzer1.analyze(record);
                }
                analyzer1.end();
            };
        };
        try {
            analyzer1.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        // Data set 2 with length 3
        String[] data2 = new String[] { "A", "AB", "ABC" };
        Runnable r2 = new Runnable() {

            @Override
            public void run() {
                analyzer2.init();
                for (String record : data2) {
                    analyzer2.analyze(record);
                }
                analyzer2.end();
            };
        };
        try {
            analyzer2.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        // Data set 3 with length 4
        String[] data3 = new String[] { "computer", "machine", "PC", "laptop" };
        Runnable r3 = new Runnable() {

            @Override
            public void run() {
                analyzer3.init();
                for (String record : data3) {
                    analyzer3.analyze(record);
                }
                analyzer3.end();
            };
        };
        try {
            analyzer3.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        // Running the analyzers in parallel.

        List<Thread> workers = new ArrayList<>();
        workers.add(new Thread(r1));
        workers.add(new Thread(r2));
        workers.add(new Thread(r3));
        for (Thread worker : workers) {
            worker.start();
        }
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Merge the analyzer and assert the result.
        Analyzer<TextLengthStatistics> mergedAnalyzer = analyzer1.merge(analyzer2).merge(analyzer3);
        TextLengthStatistics stats = mergedAnalyzer.getResult().get(0);
        // Min
        Assert.assertEquals(0, stats.getMinTextLength(), 0);
        Assert.assertEquals(1, stats.getMinTextLengthIgnoreBlank(), 0);
        // Max
        Assert.assertEquals(13, stats.getMaxTextLength(), 0);
        Assert.assertEquals(13, stats.getMaxTextLengthIgnoreBlank(), 0);
        // Avg
        Assert.assertEquals(4.416667, stats.getAvgTextLength(), 0.00001);
        Assert.assertEquals(5.2, stats.getAvgTextLengthIgnoreBlank(), 0);
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
