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
package org.talend.datascience.common.inference.type;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.datascience.common.inference.AnalyzerTest;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class DataTypeAnalyzerTest extends AnalyzerTest {

    public DataTypeAnalyzer createDataTypeanalyzer() {
        DataTypeAnalyzer analyzer = new DataTypeAnalyzer();
        analyzer.init();
        return analyzer;
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEmptyRecords() throws Exception {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        analyzer.analyze();
        assertEquals(0, analyzer.getResult().size());
        analyzer.analyze(null);
        assertEquals(0, analyzer.getResult().size());
        analyzer.analyze("");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    public void testAnalysisResize() throws Exception {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        analyzer.analyze("aaaa");
        assertEquals(1, analyzer.getResult().size());
        analyzer.analyze("aaaa", "bbbb");
        assertEquals(2, analyzer.getResult().size());
    }

    @Test
    public void testString() throws Exception {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        // One string
        analyzer.analyze("aaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
        // Two strings
        analyzer.analyze("bbbb");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
        // One integer
        analyzer.analyze("2");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    public void testInteger() throws Exception {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        // One integer
        analyzer.analyze("0");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.INTEGER, analyzer.getResult().get(0).getSuggestedType());
        // Two integers
        analyzer.analyze("1");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.INTEGER, analyzer.getResult().get(0).getSuggestedType());
        // One string
        analyzer.analyze("aaaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.INTEGER, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    @Ignore
    public void testIncorrectCharDetection() throws Exception {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        // One character
        analyzer.analyze("M");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
        // Two characters
        analyzer.analyze("M");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
        // The new value should invalidate previous assumptions about CHAR value
        // (no longer a CHAR).
        analyzer.analyze("Mme");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
    }

    // TODO All other data types

    @Test
    public void testBoolean() throws Exception {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        // One boolean
        analyzer.analyze("true");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
        // Two booleans
        analyzer.analyze("false");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
        // One string
        analyzer.analyze("aaaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    public void testMixedDoubleInteger() throws Exception {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        String[] toTestMoreDouble = { "1.2", "3.4E-10", "1" };
        for (String string : toTestMoreDouble) {
            analyzer.analyze(string);
        }
        assertEquals(DataType.Type.DOUBLE, analyzer.getResult().get(0).getSuggestedType());

        String[] toTestMoreInteger = { "1.2", "3.4E-10", "1", "3", "6", "80" };
        for (String string : toTestMoreInteger) {
            analyzer.analyze(string);
        }
        assertEquals(DataType.Type.INTEGER, analyzer.getResult().get(0).getSuggestedType());

    }

    @Test
    public void testMultipleColumns() throws Exception {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        analyzer.analyze("true", "aaaa");
        analyzer.analyze("true", "bbbb");
        assertEquals(2, analyzer.getResult().size());
        assertEquals(DataType.Type.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(1).getSuggestedType());
    }

    @Test
    public void testInferDataTypes() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("employee_100.csv"));
        for (String[] record : records) {
            analyzer.analyze(record);
        }
        final List<DataType> result = analyzer.getResult();
        assertEquals(18, result.size());
        assertEquals(DataType.Type.INTEGER, result.get(0).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(1).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(2).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(3).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(4).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(5).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(6).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(7).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(8).getSuggestedType());
        assertEquals(DataType.Type.DATE, result.get(9).getSuggestedType());
        assertEquals(DataType.Type.DATE, result.get(10).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(11).getSuggestedType());
        assertEquals(DataType.Type.DOUBLE, result.get(12).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(13).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(14).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(15).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(16).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(17).getSuggestedType());
    }

    @Test
    public void testDateColumn() throws Exception {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        analyzer.analyze("10-Oct-2015");
        analyzer.analyze("11-Oct-2015");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.DATE, analyzer.getResult().get(0).getSuggestedType());
    }

    /**
     * Test the order of column index to see whether it is same after the inferring type done comparing the before or
     * not.
     */
    @Test
    public void testInferTypesColumnIndexOrder() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("customers_100_bug_TDQ10380.csv"));
        for (String[] record : records) {
            analyzer.analyze(record);
        }
        final List<DataType> result = analyzer.getResult();
        assertEquals(DataType.Type.INTEGER, result.get(0).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(1).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(2).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(3).getSuggestedType());
        assertEquals(DataType.Type.DATE, result.get(4).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(5).getSuggestedType());
        assertEquals(DataType.Type.DATE, result.get(6).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(7).getSuggestedType());
        assertEquals(DataType.Type.DOUBLE, result.get(8).getSuggestedType());
    }

    @Test
    public void testGetDataTypeWithRatio() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        String[] records = new String[] { "1", "2", "3", "4", "1.0", "2.0", "3.0" };
        for (String record : records) {
            analyzer.analyze(record);
        }
        analyzer.end();
        final List<DataType> result = analyzer.getResult();
        assertEquals(DataType.Type.INTEGER, result.get(0).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(0).getSuggestedType(0.6));
        assertEquals(DataType.Type.DOUBLE, result.get(0).getSuggestedType(0.3));
    }
    @Test
    public void testGetDataTypeWithRatioEmpty() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        String[] records = new String[] { "", "", "", "", "1.0", "2.0", "3.0" };
        for (String record : records) {
            analyzer.analyze(record);
        }
        analyzer.end();
        final List<DataType> result = analyzer.getResult();
        assertEquals(DataType.Type.DOUBLE, result.get(0).getSuggestedType());
        assertEquals(DataType.Type.DOUBLE, result.get(0).getSuggestedType(0.9));
    }
    @Test
    public void testGetDataTypeWithRatioEmpty2() {
        DataTypeAnalyzer analyzer = createDataTypeanalyzer();
        String[] records = new String[] { "1", "2", "", "", "1.0", "2.0" };
        for (String record : records) {
            analyzer.analyze(record);
        }
        analyzer.end();
        final List<DataType> result = analyzer.getResult();
        assertEquals(DataType.Type.INTEGER, result.get(0).getSuggestedType(0.9));
        assertEquals(DataType.Type.DOUBLE, result.get(0).getSuggestedType(0.1));
    }

}
