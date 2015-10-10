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
package org.talend.dataquality.semantic.statistics;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;

/**
 * This test is ignored for the time being because the dictionary path and key word path is hard coded, they should be
 * replaced later by elastic search server.
 * 
 * @author zhao
 *
 */
public class SemanticAnalyzerTest extends AnalyzerTest {

    private SemanticAnalyzer createAnalyzer() throws URISyntaxException {
        final URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI();
        final URI kwPath = this.getClass().getResource("/luceneIdx/keyword").toURI();
        final CategoryRecognizerBuilder builder = CategoryRecognizerBuilder.newBuilder() //
                .ddPath(ddPath) //
                .kwPath(kwPath) //
                .setMode(Mode.LUCENE);
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(builder);
        return semanticAnalyzer;
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testHandleCustomer100() throws URISyntaxException {
        SemanticAnalyzer semanticAnalyzer = createAnalyzer();
        semanticAnalyzer.init();
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("customers_100_bug_TDQ10380.csv"));
        for (String[] record : records) {
            semanticAnalyzer.analyze(record);
        }
        semanticAnalyzer.end();
        List<SemanticType> result = semanticAnalyzer.getResult();
        int columnIndex = 0;
        String[] expectedCategories = new String[] { //
        "", //
                SemanticCategoryEnum.FIRST_NAME.getId(), //
                SemanticCategoryEnum.CITY.getId(), //
                SemanticCategoryEnum.US_STATE_CODE.getId(), //
                "", //
                SemanticCategoryEnum.CITY.getId(), //
                "", //
                "", //
                "" //
        };
        for (SemanticType columnSemanticType : result) {
            assertEquals(expectedCategories[columnIndex++], columnSemanticType.getSuggestedCategory());
        }
    }

    @Test
    public void testSEDOL() throws URISyntaxException {
        SemanticAnalyzer semanticAnalyzer = createAnalyzer();
        semanticAnalyzer.init();
        String[] sedols = new String[] { "7108899", "B0YBKJ7", "4065663", "B0YBLH2", "2282765", "B0YBKL9", "5579107", "B0YBKR5",
                "5852842", "B0YBKT7", "B000300" };
        for (String field : sedols) {
            semanticAnalyzer.analyze(field);
        }
        semanticAnalyzer.end();
        List<SemanticType> result = semanticAnalyzer.getResult();
        int columnIndex = 0;
        String[] expectedCategories = new String[] { //
        "SEDOL" };
        for (SemanticType columnSemanticType : result) {
            assertEquals(expectedCategories[columnIndex++], columnSemanticType.getSuggestedCategory());
        }
    }

    @Test
    public void testHandle() throws URISyntaxException {
        SemanticAnalyzer semanticAnalyzer = createAnalyzer();
        semanticAnalyzer.init();
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("employee_1000.csv"));
        for (String[] record : records) {
            semanticAnalyzer.analyze(record);
        }
        semanticAnalyzer.end();
        List<SemanticType> result = semanticAnalyzer.getResult();
        int columnIndex = 0;
        String[] expectedCategories = new String[] { "", //
                "", //
                SemanticCategoryEnum.FIRST_NAME.getId(), //
                SemanticCategoryEnum.FIRST_NAME.getId(), //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                SemanticCategoryEnum.GENDER.getId(), //
                "" //
        };
        for (SemanticType columnSemanticType : result) {
            assertEquals(expectedCategories[columnIndex++], columnSemanticType.getSuggestedCategory());
        }
    }

    @Ignore
    @Test
    public void testValidMailHandle() throws URISyntaxException {
        SemanticAnalyzer semanticAnalyzer = createAnalyzer();
        semanticAnalyzer.init();
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("employee_valid_email.csv"));
        for (String[] record : records) {
            semanticAnalyzer.analyze(record);
        }
        semanticAnalyzer.end();
        List<SemanticType> result = semanticAnalyzer.getResult();
        int columnIndex = 0;
        String[] expectedCategories = new String[] { "", //
                "", //
                "", //
                "", //
                "EMAIL", //
                "", //
                "", //
                "", //
                "", //
                "DATE", //
                "DATE", //
                "", //
                "", //
                "", //
                "", //
                "GENDER", //
                "GENDER", //
                "" //
        };
        for (SemanticType columnSemanticType : result) {
            assertEquals(expectedCategories[columnIndex++], columnSemanticType.getSuggestedCategory());
        }
    }

    @Test
    public void testURLDetection() throws URISyntaxException {
        SemanticAnalyzer semanticAnalyzer = createAnalyzer();
        semanticAnalyzer.init();
        final List<String[]> records = getRecords(SemanticAnalyzerTest.class.getResourceAsStream("url.csv"));
        for (String[] record : records) {
            semanticAnalyzer.analyze(record);
        }
        semanticAnalyzer.end();
        List<SemanticType> result = semanticAnalyzer.getResult();
        int columnIndex = 0;
        String[] expectedCategories = new String[] { SemanticCategoryEnum.URL.getId() };
        for (SemanticType columnSemanticType : result) {
            assertEquals(expectedCategories[columnIndex++], columnSemanticType.getSuggestedCategory());
        }
    }

    @Test
    public void testConcurrentAccess() throws Exception {
        final AtomicBoolean failed = new AtomicBoolean();
        Runnable r = new Runnable() {

            @Override
            public void run() {
                final List<String[]> records = getRecords(AnalyzerTest.class
                        .getResourceAsStream("customers_100_bug_TDQ10380.csv"));
                SemanticAnalyzer semanticAnalyzer = null;
                try {
                    semanticAnalyzer = createAnalyzer();
                    for (String[] record : records) {
                        semanticAnalyzer.analyze(record);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failed.set(true);
                }
                semanticAnalyzer.end();
                List<SemanticType> result = semanticAnalyzer.getResult();
                int columnIndex = 0;
                String[] expectedCategories = new String[] { //
                "", //
                        SemanticCategoryEnum.FIRST_NAME.getId(), //
                        SemanticCategoryEnum.CITY.getId(), //
                        SemanticCategoryEnum.US_STATE_CODE.getId(), //
                        "", //
                        SemanticCategoryEnum.CITY.getId(), //
                        "", //
                        "", //
                        "" //
                };
                for (SemanticType columnSemanticType : result) {
                    assertEquals(expectedCategories[columnIndex++], columnSemanticType.getSuggestedCategory());
                }
            }
        };
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            workers.add(new Thread(r));
        }
        for (Thread worker : workers) {
            worker.start();
        }
        for (Thread worker : workers) {
            worker.join();
        }
        assertEquals(failed.get(), false);
    }
}
