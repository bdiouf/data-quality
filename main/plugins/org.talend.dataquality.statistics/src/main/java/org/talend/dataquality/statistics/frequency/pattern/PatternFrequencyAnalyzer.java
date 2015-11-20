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
package org.talend.dataquality.statistics.frequency.pattern;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.statistics.frequency.FrequencyAnalyzer;
import org.talend.datascience.common.inference.ResizableList;

/**
 * Abstract class for pattern frequency analyzer.
 * 
 * @author mzhao
 *
 */
public abstract class PatternFrequencyAnalyzer extends FrequencyAnalyzer<PatternFrequencyStatistics> implements
        Comparable<PatternFrequencyAnalyzer> {

    private static final long serialVersionUID = -4658709249927616622L;

    /**
     * The recognition level indicates the priorities of which recognition class should be applied in the first place
     * when several recognizer exist. The lower this value is, the highest priority it will be applied.
     * 
     * @return level of the PatternAnalyzer
     */
    public abstract int getLevel();

    @Override
    public int compareTo(PatternFrequencyAnalyzer another) {
        return this.getLevel() - another.getLevel();
    }

    /**
     * Recognize the string pattern and the complete status.
     * 
     * @param stringToRecognize the string whose pattern is to be recognized.
     * @return recognition result with complete status.
     */
    protected abstract RecognitionResult recognize(String stringToRecognize);

    @Override
    protected abstract String getValuePattern(String originalValue);

    @Override
    protected void initFreqTableList(int size) {
        List<PatternFrequencyStatistics> freqTableList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PatternFrequencyStatistics freqTable = new PatternFrequencyStatistics();
            freqTable.setAlgorithm(algorithm);
            freqTable.setParameter(parameters);
            freqTableList.add(freqTable);
        }
        freqTableStatistics = new ResizableList<>(freqTableList);
    }

}
