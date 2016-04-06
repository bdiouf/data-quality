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
     * Get top k frequency table given evaluator's implementation. <br>
     * Note that {@link CMSFrequencyEvaluator} has no frequency table and will return a empty map.
     * 
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
     * Get frequencies of given item. <br>
     * Note that it's not available for {@link SSFrequencyEvaluator} .
     * 
     * @param item
     * @return frequencies.
     */
    public abstract long getFrequency(String item);
}
