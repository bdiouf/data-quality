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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.talend.dataquality.statistics.frequency.pattern.AsciiCharPatternRecognition;
import org.talend.dataquality.statistics.frequency.pattern.DatePatternRecognition;
import org.talend.dataquality.statistics.frequency.pattern.EastAsiaCharPatternRecognition;
import org.talend.dataquality.statistics.frequency.pattern.EmptyPatternRecognition;
import org.talend.dataquality.statistics.frequency.pattern.PatternRecognition;
import org.talend.dataquality.statistics.frequency.pattern.RecognitionResult;
import org.talend.dataquality.statistics.frequency.pattern.TimePatternRecognition;
import org.talend.datascience.common.inference.ResizableList;

/**
 * Compute the pattern frequency tables.<br>
 * This class is a composite analyzer that it will automatically attribute a character to the correct pattern group.
 * 
 * @author mzhao
 *
 */
public class PatternFrequencyAnalyzer extends FrequencyAnalyzer<PatternFrequencyStatistics> {

    private static final long serialVersionUID = -4658709249927616622L;

    private Set<PatternRecognition> patternRecognitions = new TreeSet<PatternRecognition>();

    public PatternFrequencyAnalyzer() {
        // Initialize the built-in string pattern recognitions.
        // Date
        patternRecognitions.add(new EmptyPatternRecognition());
        patternRecognitions.add(new DatePatternRecognition());
        patternRecognitions.add(new TimePatternRecognition());
        patternRecognitions.add(new AsciiCharPatternRecognition());

    }

    /**
     * Inject the recognizer of types below:<br>
     * <ul>
     * <li>{@link EmptyPatternRecognition}</>
     * <li>{@link DatePatternRecognition}</>
     * <li>{@link TimePatternRecognition}</>
     * <li>{@link AsciiCharPatternRecognition}</>
     * <li>{@link EastAsiaCharPatternRecognition}</>
     * </ul>
     * 
     * @param recognizerToInject the recognition to be registered.
     */
    public void injectRecognizer(PatternRecognition recognizerToInject) {
        if (recognizerToInject == null) {
            new RuntimeException("null recognition is not allowed");
        }
        // No need to inject if already existed.
        Iterator<PatternRecognition> recIterator = patternRecognitions.iterator();
        while (recIterator.hasNext()) {
            if (recIterator.next().getLevel() == recognizerToInject.getLevel()) {
                // Already exist.
                return;
            }
        }
        // Inject
        patternRecognitions.add(recognizerToInject);
    }

    /**
     * Remove the recognizer instance Note that nothing to do with this method if the recognizer to be removed does not
     * exist in the pool.
     * 
     * @param recognizerToRemove the recognizer instance added.
     */
    public void removeRecognizer(PatternRecognition recognizerToRemove) {
        if (recognizerToRemove == null) {
            new RuntimeException("null recognition is not allowed");
        }
        patternRecognitions.remove(recognizerToRemove);
    }

    /**
     * Remove the recognizer given its level. <br>
     * Note that nothing to do with this method if the recognizer to be removed does not exist in the pool.
     * 
     * @param level the recognizer's level
     */
    public void removeRecognizer(int level) {
        // No need to inject if already existed.
        Iterator<PatternRecognition> recIterator = patternRecognitions.iterator();
        PatternRecognition toRemove = null;
        while (recIterator.hasNext()) {
            PatternRecognition next = recIterator.next();
            if (next.getLevel() == level) {
                // Already exist.
                toRemove = next;
                break;
            }
        }
        if (toRemove != null) {
            patternRecognitions.remove(toRemove);
        }
    }

    @Override
    protected String getValuePattern(String originalValue) {
        Iterator<PatternRecognition> recognizerIterator = patternRecognitions.iterator();
        String patternValue = originalValue;
        while (recognizerIterator.hasNext()) {
            PatternRecognition next = recognizerIterator.next();
            RecognitionResult result = next.recognize(patternValue);
            if (result.isComplete()) {
                return result.getPatternString();
            } else {
                // Go to next recognizer.
                patternValue = result.getPatternString();
            }
        }

        // value is not recognized completely.
        return patternValue;
    }

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
