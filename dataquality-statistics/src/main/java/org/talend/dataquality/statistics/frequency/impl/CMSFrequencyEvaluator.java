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
package org.talend.dataquality.statistics.frequency.impl;

import java.util.HashMap;
import java.util.Map;

import com.clearspring.analytics.stream.frequency.CountMinSketch;

/**
 * Count min sketch statistics bean.<br/>
 * 
 * Frequency analyzer implemented with count min sketch algorithm.<br>
 * Note that this mean does <b>NOT</b> provide frequency table map, but provide a way to get the frequency with
 * specificed key.<br/>
 * Use {@link SSFrequencyAnalyzer} instead to obtain a frequency table.
 * 
 * Note that this mean does <b>NOT</b> provide frequency table map, but provide a way to get the frequency with
 * specificed key.<br>
 * 
 * @author zhao
 *
 */
public class CMSFrequencyEvaluator extends AbstractFrequencyEvaluator {

    private CountMinSketch sketch = null;

    public CMSFrequencyEvaluator() {
        sketch = new CountMinSketch(0.0001, 0.99, 123456);
    }

    public void initCountMinSketch(double epsOfTotalCount, double confidence, int seed) {
        sketch = new CountMinSketch(epsOfTotalCount, confidence, seed);
    }

    public CountMinSketch getCountMinSketch() {
        return sketch;
    }

    /**
     * See more parameters explaination from See
     * http://www.espertech.com/esper/release-5.2.0/esper-reference/html/functionreference.html
     *
     * @param params
     */
    @Override
    public void setParameters(Map<String, String> params) throws IllegalArgumentException {
        double epsOfTotalCount = 0.0001;
        double confidence = 0.99;
        int seed = 123456;
        boolean isCreateNew = false;
        try {
            if (params.get(EPS) != null) {
                epsOfTotalCount = Double.valueOf(params.get(EPS));
                isCreateNew = true;
            }
            if (params.get(CONFIDENCE) != null) {
                confidence = Double.valueOf(params.get(CONFIDENCE));
                isCreateNew = true;
            }
            if (params.get(SEED) != null) {
                seed = Integer.valueOf(params.get(SEED));
                isCreateNew = true;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        if (isCreateNew) {
            sketch = new CountMinSketch(epsOfTotalCount, confidence, seed);
        }
    }

    @Override
    public void add(String value) {
        sketch.add(value, 1);
    }

    @Override
    public Map<String, Long> getTopK(int topk) {
        return new HashMap<String, Long>();
    }

    @Override
    public long getFrequency(String item) {
        return sketch.estimateCount(item);
    }

    /**
     * EPS Specifies the accuracy (number of values counted * accuracy >= number of errors) of type
     */
    public static final String EPS = "epsOfTotalCount";

    /**
     * CONFIDENCE Provides the certainty with which we reach the accuracy of type double. The default is 0.99.
     */
    public static final String CONFIDENCE = "confidence";

    /**
     * A seed value for computing hash codes of type integer. This default is 123456.
     */
    public static final String SEED = "seed";

}
