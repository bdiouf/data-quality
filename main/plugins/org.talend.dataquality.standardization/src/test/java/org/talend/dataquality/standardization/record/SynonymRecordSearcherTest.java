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
package org.talend.dataquality.standardization.record;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;
import org.talend.dataquality.standardization.index.SynonymIndexBuilder;
import org.talend.dataquality.standardization.index.SynonymIndexBuilderTest;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;
import org.talend.dataquality.standardization.record.SynonymRecordSearcher.RecordResult;
import org.talend.dataquality.standardization.record.SynonymRecordSearcher.WordResult;

/**
 * Test class.
 */
public class SynonymRecordSearcherTest {

    private static final String[][][] WORDRESULTS = { //
    { { "11", "12", "13", "14", "15" }, { "21", "22", "23" }, { "31", "32", "33" }, { "41" } } // always at least one
                                                                                               // match
            , { { "11", "12" }, { "21", "22" }, { "31", "32" } } // several matches
            , { {}, { "21", "22" }, { "31", "32" } } // first search does not match anything
            , { { "11", "12" }, {}, { "31", "32" } } // second search does not match anything
            , { { "11", "12" }, { "21", "22" }, {} } // last search does not match anything
            , { { "11", "12" }, {}, {} } // 2 searches did not match
            , { {}, {}, {} } // nothing matched at all
            , { { "11", "11" }, { "21", "21" } } // matched are duplicate
    };

    @Test
    public void testRecordResultCompute() {
        int nbDup = 0;
        for (String[][] wrs : WORDRESULTS) {
            System.out.println();
            System.out.println(" ###    Testing #### ");

            nbDup += testRecordResultCompute(wrs);
        }
        Assert.assertEquals("last wordResult array should contain duplicates", 1, nbDup);
    }

