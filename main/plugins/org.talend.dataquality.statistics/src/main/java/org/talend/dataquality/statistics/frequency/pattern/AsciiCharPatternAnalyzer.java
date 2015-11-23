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

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * * Recognize ascii characters given predefined list of Ascii characters and its pattern mappings.
 * 
 * @since 1.3.0
 * @author mzhao
 */
public class AsciiCharPatternAnalyzer extends PatternFrequencyAnalyzer {

    private static final long serialVersionUID = -104288378010857759L;

    public static final int LEVEL = 3;


    public static final String CHARS_TO_REPLACE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞß0123456789";

    public static final String REPLACEMENT_CHARS = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999";

    private Pattern charsPattern = Pattern.compile("[a-z|A-Z|à-ÿ|À-ß]");
    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    protected RecognitionResult recognize(String stringToRecognize) {
        RecognitionResult result = new RecognitionResult();
        if (StringUtils.isEmpty(stringToRecognize)) {
            result.setResult(stringToRecognize, false);
            return result;
        }
        boolean isComplete = true;
        StringBuffer sb = new StringBuffer();
        int n = stringToRecognize.length();
        for (int i = 0; i < n; i++) {
            char c = stringToRecognize.charAt(i);
            int pos = CHARS_TO_REPLACE.indexOf(c);
            if (pos > -1) {
                sb.append(REPLACEMENT_CHARS.charAt(pos));
            } else {
                sb.append(c);
                isComplete = false;
            }
        }
        result.setResult(sb.toString(), isComplete);
        return result;
    }

    /**
     * Whether the patternString contains the predefined alpha character.
     * 
     * @param patternString
     * @return
     */
    public boolean containsAlphabetic(String patternString) {
        return charsPattern.matcher(patternString).find();
    }

    @Override
    public String getValuePattern(String originalValue) {
        RecognitionResult result = recognize(originalValue);
        return result.getPatternString();
    }

}
