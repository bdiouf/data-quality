package org.talend.dataquality.statistics.frequency;

import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.statistics.frequency.impl.EFrequencyAlgorithm;

public class CMSFrequencyStatisticsTest {

    private DataFrequencyAnalyzer fta = new DataFrequencyAnalyzer();

    @Before
    public void setUp() throws Exception {
        fta.setAlgorithm(EFrequencyAlgorithm.COUNT_MIN_SKETCH);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyze() {
        String[] data = new String[] { "a", "b", "a", "b", "a", "c", "1", "2", "3" };
        fta.init();
        for (String col : data) {
            fta.analyze(col);
        }
        Assert.assertEquals(3, fta.getResult().get(0).getFrequency("a"), 0);
        Assert.assertEquals(2, fta.getResult().get(0).getFrequency("b"), 0);
        Assert.assertEquals(1, fta.getResult().get(0).getFrequency("1"), 0);
        Assert.assertEquals(1, fta.getResult().get(0).getFrequency("c"), 0);

        // Test larger data
        int seed = 7364181;
        Random r = new Random(seed);
        int numItems = 1000000;
        int[] xs = new int[numItems];
        int maxScale = 20;
        fta.init();
        for (int i = 0; i < numItems; i++) {
            int scale = r.nextInt(maxScale);
            xs[i] = r.nextInt(1 << scale);
            fta.analyze(xs[i] + "");
        }
        int[] actualFreq = new int[1 << maxScale];
        for (int x : xs) {
            actualFreq[x]++;
        }

        int numErrors = 0;
        double epsOfTotalCount = 0.0001;
        double confidence = 0.99;
        for (int i = 0; i < actualFreq.length; ++i) {
            double ratio = ((double) (fta.getResult().get(0).getFrequency(i+"") - actualFreq[i])) / numItems;
            if (ratio > epsOfTotalCount) {
                numErrors++;
            }
        }
        double pCorrect = 1.0 - ((double) numErrors) / actualFreq.length;
        Assert.assertTrue("Confidence not reached: required " + confidence + ", reached " + pCorrect, pCorrect > confidence);
    }

}
