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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataquality.statistics.frequency.impl.CMSFrequencyEvaluator;
import org.talend.dataquality.statistics.frequency.impl.EFrequencyAlgorithm;
import org.talend.dataquality.statistics.frequency.impl.NaiveFrequencyEvaluator;
import org.talend.dataquality.statistics.frequency.impl.SSFrequencyEvaluator;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;

/**
 * Frequency analyzer which delegate the computation to {@link NaiveFrequencyEvaluator} , {@link SSFrequencyEvaluator}
 * and {@link CMSFrequencyEvaluator} by specify the algorithm of {@link EFrequencyAlgorithm#NAIVE} ,
 * {@link EFrequencyAlgorithm#SPACE_SAVER} and {@link EFrequencyAlgorithm#COUNT_MIN_SKETCH}
 * 
 * @author mzhao
 *
 */
public abstract class FrequencyAnalyzer<T extends FrequencyStatistics> implements Analyzer<T> {

    private static final long serialVersionUID = 5073865267265592024L;

    protected ResizableList<T> freqTableStatistics = null;

    protected EFrequencyAlgorithm algorithm = EFrequencyAlgorithm.NAIVE;

    protected Map<String, String> parameters = new HashMap<>();

    /**
     * Set the algorithm used to compute the frequency table.
     * 
     * @param algorithm
     */
    public void setAlgorithm(EFrequencyAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Set parameters of the frequency analyzer. Parameters set as:<br>
     * {@link CMSFrequencyEvaluator#EPS }<br>
     * {@link CMSFrequencyEvaluator#SEED}<br>
     * {@link CMSFrequencyEvaluator#CONFIDENCE}<br>
     * {@link SSFrequencyEvaluator#CAPACITY}
     * <P>
     * these parameters are not mandatory since there are default values.
     * 
     * @param parameters parameters to be set when using algorithm {@link EFrequencyAlgorithm#SPACE_SAVER} and
     * {@link EFrequencyAlgorithm#COUNT_MIN_SKETCH}
     */
    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get value pattern which used to computed the frequencies.
     * 
     * @param originalValue the original value
     * @return value pattern used to compute the frequencies.
     */
    protected abstract String getValuePattern(String originalValue);

    protected abstract void initFreqTableList(int size);

    @Override
    public void init() {
        if (freqTableStatistics != null) {
            freqTableStatistics.clear();
        }
    }

    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        if (freqTableStatistics == null || freqTableStatistics.size() == 0) {
            initFreqTableList(record.length);
        }
        for (int i = 0; i < record.length; i++) {
            FrequencyStatistics freqStas = freqTableStatistics.get(i);
            freqStas.add(getValuePattern(record[i]));
        }
        return true;
    }

    @Override
    public void end() {
    }

    @Override
    public Analyzer<T> merge(Analyzer<T> another) {
        throw new NotImplementedException();
    }

    @Override
    public List<T> getResult() {
        return freqTableStatistics;
    }

    @Override
    public void close() throws Exception {

    }
}