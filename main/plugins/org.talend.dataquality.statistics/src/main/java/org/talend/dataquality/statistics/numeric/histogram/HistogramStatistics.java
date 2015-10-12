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
package org.talend.dataquality.statistics.numeric.histogram;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Histogram statistics bean.
 * 
 * @author zhao
 *
 */
public class HistogramStatistics {

    private double min, max;

    private int numBins;

    private long countBelowMin, countAboveMax;

    private long[] result = new long[numBins];

    private double binSize = (max - min) / numBins;

    public void setParameters(double max, double min, int numBins) {
        this.max = max;
        this.min = min;
        this.numBins = numBins;
        binSize = (max - min) / numBins;
        result = new long[numBins];

    }

    public void add(double d) {
        double bin = ((d - min) / binSize);
        if (bin < 0) { /* this data is smaller than min */
            countBelowMin ++;
        } else if (bin > numBins) { /* this data point is bigger than max */
            countAboveMax ++;
        } else {
            if (bin == numBins) {
                result[(int) bin - 1] += 1; // Include count of the upper boundary.
            } else {
                result[(int) bin] += 1;
            }
        }
    }

    /**
     * Get histograms as a map
     * 
     * @return the histogram map where Key is the range and value is the freqency. <br>
     * Note that the returned ranges are in pattern of [Min,
     * Min+binSize),[Min+binSize,Min+binSize*2)...[Max-binSize,Max<b>]</b>
     */
    public Map<Range, Long> getHistogram() {
        Map<Range, Long> histogramMap = new LinkedHashMap<Range, Long>();
        double currentMin = min;
        for (int i = 0; i < numBins; i++) {
            double currentMax = currentMin + binSize;
            if ((i + 1) == numBins) {
                currentMax = max;
            }
            Range r = new Range(currentMin, currentMax);
            histogramMap.put(r, result[i]);
            currentMin = currentMin + binSize;
        }
        return histogramMap;
    }

    /**
     * @return returns when all values in the histogram (no value is outside the given range)
     */
    public boolean isComplete() {
        return countBelowMin == 0 && countAboveMax == 0;
    }

    
    public long getCountBelowMin() {
        return countBelowMin;
    }

    
    public long getCountAboveMax() {
        return countAboveMax;
    }
    

}
