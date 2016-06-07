package org.talend.dataquality.statistics.semantic;

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
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.statistics.SemanticAnalyzer;
import org.talend.dataquality.semantic.statistics.SemanticType;
import org.talend.dataquality.statistics.cardinality.CardinalityAnalyzer;
import org.talend.dataquality.statistics.frequency.DataTypeFrequencyAnalyzer;
import org.talend.dataquality.statistics.frequency.pattern.CompositePatternFrequencyAnalyzer;
import org.talend.dataquality.statistics.numeric.quantile.QuantileAnalyzer;
import org.talend.dataquality.statistics.numeric.summary.SummaryAnalyzer;
import org.talend.dataquality.statistics.text.TextLengthAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeAnalyzer;
import org.talend.dataquality.statistics.type.DataTypeEnum;
import org.talend.dataquality.statistics.type.DataTypeOccurences;

public class AnalyzerPerformanceTest {

    private static CategoryRecognizerBuilder builder;

    private static List<String[]> records;

    private final DataTypeEnum[] types = new DataTypeEnum[] { DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.INTEGER,
            DataTypeEnum.INTEGER, DataTypeEnum.TIME, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.STRING,
            DataTypeEnum.STRING, DataTypeEnum.STRING, DataTypeEnum.INTEGER, DataTypeEnum.STRING, DataTypeEnum.DATE,
            DataTypeEnum.DATE, DataTypeEnum.STRING, };

    @BeforeClass
    public static void setupBuilder() throws URISyntaxException {
        final URI ddPath = AnalyzerPerformanceTest.class.getResource("/luceneIdx/dictionary").toURI();
        final URI kwPath = AnalyzerPerformanceTest.class.getResource("/luceneIdx/keyword").toURI();
        builder = CategoryRecognizerBuilder.newBuilder() //
                .ddPath(ddPath) //
                .kwPath(kwPath) //
                .lucene();
        records = getRecords("Card_Exceptions_Preparation.csv");
    }

    private Analyzer<Result> setupBaselineAnalyzers() {
        // Analysis.QUALITY, Analysis.CARDINALITY, Analysis.TYPE, Analysis.FREQUENCY, Analysis.PATTERNS,
        // Analysis.SEMANTIC
        return Analyzers.with(//
                new CardinalityAnalyzer(), //
                new DataTypeAnalyzer(), //
                new DataTypeFrequencyAnalyzer(), //
                new CompositePatternFrequencyAnalyzer(types), //
                new SemanticAnalyzer(builder) //
        );
    }

    private Analyzer<Result> setupAdvancedAnalyzers() {
        // Analysis.LENGTH, Analysis.QUANTILES, Analysis.SUMMARY, Analysis.HISTOGRAM
        return Analyzers.with(//
                new TextLengthAnalyzer(), //
                new QuantileAnalyzer(types), //
                new SummaryAnalyzer(types) //
        );
    }

    @Test
    public void testBaselineAnalysis() {

        Analyzer<Result> analyzers = setupBaselineAnalyzers();

        String[] firstRecord = records.get(0);
        analyzers.analyze(firstRecord);
        long begin = System.currentTimeMillis();
        for (String[] record : records) {
            analyzers.analyze(record);
        }
        final List<Analyzers.Result> result = analyzers.getResult();
        long end = System.currentTimeMillis();
        System.out.println("baseline analysis took " + (end - begin) + " ms.");

        assertEquals(20, result.size());

        // Composite result assertions (there should be a DataType and a SemanticType)
        for (Analyzers.Result columnResult : result) {
            assertNotNull(columnResult.get(DataTypeOccurences.class));
            assertNotNull(columnResult.get(SemanticType.class));
        }
        // Data type assertions
        assertEquals(DataTypeEnum.INTEGER, result.get(0).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(1).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(2).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(3).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(4).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(5).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(6).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(7).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(8).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.TIME, result.get(9).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(10).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(11).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(12).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(13).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(14).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.INTEGER, result.get(15).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(16).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.DATE, result.get(17).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.DATE, result.get(18).get(DataTypeOccurences.class).getSuggestedType());
        assertEquals(DataTypeEnum.STRING, result.get(19).get(DataTypeOccurences.class).getSuggestedType());
        // Semantic types assertions
        String[] expectedCategories = new String[] { "", //
                SemanticCategoryEnum.US_STATE_CODE.getId(), //
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
                "", //
                "", //
                SemanticCategoryEnum.LAST_NAME.getId(), //
                "", //
                "", //
                "" //
        };
        for (int i = 0; i < expectedCategories.length; i++) {
            assertEquals(expectedCategories[i], result.get(i).get(SemanticType.class).getSuggestedCategory());
        }
    }

    @Test
    public void testAdvancedAnalysis() {

        Analyzer<Result> analyzers = setupAdvancedAnalyzers();

        long begin = System.currentTimeMillis();
        for (String[] record : records) {
            analyzers.analyze(record);
        }
        final List<Analyzers.Result> result = analyzers.getResult();
        long end = System.currentTimeMillis();
        System.out.println("advanced analysis took " + (end - begin) + " ms.");
    }

    private static List<String[]> getRecords(String path) {
        List<String[]> records = new ArrayList<String[]>();
        try {
            Reader reader = new FileReader(AnalyzerPerformanceTest.class.getResource(path).getPath());
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
