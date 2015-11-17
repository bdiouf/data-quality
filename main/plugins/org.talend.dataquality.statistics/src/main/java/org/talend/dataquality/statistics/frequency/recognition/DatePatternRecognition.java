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

import org.talend.datascience.common.inference.type.DatetimePatternManager;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

/**
 * Recognize date types given the predefined date regex pattern.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class DatePatternRecognition extends PatternRecognition {

    private static final long serialVersionUID = -6360092927227678935L;

    public static final int LEVEL = 1;

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public RecognitionResult recognize(String stringToRecognize) {
        RecognitionResult result = RecognitionResult.getEmptyResult();
        if (TypeInferenceUtils.isDate(stringToRecognize)) {
            result.setResult(DatetimePatternManager.getInstance().datePatternReplace(stringToRecognize), true);
        }else{
            result.setResult(stringToRecognize, false);
        }
        return result;
    }
}
