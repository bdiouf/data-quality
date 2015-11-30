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

import org.apache.commons.lang.NotImplementedException;

/**
 * Compute the pattern frequency tables.<br>
 * This class is a composite analyzer that it will automatically attribute a character to the correct pattern group.
 * 
 * @since 1.3.3
 * @author mzhao
 *
 */
public class CompositePatternFrequencyAnalyzer extends AbstractPatternFrequencyAnalyzer {

    private static final long serialVersionUID = -4658709249927616622L;

    private List<AbstractPatternFrequencyAnalyzer> patternFreqAnalyzers = new ArrayList<AbstractPatternFrequencyAnalyzer>();

    public CompositePatternFrequencyAnalyzer() {
        // Initialize the built-in string pattern recognitions.
        // Date
        patternFreqAnalyzers.add(new EmptyPatternFrequencyAnalyzer());
        patternFreqAnalyzers.add(new DateTimePatternFrequencyAnalyzer());
        patternFreqAnalyzers.add(new LatinExtendedCharPatternFrequencyAnalyzer());

    }

    public CompositePatternFrequencyAnalyzer(List<AbstractPatternFrequencyAnalyzer> analyzerList) {
        patternFreqAnalyzers.addAll(analyzerList);
    }

    @Override
    public void init() {
        for (AbstractPatternFrequencyAnalyzer analyzer : patternFreqAnalyzers) {
            analyzer.init();
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
        String patternValue = originalValue;
        for (AbstractPatternFrequencyAnalyzer analyzer : patternFreqAnalyzers) {
            RecognitionResult result = analyzer.recognize(patternValue);
            if (result.isComplete()) {
                return result.getPatternString();
            } else {
                // Go to next recognizer
                patternValue = result.getPatternString();
            }
        }

        // value is not recognized completely.
        return patternValue;
    }

    @Override
    protected RecognitionResult recognize(String stringToRecognize) {
        throw new NotImplementedException();
    }

}
