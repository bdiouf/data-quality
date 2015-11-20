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

import java.util.Locale;

import org.talend.dataquality.statistics.type.CustomDatetimePatternManager;
import org.talend.datascience.common.parameter.ParameterUtils;

/**
 * Recognize date types given the predefined date regex pattern.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class DatePatternAnalyzer extends PatternFrequencyAnalyzer {

    private static final long serialVersionUID = -6360092927227678935L;

    public static final int LEVEL = 1;

    private String customizedPattern = null;

    private Locale locale = Locale.getDefault();

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public void init() {
        customizedPattern = ParameterUtils.getCustomizedPattern(parameters);
        Locale newLocale = ParameterUtils.getLocale(parameters);
        if (newLocale != null) {
            locale = newLocale;
        }
        super.init();
    }

    @Override
    public RecognitionResult recognize(String stringToRecognize) {
        RecognitionResult result = RecognitionResult.getEmptyResult();
        String datePatternAfterReplace = CustomDatetimePatternManager.datePatternReplace(stringToRecognize, customizedPattern,
                locale);
        if (stringToRecognize.equals(datePatternAfterReplace)) {
            // Did not recognized.
            result.setResult(stringToRecognize, false);
        } else {
            result.setResult(datePatternAfterReplace, true);
        }
        return result;
    }

    @Override
    protected String getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternString();
    }
}
