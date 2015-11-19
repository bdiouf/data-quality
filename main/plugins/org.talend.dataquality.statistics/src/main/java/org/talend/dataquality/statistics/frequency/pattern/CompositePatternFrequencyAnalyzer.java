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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.talend.dataquality.statistics.frequency.PatternFrequencyAnalyzer;

/**
 * Compute the pattern frequency tables.<br>
 * This class is a composite analyzer that it will automatically attribute a character to the correct pattern group.
 * 
 * @since 1.3.3
 * @author mzhao
 *
 */
public class CompositePatternFrequencyAnalyzer extends PatternFrequencyAnalyzer {

    private static final long serialVersionUID = -4658709249927616622L;

    private Set<PatternFrequencyAnalyzer> patternRecognitions = new TreeSet<PatternFrequencyAnalyzer>();

    public CompositePatternFrequencyAnalyzer() {
        // Initialize the built-in string pattern recognitions.
        // Date
        patternRecognitions.add(new EmptyPatternAnalyzer());
        patternRecognitions.add(new DatePatternAnalyzer());
        patternRecognitions.add(new TimePatternAnalyzer());
        patternRecognitions.add(new AsciiCharPatternAnalyzer());

    }

    /**
     * Inject the recognizer of types below:<br>
     * <ul>
     * <li>{@link EmptyPatternAnalyzer}</>
     * <li>{@link DatePatternAnalyzer}</>
     * <li>{@link TimePatternAnalyzer}</>
     * <li>{@link AsciiCharPatternAnalyzer}</>
     * <li>{@link EastAsiaCharPatternAnalyzer}</>
     * </ul>
     * 
     * @param recognizerToInject the recognition to be registered.
     */
    public void injectRecognizer(PatternFrequencyAnalyzer recognizerToInject) {
        if (recognizerToInject == null) {
            throw new RuntimeException("null recognition is not allowed");
        }
        // No need to inject if already existed.
        Iterator<PatternFrequencyAnalyzer> recIterator = patternRecognitions.iterator();
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
    public void removeRecognizer(PatternFrequencyAnalyzer recognizerToRemove) {
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
        Iterator<PatternFrequencyAnalyzer> recIterator = patternRecognitions.iterator();
        PatternFrequencyAnalyzer toRemove = null;
        while (recIterator.hasNext()) {
            PatternFrequencyAnalyzer next = recIterator.next();
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

    /**
     * Recognize the string and return the pattern of the string with a boolean indicating the pattern replacement is
     * complete if true ,false otherwise.
     * 
     * @param originalValue the string to be replaced by its pattern string
     * @return the recognition result bean.
     */
    @Override
    protected String getValuePattern(String originalValue) {
        Iterator<PatternFrequencyAnalyzer> recognizerIterator = patternRecognitions.iterator();
        String patternValue = originalValue;
        while (recognizerIterator.hasNext()) {
            PatternFrequencyAnalyzer next = recognizerIterator.next();
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
    public void setParameters(Map<String, String> parameters) {
        Iterator<PatternFrequencyAnalyzer> recIterator = patternRecognitions.iterator();
        while (recIterator.hasNext()) {
            recIterator.next().setParameters(parameters);
        }
    }

    @Override
    public int getLevel() {
        return -1;
    }

    @Override
    public RecognitionResult recognize(String stringToRecognize) {
        return null; // Nothing to do for composite pattern.
    }

}
