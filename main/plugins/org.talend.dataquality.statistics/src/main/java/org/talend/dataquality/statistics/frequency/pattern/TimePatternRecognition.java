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

import org.talend.datascience.common.inference.type.DatetimePatternManager;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

/**
 * Recognize time types given the predefined time regex pattern.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class TimePatternRecognition extends PatternRecognition {

    private static final long serialVersionUID = -8037012388831672458L;

    public static final int LEVEL = 2;

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public RecognitionResult recognize(String stringToRecognize) {
        RecognitionResult result = RecognitionResult.getEmptyResult();
        if (TypeInferenceUtils.isTime(stringToRecognize)) {
            result.setResult(DatetimePatternManager.getInstance().timePatternReplace(stringToRecognize), true);
        } else {
            result.setResult(stringToRecognize, false);
        }
        return result;
    }

}
