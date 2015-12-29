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
package org.talend.dataquality.wordnet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class WordNetDictionaryTest {

    private static final Logger LOGGER = Logger.getLogger(WordNetDictionaryTest.class);

    private static WordNetDictionary wordnet;

    @BeforeClass
    public static void prepare() {
        try {
            wordnet = WordNetDictionary.getInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    public void testContainsWord() {
        assertTrue(wordnet.containsWord("talent"));
        assertFalse(wordnet.containsWord("foobar"));
    }

    @AfterClass
    public static void close() {
        wordnet.close();
    }

}
