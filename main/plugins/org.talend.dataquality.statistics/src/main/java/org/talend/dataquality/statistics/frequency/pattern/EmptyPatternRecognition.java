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
 * Empty recognizer handling null and "" values
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class EmptyPatternRecognition extends PatternRecognition {

    private static final long serialVersionUID = 1973291585278371232L;
    public static final int LEVEL = 0;
    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public RecognitionResult recognize(String stringToRecognize) {
        RecognitionResult result = RecognitionResult.getEmptyResult();
        if (StringUtils.isEmpty(stringToRecognize)) {
            result.setResult(stringToRecognize, true);
        } else {
            result.setResult(stringToRecognize, false);
        }
        return result;
    }

}
