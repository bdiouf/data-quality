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
import org.talend.datascience.common.regex.ChainResponsibilityHandler;
import org.talend.datascience.common.regex.HandlerFactory;

/**
 * * Recognize East Asia characters given predefined list of characters and its pattern mappings.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class EastAsiaCharPatternAnalyzer extends PatternFrequencyAnalyzer {

    private static final long serialVersionUID = 3116215612379217599L;

    public static final int LEVEL = 4;

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    protected RecognitionResult recognize(String stringToRecognize) {
        RecognitionResult result = RecognitionResult.getEmptyResult();
        if (StringUtils.isEmpty(stringToRecognize)) {
            result.setResult(stringToRecognize, false);
            return result;
        }
        // since the current implementation of East Asia character replacement is using regex macher , there is no way
        // to get the "isComplete" status during the process. So here the status simply deemed as "not complete yet".
        boolean isComplete = false;
        ChainResponsibilityHandler createEastAsiaPatternHandler = HandlerFactory.createEastAsiaPatternHandler();
        result.setResult(createEastAsiaPatternHandler.handleRequest(stringToRecognize), isComplete);
        return result;
    }

    @Override
    protected String getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternString();
    }

}
