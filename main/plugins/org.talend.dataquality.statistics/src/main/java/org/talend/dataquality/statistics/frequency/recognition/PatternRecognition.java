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
package org.talend.dataquality.statistics.frequency.recognition;

import java.io.Serializable;

/**
 * Pattern recognition for string type. <br>
 * The pattern categories like date string, time string, Ascii and Asia character etc.
 * 
 * @since 1.3.0
 * @author mzhao
 *
 */
public abstract class PatternRecognition implements IRecogniation, Serializable, Comparable<PatternRecognition> {

    private static final long serialVersionUID = 5485881256810363647L;

    /**
     * The recognition level indicates the priorities of which recognition class should be applied in the first place
     * when several recognizer exist. The lower this value is, the highest priority it will be applied.
     * 
     * @return level of the recognition
     */
    public abstract int getLevel();

    @Override
    public int compareTo(PatternRecognition another) {
        return this.getLevel() - another.getLevel();
    }

    /**
     * Recognize the string and return the pattern of the string with a boolean indicating the pattern replacement is
     * complete if true ,false otherwise.
     * 
     * @param stringToRecognize the string to be replaced by its pattern string
     * @return the recognition result bean.
     */
    public abstract RecognitionResult recognize(String stringToRecognize);

}
