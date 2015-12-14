package org.talend.dataquality.statistics.frequency.recognition;

public abstract class AbstractPatternRecognizer {

    /**
     * Recognize the string pattern and the completeness status.
     * 
     * @param stringToRecognize the string whose pattern is to be recognized.
     * @return recognition result with completeness status.
     */
    public abstract RecognitionResult recognize(String stringToRecognize);

    protected abstract String getValuePattern(String originalValue);

}
