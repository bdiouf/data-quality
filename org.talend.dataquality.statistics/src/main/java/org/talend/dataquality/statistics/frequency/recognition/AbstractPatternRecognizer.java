package org.talend.dataquality.statistics.frequency.recognition;

import java.util.Set;

public abstract class AbstractPatternRecognizer {

    /**
     * Recognize the string pattern and the completeness status.
     * 
     * @param stringToRecognize the string whose pattern is to be recognized.
     * @return recognition result with completeness status.
     */
    public abstract RecognitionResult recognize(String stringToRecognize);

    protected abstract Set<String> getValuePattern(String originalValue);

}
