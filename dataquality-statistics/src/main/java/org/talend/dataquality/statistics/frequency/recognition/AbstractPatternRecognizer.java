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
