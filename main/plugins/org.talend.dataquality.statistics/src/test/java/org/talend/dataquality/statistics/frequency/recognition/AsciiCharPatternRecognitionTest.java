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

    }
}
