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

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.AnalyzerSupplier;
import org.talend.datascience.common.inference.ConcurrentAnalyzer;

public class ConcurrentAnalyzerTest extends AnalyzerTest {

    private AtomicBoolean errorOccurred = new AtomicBoolean();

    @Test
    public void testThreadSafeConcurrentAccess() {
        errorOccurred.set(false);
        URI ddPath = null;
        URI kwPath = null;
        try {
            ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI();
            kwPath = this.getClass().getResource("/luceneIdx/keyword").toURI();
            assertNotNull("Keyword dictionary not loaded", kwPath);
            assertNotNull("data dictionary not loaded", ddPath);
            final Analyzer<SemanticType> analyzer = createSemanticAnalyzer(ddPath, kwPath);
            Runnable r = new Runnable() {

                @Override
                public void run() {
                   doConcurrentAccess(analyzer);
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

            assertEquals("ConcurrentAccess not failed", false, errorOccurred.get());
            // fail("ERRROR");
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
        errorOccurred.set(false);
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

    private void doConcurrentAccess(Analyzer<SemanticType> semanticAnalyzer) {
        semanticAnalyzer.init();
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("customers_100_bug_TDQ10380.csv"));
        try {

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
                if(!expectedCategories[columnIndex++].equals( columnSemanticType.getSuggestedCategory())){
                    errorOccurred.set(true);
                }
            }
        } catch (Exception e) {
            errorOccurred.set(true);
        }
    }
}
