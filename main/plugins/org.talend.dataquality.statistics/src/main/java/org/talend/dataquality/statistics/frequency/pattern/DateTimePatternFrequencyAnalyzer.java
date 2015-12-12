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

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.statistics.datetime.CustomDateTimePatternManager;

/**
 * Recognize date types given the predefined date regex pattern.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class DateTimePatternFrequencyAnalyzer extends AbstractPatternFrequencyAnalyzer {

    private static final long serialVersionUID = -6360092927227678935L;

    private List<String> customDateTimePatterns = new ArrayList<>();

    public void addCustomDateTimePattern(String pattern) {
        this.customDateTimePatterns.add(pattern);
    }

    public void addCustomDateTimePatterns(List<String> patterns) {
        this.customDateTimePatterns.addAll(patterns);
    }

    public List<String> getCustomDateTimePattern() {
        return customDateTimePatterns;
    }

    @Override
    protected RecognitionResult recognize(String stringToRecognize) {
        RecognitionResult result = new RecognitionResult();
        String datePatternAfterReplace = CustomDateTimePatternManager.replaceByDateTimePattern(stringToRecognize,
                customDateTimePatterns);
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
