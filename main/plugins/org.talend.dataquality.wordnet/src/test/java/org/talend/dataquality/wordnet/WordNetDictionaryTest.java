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

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class WordNetDictionaryTest {

    private static final Logger LOGGER = Logger.getLogger(WordNetDictionaryTest.class);

    private static WordNetDictionary wordnet;

    private static int countThread = 2;

    @BeforeClass
    public static void prepare() {
        try {
            wordnet = WordNetDictionary.getInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    public void testIsValidWord() {
        assertTrue(wordnet.isValidWord("talent"));
        assertTrue(wordnet.isValidWord("postal_code"));
        assertTrue(wordnet.isValidWord("Of"));

        assertFalse(wordnet.isValidWord("foobar"));
        assertFalse(wordnet.isValidWord("PostalCode"));

    }

    @Test
    public void testIsValidTerm() {

        assertTrue(wordnet.isValidTerm("talent"));
        assertTrue(wordnet.isValidTerm("PostalCode"));
        assertTrue(wordnet.isValidTerm("CodePostal"));
        assertTrue(wordnet.isValidTerm("Postal Code"));
        assertTrue(wordnet.isValidTerm("CareOfName"));

        assertFalse(wordnet.isValidTerm("foobar"));
        assertFalse(wordnet.isValidTerm("country_code"));
        assertFalse(wordnet.isValidTerm("Code_Postal"));
        assertFalse(wordnet.isValidTerm("CodePays"));
        assertFalse(wordnet.isValidTerm(null));
    }

    @AfterClass
    public static void close() {
        wordnet.close();
    }

    @Test
    public void testIsValidWord_multi_thread() throws InterruptedException {
        final AtomicBoolean atomBoolean = new AtomicBoolean(true);
        Runnable runable1 = new Runnable() {

            @Override
            public void run() {
                try {
                    boolean flag = wordnet.isValidWord("of");
                    assertTrue(flag);
                    System.out.println("of:" + flag);
                    flag = wordnet.isValidWord("apple");
                    System.out.println("apple:" + flag);
                    assertTrue(flag);

                    flag = wordnet.isValidWord("foobar");
                    System.out.println("foobar:" + flag);
                    assertFalse(flag);
                    flag = wordnet.isValidWord("PostalCode");
                    System.out.println("PostalCode:" + flag);
                    assertFalse(flag);
                } catch (Exception exc) {
                    atomBoolean.set(false);
                } catch (AssertionError error) {
                    atomBoolean.set(false);
                } finally {
                    countThread--;
                }

            }

        };

        Runnable runable2 = new Runnable() {

            @Override
            public void run() {
                try {
                    boolean flag = wordnet.isValidWord("talent");
                    assertTrue(flag);
                    System.out.println("talent:" + flag);
                    flag = wordnet.isValidWord("postal_code");
                    System.out.println("postal_code:" + flag);
                    assertTrue(flag);

                    flag = wordnet.isValidWord("talend");
                    System.out.println("talend:" + flag);
                    assertFalse(flag);
                    flag = wordnet.isValidWord("childe");
                    System.out.println("childe:" + flag);
                    assertFalse(flag);
                } catch (Exception exc) {
                    atomBoolean.set(false);
                } catch (AssertionError error) {
                    atomBoolean.set(false);
                } finally {
                    countThread--;
                }

            }

        };
        Thread thread1 = new Thread(runable1);
        Thread thread2 = new Thread(runable2);
        thread1.start();
        thread2.start();
        while (true) {
            if (countThread == 0) {
                assertTrue(atomBoolean.get());
                break;
            }
        }
        // Thread tc = Thread.currentThread();
        //
        // synchronized (tc) {
        //
        // while (thread1.isAlive() || thread1.isAlive()) {
        //
        // tc.wait(200);
        //
        // }
        //
        // tc.notify();
        // }
        // assertTrue(atomBoolean.get());
    }
}
