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

import org.talend.dataquality.statistics.type.CustomDatetimePatternManager;

/**
 * Recognize date types given the predefined date regex pattern.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class DateTimePatternFrequencyAnalyzer extends AbstractPatternFrequencyAnalyzer {

    private static final long serialVersionUID = -6360092927227678935L;

    private String customDateTimePattern = null; // TODO Replace by Set or list of custom patterns

    public void setCustomDateTimePattern(String pattern) {
        this.customDateTimePattern = pattern;
    }

    public String getCustomDateTimePattern() {
        return customDateTimePattern;
    }

    @Override
    protected RecognitionResult recognize(String stringToRecognize) {
        RecognitionResult result = new RecognitionResult();
        String datePatternAfterReplace = CustomDatetimePatternManager.replaceByDateTimePattern(stringToRecognize, customDateTimePattern);
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