    private int testRecordResultCompute(String[][] wordresults) {
        int nbDuplicateFound = 0;

        // prepare wordresults
        List<List<WordResult>> wordResults = new ArrayList<List<WordResult>>();
        for (String[] elts : wordresults) {
            List<WordResult> wrs = new ArrayList<WordResult>();
            for (String elt : elts) {
                WordResult wr = new WordResult();
                wr.input = "input " + elt;
                wr.word = "word " + elt;
                wr.score = Integer.valueOf(elt);
                wrs.add(wr);
            }
            wordResults.add(wrs);
        }

        // --- compute output results as a list
        RecordResult recRes = new RecordResult();
        recRes.record = initializeRecordToSearch(wordresults);
        recRes.wordResults.addAll(wordResults);

        List<OutputRecord> expectedOutputRows = null;
        expectedOutputRows = new ArrayList<OutputRecord>();
        SynonymRecordSearcher.RecordResult.computeOutputRows(wordresults.length, new ArrayList<WordResult>(), recRes.wordResults,
                expectedOutputRows);
        for (OutputRecord outputRecord : expectedOutputRows) {
            System.out.println(outputRecord);
        }

        // --- test that duplicates are removed when using a set instead of a list
        Set<OutputRecord> uniques = new HashSet<OutputRecord>();
        uniques.addAll(expectedOutputRows);
        Assert.assertTrue(uniques.size() <= expectedOutputRows.size());
        if (uniques.size() < expectedOutputRows.size()) {
            nbDuplicateFound++;
        }

        List<OutputRecord> outputRows = recRes.computeOutputRows();

        // --- check some assertions

        // verify number of results
        int expectedNbOutput = 1;
        for (String[] in : wordresults) {
            expectedNbOutput *= Math.max(in.length, 1);
        }

        Assert.assertEquals(expectedNbOutput, expectedOutputRows.size());
        Assert.assertTrue(expectedOutputRows.size() >= outputRows.size());

        for (OutputRecord outputRecord : outputRows) {
            boolean found = false;
            for (OutputRecord expectedRecord : expectedOutputRows) {
                if (expectedRecord.equals(outputRecord)) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue("Record not found: " + outputRecord, found);
        }
        return nbDuplicateFound;
    }

    /**
     * DOC scorreia Comment method "initializeRecordToSearch".
     * 
     * @param wordresults
     * @return
     */
    private String[] initializeRecordToSearch(String[][] wordresults) {
        Random rnd = new Random();
        String[] init = new String[wordresults.length];
        int i = 0;
        for (String[] wr : wordresults) {
            if (wr.length == 0) {
                init[i] = "AAA";
            } else {
                init[i] = wr[rnd.nextInt(wr.length)];
            }
            i++;
        }
        return init;
    }

    private void initIdx(String path) {
        SynonymIndexBuilder builder = new SynonymIndexBuilder();
        builder.deleteIndexFromFS(path);
        builder.initIndexInFS(path);
        for (String[] synonyms : SynonymIndexBuilderTest.synonyms) {
            try {
                builder.insertDocument(synonyms[0], synonyms[1]);
            } catch (IOException e) {
                e.printStackTrace();
                fail("should not get an exception here");
            }
        }
        builder.closeIndex();
    }

    public void testSearch(String[] record, int topDocLimit, int resultLimit) {
        SynonymRecordSearcher recSearcher = new SynonymRecordSearcher(record.length);
        for (int i = 0; i < record.length; i++) {
            initIdx("data/idx" + (i + 1));
            SynonymIndexSearcher searcher = new SynonymIndexSearcher("data/idx" + (i + 1));
            searcher.setTopDocLimit(topDocLimit);
            recSearcher.addSearcher(searcher, i);
        }

        try {
            TopDocs topDocs;
            int hits = 1;
            for (int i = 0; i < record.length; i++) {
                topDocs = recSearcher.getSearcher(i).searchDocumentBySynonym(record[i]);
                hits *= topDocs.totalHits;
            }

            List<OutputRecord> results = recSearcher.search(resultLimit, record);
            Assert.assertNotNull(results);
            Assert.assertFalse(results.isEmpty());
            for (OutputRecord outputRecord : results) {
                Assert.assertNotNull(outputRecord);
                String[] resultingRecord = outputRecord.getRecord();
                Assert.assertEquals(record.length, resultingRecord.length);
                System.out.println(StringUtils.join(resultingRecord, '|'));
                System.out.println("\t" + outputRecord.getScore());
            }
            Assert.assertEquals(Math.min(hits, resultLimit), results.size());

            for (int i = 0; i < record.length; i++) {
                recSearcher.getSearcher(i).close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("should not get an exception here");
        }
        System.out.println("");

    }

    @Test
    public void testSearch1() {
        String[] record = { "I.B.M." };
        testSearch(record, 5, 15);
    }

    @Test
    public void testSearch2() {
        String[] record = { "IBM", "ANpe" };
        testSearch(record, 5, 15);
    }

    @Test
    public void testSearch3() {
        String[] record = { "IBM", "ANPE", "International" };
        testSearch(record, 5, 15);
        testSearch(record, 5, 100);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.record.SynonymRecordSearcher#addSearcher(org.talend.dataquality.standardization.index.SynonymIndexSearcher, int)}
     * .
     */
    @Test
    public void testAddSearcher() {
        SynonymRecordSearcher recSearcher = new SynonymRecordSearcher(2);
        try {
            recSearcher.addSearcher(null, 0);
        } catch (Exception e) {
            Assert.assertNotNull("we should get an exception here", e);
        }
        try {
            recSearcher.addSearcher(new SynonymIndexSearcher(), 2);
            Assert.fail("Index should be out of bounds here: trying to set a searcher at position in an empty array");
        } catch (Exception e) {
            Assert.assertNotNull("we should get an exception here", e);
        } catch (java.lang.AssertionError e) {
            Assert.assertNotNull("we should get an assertion error here when -ea is added to the VM arguments", e);
        }

        try {
            SynonymIndexSearcher searcher = new SynonymIndexSearcher();
            recSearcher.addSearcher(searcher, 1);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            SynonymIndexSearcher searcher = new SynonymIndexSearcher();
            recSearcher.addSearcher(searcher, -1);
        } catch (Exception e) {
            Assert.assertNotNull("we should get an exception here", e);
        }
    }

}
