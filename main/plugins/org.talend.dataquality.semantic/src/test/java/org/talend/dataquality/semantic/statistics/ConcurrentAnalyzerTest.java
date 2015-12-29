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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.AnalyzerSupplier;
import org.talend.datascience.common.inference.ConcurrentAnalyzer;

public class ConcurrentAnalyzerTest extends AnalyzerTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testThreadSafeConcurrentAccess() throws Exception {
        final URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI();
        final URI kwPath = this.getClass().getResource("/luceneIdx/keyword").toURI();
        try (Analyzer<SemanticType> analyzer = createSemanticAnalyzer(ddPath, kwPath)) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    final AtomicBoolean failed = doConcurrentAccess(analyzer);
                    assertEquals(failed.get(), false);
                };
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
        }
    }

    private Analyzer<SemanticType> createSemanticAnalyzer(final URI ddPath, final URI kwPath) {
        AnalyzerSupplier<Analyzer<SemanticType>> analyzer = new AnalyzerSupplier<Analyzer<SemanticType>>() {

            @Override
            public Analyzer<SemanticType> get() {
                final CategoryRecognizerBuilder builder = CategoryRecognizerBuilder.newBuilder() //
                        .ddPath(ddPath) //
                        .kwPath(kwPath) //
                        .setMode(CategoryRecognizerBuilder.Mode.LUCENE);
                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(builder);
                return semanticAnalyzer;
            }

        };
        return ConcurrentAnalyzer.make(analyzer, 20);
    }

    private AtomicBoolean doConcurrentAccess(Analyzer<SemanticType> semanticAnalyzer) {
        final AtomicBoolean failed = new AtomicBoolean();
        semanticAnalyzer.init();
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("customers_100_bug_TDQ10380.csv"));
        try {
            for (String[] data : records) {
                semanticAnalyzer.analyze(data);
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
        assertFalse(result.isEmpty());
        for (SemanticType columnSemanticType : result) {
            assertEquals(expectedCategories[columnIndex++], columnSemanticType.getSuggestedCategory());
        }
        return failed;
    }

}
