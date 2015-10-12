package org.talend.dataquality.statistics.numeric.histogram;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.datascience.common.inference.type.DataType;
import org.talend.datascience.common.inference.type.DataType.Type;

public class HistogramAnalyzerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private HistogramAnalyzer createAnalyzer(Type[] types, HistogramParameter histogramParameter) {
        return new HistogramAnalyzer(types, histogramParameter);
    }

    @Test
    public void testResizeWithInvalidValues() throws Exception {
        String[][] data = { { "aaaa" }, { "5" } };
        HistogramParameter histogramParameter = new HistogramParameter();
        histogramParameter.setDefaultParameters(0, 5, 1);
        HistogramAnalyzer analyzer = createAnalyzer(new DataType.Type[] { Type.DOUBLE }, histogramParameter);
        for (String[] d : data) {
            analyzer.analyze(d);
        }
        Map<Range, Long> histogram = analyzer.getResult().get(0).getHistogram();
        for (Entry<Range, Long> entry : histogram.entrySet()) {
            final Range range = entry.getKey();
            Assert.assertEquals(0, range.getLower(), 0.00);
            Assert.assertEquals(5, range.getUpper(), 0.00);
        }
    }

    @Test
    public void testAnalyzeStringArray() {
        String[] data = { "0", "2", "2.5", "4", "6", "7", "8", "9", "10" };
        HistogramParameter histogramParameter = new HistogramParameter();
        histogramParameter.setDefaultParameters(0, 10, 4);
        HistogramAnalyzer analyzer = createAnalyzer(new DataType.Type[] { Type.DOUBLE }, histogramParameter);
        for (String d : data) {
            analyzer.analyze(d);
        }

        Map<Range, Long> histogram = analyzer.getResult().get(0).getHistogram();

        Iterator<Entry<Range, Long>> entrySet = histogram.entrySet().iterator();
        int idx = 0;
        while (entrySet.hasNext()) {
            Entry<Range, Long> entry = entrySet.next();
            Range r = entry.getKey();
            if (idx == 0) {
                Assert.assertEquals(0, r.getLower(), 0.00);
                Assert.assertEquals(2.5, r.getUpper(), 0.00);
                Assert.assertEquals(2, entry.getValue(), 0);
            }
            if (idx == 1) {
                Assert.assertEquals(2.5, r.getLower(), 0.00);
                Assert.assertEquals(5, r.getUpper(), 0.00);
                Assert.assertEquals(2, entry.getValue(), 0);
            }
            if (idx == 2) {
                Assert.assertEquals(5, r.getLower(), 0.00);
                Assert.assertEquals(7.5, r.getUpper(), 0.00);
                Assert.assertEquals(2, entry.getValue(), 0);
            }
            if (idx == 3) {
                Assert.assertEquals(7.5, r.getLower(), 0.00);
                Assert.assertEquals(10, r.getUpper(), 0.00);
                Assert.assertEquals(3, entry.getValue(), 0);
            }
            idx++;
        }
    }

    @Test
    public void testAnalyzeExtended() {
        String[] data = { "1", "2", "3", "4", "5", "6", "7", "8", "9" ,"10"};
        HistogramParameter histogramParameter = new HistogramParameter();
        histogramParameter.setDefaultParameters(2, 8, 3);
        HistogramAnalyzer analyzer = createAnalyzer(new DataType.Type[] { Type.INTEGER }, histogramParameter);
        for (String d : data) {
            analyzer.analyze(d);
        }

        HistogramStatistics histogramStatistics = analyzer.getResult().get(0);
        Map<Range, Long> histogram = histogramStatistics.getHistogram();

        Iterator<Entry<Range, Long>> entrySet = histogram.entrySet().iterator();
        int idx = 0;
        while (entrySet.hasNext()) {
            Entry<Range, Long> entry = entrySet.next();
            Range r = entry.getKey();
            if (idx == 0) {
                System.out.println(r.getLower() + " to " + r.getUpper() + ", count:" + entry.getValue());
                Assert.assertEquals(2, r.getLower(), 0.00);
                Assert.assertEquals(4, r.getUpper(), 0.00);
                Assert.assertEquals(2, entry.getValue(), 0);
            }
            if (idx == 1) {
                System.out.println(r.getLower() + " to " + r.getUpper() + ", count:" + entry.getValue());
                Assert.assertEquals(4, r.getLower(), 0.00);
                Assert.assertEquals(6, r.getUpper(), 0.00);
                Assert.assertEquals(2, entry.getValue(), 0);
            }
            if (idx == 2) {
                System.out.println(r.getLower() + " to " + r.getUpper() + ", count:" + entry.getValue());
                Assert.assertEquals(6, r.getLower(), 0.00);
                Assert.assertEquals(8, r.getUpper(), 0.00);
                Assert.assertEquals(3, entry.getValue(), 0);
            }

            idx++;
        }
        //Assert the value out of range
        Assert.assertFalse(histogramStatistics.isComplete());
        Assert.assertEquals(1,histogramStatistics.getCountBelowMin(),0);
        Assert.assertEquals(2,histogramStatistics.getCountAboveMax(),0);
    }

    @Test
    public void testAnalyzeNegative() {
        String[] data = { "-2", "-4", "-6", "-7", "8", "9", "5", "1" };
        HistogramParameter histogramParameter = new HistogramParameter();
        histogramParameter.setDefaultParameters(-4, 8, 3);
        HistogramAnalyzer analyzer = createAnalyzer(new DataType.Type[] { Type.INTEGER }, histogramParameter);
        for (String d : data) {
            analyzer.analyze(d);
        }
        Map<Range, Long> histogram = analyzer.getResult().get(0).getHistogram();
        histogram.forEach(new BiConsumer<Range, Long>() {

            @Override
            public void accept(Range t, Long u) {
                System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                if (t.getLower() == -4.0) {
                    Assert.assertEquals(2, u, 0.0);
                }
                if (t.getLower() == 0.0) {
                    Assert.assertEquals(1, u, 0.0);
                }
                if (t.getLower() == 4.0) {
                    Assert.assertEquals(2, u, 0.0);
                }
            }

        });
    }

    @Test
    public void testAnalyzeFranction() {
        String[] data = { "-0.0001", "-0.00004", "-0.00006", "-0.00007", "8", "7", "9", "5", "1" };
        HistogramParameter histogramParameter = new HistogramParameter();
        histogramParameter.setDefaultParameters(-0.004, 9, 3);
        HistogramAnalyzer analyzer = createAnalyzer(new DataType.Type[] { Type.DOUBLE }, histogramParameter);
        for (String d : data) {
            analyzer.analyze(d);
        }
        Map<Range, Long> histogram = analyzer.getResult().get(0).getHistogram();
        histogram.forEach(new BiConsumer<Range, Long>() {

            @Override
            public void accept(Range t, Long u) {
                if (t.getLower() == -0.004) {
                    Assert.assertEquals(5, u, 0.0);
                    System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (Math.round(t.getLower() * 1000.0) / 1000.0 == 2.997) {
                    Assert.assertEquals(1, u, 0.0);
                    System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (Math.round(t.getLower() * 1000.0) / 1000.0 == 5.999) {
                    Assert.assertEquals(3, u, 0.0);
                    System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                }
            }

        });
    }

    @Test
    public void testHistogramWithColumnParameters() {
        String[][] data = new String[][] { { "1", "1", "one" }, { "2", "2", "2" }, { "3", "3", "3" }, { "4", "4", "4" },
                { "5", "5", "5" }, { "6", "6", "6" }, { "7", "7", "7" }, { "8", "8", "8" }, { "9", "9", "9" },
                { "10", "10", "10" } };
        HistogramParameter histogramParameter = new HistogramParameter();
        HistogramColumnParameter column1Param = new HistogramColumnParameter();
        column1Param.setParameters(2, 8, 3);
        histogramParameter.putColumnParameter(0, column1Param);
        HistogramColumnParameter column2Param = new HistogramColumnParameter();
        column2Param.setParameters(0, 9, 4);
        histogramParameter.putColumnParameter(1, column2Param);
        HistogramAnalyzer analyzer = createAnalyzer(new DataType.Type[] { Type.INTEGER, Type.INTEGER, Type.STRING },
                histogramParameter);
        for (String[] d : data) {
            analyzer.analyze(d);
        }
        Map<Range, Long> col1Histogram = analyzer.getResult().get(0).getHistogram();
        Map<Range, Long> col2Histogram = analyzer.getResult().get(1).getHistogram();
        col1Histogram.forEach(new BiConsumer<Range, Long>() {

            @Override
            public void accept(Range t, Long u) {
                if (t.getLower() == 2) {
                    Assert.assertEquals(2, u, 0.0);
                    Assert.assertEquals(4, t.getUpper(), 0.0);
                    System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (t.getLower() == 4) {
                    Assert.assertEquals(2, u, 0.0);
                    Assert.assertEquals(6, t.getUpper(), 0.0);
                    System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (t.getLower() == 6) {
                    Assert.assertEquals(3, u, 0.0);
                    Assert.assertEquals(8, t.getUpper(), 0.0);
                    System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
            }

        });
        col2Histogram.forEach(new BiConsumer<Range, Long>() {

            @Override
            public void accept(Range t, Long u) {
                if (t.getLower() == 0) {
                    Assert.assertEquals(2, u, 0.0);
                    Assert.assertEquals(2.25, t.getUpper(), 0.0);
                    System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (t.getLower() == 2.25) {
                    Assert.assertEquals(2, u, 0.0);
                    Assert.assertEquals(4.5, t.getUpper(), 0.0);
                    System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (t.getLower() == 4.5) {
                    Assert.assertEquals(2, u, 0.0);
                    Assert.assertEquals(6.75, t.getUpper(), 0.0);
                    System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
                if (t.getLower() == 6.75) {
                    Assert.assertEquals(3, u, 0.0);
                    Assert.assertEquals(9, t.getUpper(), 0.0);
                    System.out.println(t.getLower() + " to " + t.getUpper() + ", count:" + u);
                    return;
                }
            }

        });
    }
}
