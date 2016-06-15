package org.talend.dataquality.semantic.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.common.inference.Analyzers;
import org.talend.dataquality.common.inference.Analyzers.Result;
import org.talend.dataquality.common.inference.ValueQualityStatistics;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum.RecognizerType;
import org.talend.dataquality.semantic.recognizer.CategoryFrequency;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

public class SemanticQualityAnalyzerTest {

    private static CategoryRecognizerBuilder builder;

    private static final List<String[]> RECORDS_CRM_CUST = getRecords("crm_cust.csv");

    private final String[] EXPECTED_CATEGORIES = new String[] { //
            "", //
            "CIVILITY", //
            "FIRST_NAME", //
            "LAST_NAME", //
            "COUNTRY_CODE_ISO3", //
            "ADDRESS_LINE", //
            "FR_POSTAL_CODE", //
            "CITY", //
            "", //
            "EMAIL", //
            "", //
            "", //
    };

    private static final long[][] EXPECTED_VALIDITY_COUNT = new long[][] { //
            new long[] { 1000, 0, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 990, 10, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 996, 4, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 518, 0, 482 }, //
            new long[] { 996, 4, 0 }, //
            new long[] { 1000, 0, 0 }, //
            new long[] { 1000, 0, 0 }, //
    };

    @BeforeClass
    public static void setupBuilder() throws URISyntaxException {
        final URI ddPath = SemanticQualityAnalyzerTest.class.getResource("/luceneIdx/dictionary").toURI();
        final URI kwPath = SemanticQualityAnalyzerTest.class.getResource("/luceneIdx/keyword").toURI();
        builder = CategoryRecognizerBuilder.newBuilder() //
                .ddPath(ddPath) //
                .kwPath(kwPath) //
                .lucene();
    }

    private Analyzer<Result> setupAnalyzers() {

        return Analyzers.with(//
                new SemanticAnalyzer(builder), //
                new SemanticQualityAnalyzer(builder, EXPECTED_CATEGORIES)//
        );
    }

    @Test
    public void testAnalysis() {
        Analyzer<Result> analyzers = setupAnalyzers();

        for (String[] record : RECORDS_CRM_CUST) {
            analyzers.analyze(record);
        }
        final List<Analyzers.Result> result = analyzers.getResult();

        assertEquals(EXPECTED_CATEGORIES.length, result.size());

        // Composite result assertions (there should be a DataType and a SemanticType)
        for (Analyzers.Result columnResult : result) {
            assertNotNull(columnResult.get(SemanticType.class));
            assertNotNull(columnResult.get(ValueQualityStatistics.class));
        }

        // Semantic types assertions
        for (int i = 0; i < EXPECTED_CATEGORIES.length; i++) {
            final SemanticType stats = result.get(i).get(SemanticType.class);
            // System.out.println("\"" + stats.getSuggestedCategory() + "\", //");
            assertEquals("Unexpected SemanticType on column " + i, EXPECTED_CATEGORIES[i],
                    result.get(i).get(SemanticType.class).getSuggestedCategory());
            for (CategoryFrequency cf : stats.getCategoryToCount().keySet()) {
                if (EXPECTED_CATEGORIES[i].equals(cf.getCategoryId())) {
                    SemanticCategoryEnum cat = SemanticCategoryEnum.getCategoryById(cf.getCategoryId());
                    if (RecognizerType.CLOSED_INDEX == cat.getRecognizerType() || //
                            RecognizerType.REGEX == cat.getRecognizerType()) {
                        assertEquals("Unexpected SemanticType occurence on column " + i, EXPECTED_VALIDITY_COUNT[i][0],
                                cf.getCount());
                    }
                }
            }
        }

        // Semantic validation assertions
        for (int i = 0; i < EXPECTED_CATEGORIES.length; i++) {
            final ValueQualityStatistics stats = result.get(i).get(ValueQualityStatistics.class);
            // System.out.println("new long[] {" + stats.getValidCount() + ", " + stats.getInvalidCount() + ", "
            // + stats.getEmptyCount() + "}, //");
            assertEquals("Unexpected valid count on column " + i, EXPECTED_VALIDITY_COUNT[i][0], stats.getValidCount());
            assertEquals("Unexpected invalid count on column " + i, EXPECTED_VALIDITY_COUNT[i][1], stats.getInvalidCount());
            assertEquals("Unexpected empty count on column " + i, EXPECTED_VALIDITY_COUNT[i][2], stats.getEmptyCount());
            assertEquals("Unexpected unknown count on column " + i, 0, stats.getUnknownCount());
        }
    }

    private static List<String[]> getRecords(String path) {
        List<String[]> records = new ArrayList<String[]>();
        try {
            Reader reader = new FileReader(SemanticQualityAnalyzerTest.class.getResource(path).getPath());
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader();
            Iterable<CSVRecord> csvRecords = csvFormat.parse(reader);

            for (CSVRecord csvRecord : csvRecords) {
                String[] values = new String[csvRecord.size()];
                for (int i = 0; i < csvRecord.size(); i++) {
                    values[i] = csvRecord.get(i);
                }
                records.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}
