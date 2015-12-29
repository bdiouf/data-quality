package org.talend.dataquality.statistics.frequency.impl;

import java.util.Map;

/**
 * Abstract frequency evaluator.
 * 
 * @author mzhao
 *
 */
public abstract class AbstractFrequencyEvaluator {

    /**
     * Add the value to frequency evaluator.
     * 
     * @param value value used to compute frequencies.
     */
    public abstract void add(String value);

    /**
     * Get top k frequency table given evaluator's implementation.
     * <br>
     * Note that {@link CMSFrequencyEvaluator} has no frequency table and will return a empty map.
     * @param topk
     * @return the top k frequency table.
     */
    public abstract Map<String, Long> getTopK(int topk);

    /**
     * Set the parameters of the evaluator. And initialize the required calculator elements given the new parameters.
     * 
     * @param params
     * @throws IllegalArgumentException thrown when parameters are illegal set.
     */
    public abstract void setParameters(Map<String, String> params) throws IllegalArgumentException;

    /**
     * Get frequencies of given item.
     * <br>
     * Note that it's not available for {@link SSFrequencyEvaluator} .
     * @param item
     * @return frequencies.
     */
    public abstract long getFrequency(String item);
}
