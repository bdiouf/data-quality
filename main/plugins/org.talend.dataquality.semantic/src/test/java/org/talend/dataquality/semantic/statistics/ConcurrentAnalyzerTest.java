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

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.AnalyzerSupplier;
import org.talend.datascience.common.inference.ConcurrentAnalyzer;

public class ConcurrentAnalyzerTest extends AnalyzerTest {

    private AtomicBoolean errorOccurred = new AtomicBoolean();

    @Before
    public void setUp() throws Exception {
        errorOccurred.set(false);
    }

    @Test
    public void testThreadSafeConcurrentAccess() {
        try {
            URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI();
            URI kwPath = this.getClass().getResource("/luceneIdx/keyword").toURI();
            assertNotNull("Keyword dictionary not loaded", kwPath);
            assertNotNull("data dictionary not loaded", ddPath);
            final CategoryRecognizerBuilder builder = CategoryRecognizerBuilder.newBuilder() //
                    .ddPath(ddPath) //
                    .kwPath(kwPath) //
                    .setMode(CategoryRecognizerBuilder.Mode.LUCENE);
            AnalyzerSupplier<Analyzer<SemanticType>> analyzer1 = new AnalyzerSupplier<Analyzer<SemanticType>>() {

                @Override
                public Analyzer<SemanticType> get() {
                    return new SemanticAnalyzer(builder);
                }

            };
            final Analyzer<SemanticType> analyzer = ConcurrentAnalyzer.make(analyzer1, 2);
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    doConcurrentAccess(analyzer);
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
            assertEquals("ConcurrentAccess not failed", false, errorOccurred.get());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail("Problem while loading dictionaries");
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Thread has been interrupted");
        }
    }

    @Test
    public synchronized void testThreadUnsafeConcurrentAccess() throws Exception {
        final URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI();
        final URI kwPath = this.getClass().getResource("/luceneIdx/keyword").toURI();
        final CategoryRecognizerBuilder builder = CategoryRecognizerBuilder.newBuilder() //
                .ddPath(ddPath) //
                .kwPath(kwPath) //
                .setMode(CategoryRecognizerBuilder.Mode.LUCENE);
        try (Analyzer<SemanticType> analyzer = new SemanticAnalyzer(builder)) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    doConcurrentAccess(analyzer);
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
            assertEquals("ConcurrentAccess failed", true, errorOccurred.get());
        }
    }

    private void doConcurrentAccess(Analyzer<SemanticType> semanticAnalyzer) {
        try {
            semanticAnalyzer.init();
            final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("customers_100_bug_TDQ10380.csv"));
            for (String[] data : records) {
                try {
                    semanticAnalyzer.analyze(data);
                } catch (Throwable e) {
                    errorOccurred.set(true);
                }
            }
            semanticAnalyzer.end();
            List<SemanticType> result = semanticAnalyzer.getResult();
            int columnIndex = 0;
            final String[] expectedCategories = new String[] { //
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
            if (result.isEmpty()) {
                errorOccurred.set(true);
            }
            for (SemanticType columnSemanticType : result) {
                if (!expectedCategories[columnIndex++].equals(columnSemanticType.getSuggestedCategory())) {
                    errorOccurred.set(true);
                }
            }
        } catch (Exception e) {
            errorOccurred.set(true);
        } finally {
            try {
                semanticAnalyzer.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
