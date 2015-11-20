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

import org.apache.commons.lang.StringUtils;

/**
 * Recognition result bean. <b>Important</b> note that this result's instance is intended to be reused due to the memory
 * considerations when analyzing large dataset. So the you must not expect something like :<br>
 * List<RecognitionResult> results = recognize(List<String> stringToRecognize);<br>
 * But please use it to get the information of one result immediately per iteration of recognizer.
 * 
 * @author mzhao
 * @since 1.3.0
 */
class RecognitionResult {

    private String patternString;

    private boolean isComplete;

    private static RecognitionResult INSTANCE = null;

    protected static RecognitionResult getEmptyResult() {
        if (INSTANCE == null) {
            INSTANCE = new RecognitionResult();
        }
        INSTANCE.clear();
        return INSTANCE;
    }

    private void clear() {
        this.patternString = StringUtils.EMPTY;
        this.isComplete = false;
    }

    protected String getPatternString() {
        return patternString;
    }

    /**
     * Whether the given string is recognized completely after applying this level of recognition.
     * 
     * @return true if the recognition complete.
     */
    protected boolean isComplete() {
        return isComplete;
    }

    /**
     * Set the result with the replaced pattern string and the indicator of whether the pattern replacement is complete
     * or not.
     * 
     * @param patternString
     * @param isComplete
     */
    protected void setResult(String patternString, boolean isComplete) {
        this.patternString = patternString;
        this.isComplete = isComplete;
    }
}
