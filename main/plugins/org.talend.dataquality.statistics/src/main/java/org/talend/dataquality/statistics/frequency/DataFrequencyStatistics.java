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
package org.talend.dataquality.statistics.frequency;

import java.util.Map;

import org.talend.dataquality.statistics.frequency.impl.AbstractFrequencyEvaluator;
import org.talend.dataquality.statistics.frequency.impl.CMSFrequencyEvaluator;
import org.talend.dataquality.statistics.frequency.impl.EFrequencyAlgorithm;
import org.talend.dataquality.statistics.frequency.impl.NaiveFrequencyEvaluator;
import org.talend.dataquality.statistics.frequency.impl.SSFrequencyEvaluator;

/**
 * Frequency statistics bean which delegate the computation to evaluator.
 * 
 * @author mzhao
 *
 */
public class DataFrequencyStatistics extends FrequencyStatistics {

    private AbstractFrequencyEvaluator evaluator = new NaiveFrequencyEvaluator();

    /**
     * Set the algorithm used to compute the frequency table.
     * 
     * @param algorithm
     */
    @Override
    public void setAlgorithm(EFrequencyAlgorithm algorithm) {
        switch (algorithm) {
        case NAIVE:
            evaluator = new NaiveFrequencyEvaluator();
            break;
        case SPACE_SAVER:
            evaluator = new SSFrequencyEvaluator();
            break;
        case COUNT_MIN_SKETCH:
            evaluator = new CMSFrequencyEvaluator();
            break;
        }
    }

    /**
     * Get top k frequency table.
     * 
     * @param topk
     * @return
     */
    @Override
    public Map<String, Long> getTopK(int topk) {
        return evaluator.getTopK(topk);
    }

    /**
     * Get frequencies of given item
     * 
     * @param item
     * @return frequencies.
     */
    @Override
    public long getFrequency(String item) {
        return evaluator.getFrequency(item);
    }

    @Override
    public void setParameter(Map<String, String> params) {
        evaluator.setParameters(params);
    }

    @Override
    public void add(String value) {
        evaluator.add(value);
    }
}
