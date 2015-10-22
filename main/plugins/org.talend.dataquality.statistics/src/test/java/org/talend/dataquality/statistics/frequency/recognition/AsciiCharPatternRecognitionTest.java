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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AsciiCharPatternRecognitionTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRecognize() {
        // Assert empty
        AsciiCharPatternRecognition recognizer = new AsciiCharPatternRecognition();
        RecognitionResult result = recognizer.recognize("");
        Assert.assertFalse(result.isComplete());
        Assert.assertEquals("", result.getPatternString());

        // Assert blank and compare the result instance
        RecognitionResult result2 = recognizer.recognize(" ");
        Assert.assertFalse(result2.isComplete());
        Assert.assertEquals(" ", result2.getPatternString());
        Assert.assertTrue(result == result2);

        // Assert null
        RecognitionResult result3 = recognizer.recognize(null);
        Assert.assertFalse(result3.isComplete());
        Assert.assertNull(result3.getPatternString());
        Assert.assertTrue(result == result3);

        // Assert correctness of Ascii character replacement.
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞß0123456789"; //$NON-NLS-1$
        String replChars = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999"; //$NON-NLS-1$
        RecognitionResult result4 = recognizer.recognize(chars);
        Assert.assertTrue(result4.isComplete());
        Assert.assertEquals(replChars, result4.getPatternString());
        Assert.assertTrue(result == result4);

        // Assert incomplete when the chars including a none-ascii character "ィ".
        String charsWithNoneAscii = "abcィd"; //$NON-NLS-1$
        String replCharsWithNoneAscii = "aaaィa"; //$NON-NLS-1$
        RecognitionResult result5 = recognizer.recognize(charsWithNoneAscii);
        Assert.assertFalse(result5.isComplete());
        Assert.assertEquals(replCharsWithNoneAscii, result5.getPatternString());
        Assert.assertTrue(result == result5);

        // Assert incomplete when the chars including a none-ascii character "-".
        String charsWithDash = "abc-d"; //$NON-NLS-1$
        String replCharsWithDash = "aaa-a"; //$NON-NLS-1$
        RecognitionResult result6 = recognizer.recognize(charsWithDash);
        Assert.assertFalse(result6.isComplete());
        Assert.assertEquals(replCharsWithDash, result6.getPatternString());
        Assert.assertTrue(result == result6);

        // Assert more patterns
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("*-!", "*-!");
        str2Pattern.put("1-3", "9-9");
        str2Pattern.put("2001-9-10 - 2009-09-08", "9999-9-99 - 9999-99-98");
        str2Pattern.put("2001-20-8", "yyyy-d-M");
        testRecognition(str2Pattern);
    }

    private void testRecognition(Map<String,String> str2Pattern) {
        AsciiCharPatternRecognition recognizer = new AsciiCharPatternRecognition();
        Iterator<String> strIterator = str2Pattern.keySet().iterator();
        while (strIterator.hasNext()) {
            String str = strIterator.next();
            String pattern = recognizer.recognize(str).getPatternString();
            Assert.assertTrue(str2Pattern.get(str) == pattern);

        }

    }

}
