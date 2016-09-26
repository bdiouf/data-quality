// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.grouping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class SwooshRecordGroupingTest {

    private static Logger log = Logger.getLogger(SwooshRecordGroupingTest.class);

    /**
     * The input data.
     */
    private List<String[]> inputList = null;

    private IRecordGrouping<String> recordGroup = null;

    private static final String columnDelimiter = "|"; //$NON-NLS-1$

    private List<String[]> groupingRecords = new ArrayList<String[]>();

    @Before
    public void setUp() throws Exception {
        groupingRecords.clear();
    }

    @Test
    public void testDoGroup() throws IOException {
        // account_num , last name, first name, middle initial, address, city, state, zipcode, blocking key
        // Read input data file.
        InputStream in = this.getClass().getResourceAsStream("incoming_customers_swoosh1.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        // set the matching parameters
        // matching parameters for lname
        recordGroup = createRecordGroup();
        recordGroup.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        SurvivorShipAlgorithmParams survAlgParams = new SurvivorShipAlgorithmParams();
        SurvivorshipFunction func = survAlgParams.new SurvivorshipFunction();
        func.setParameter(""); //$NON-NLS-1$
        func.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        survAlgParams.setSurviorShipAlgos(new SurvivorshipFunction[] { func });
        // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<Integer, SurvivorshipFunction>();
        SurvivorshipFunction survFunc = survAlgParams.new SurvivorshipFunction();
        survFunc.setParameter(StringUtils.EMPTY);
        survFunc.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.LONGEST);
        defaultSurvRules.put(1, survFunc);
        survAlgParams.setDefaultSurviorshipRules(defaultSurvRules);

        recordGroup.setSurvivorShipAlgorithmParams(survAlgParams);
        recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        List<Map<String, String>> matchingRule = new ArrayList<Map<String, String>>();

        Map<String, String> lnameRecords = createTmpMap(null, "1", null, "ID", "0", "JARO_WINKLER", "NO", "1", null, null, null);

        matchingRule.add(lnameRecords);

        recordGroup.addMatchRule(matchingRule);
        try {
            recordGroup.initialize();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }

        recordGroup.setIsOutputDistDetails(true);
        recordGroup.setAcceptableThreshold(0.95f);
        // loop on all input rows.
        try {
            for (String[] inputRow : inputList) {
                recordGroup.doGroup(inputRow);
            }
            recordGroup.end();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        // Assertions

        for (String[] rds : groupingRecords) {
            if (rds[0].equals("2") && rds[11].equals("true")) { //$NON-NLS-1$ //$NON-NLS-2$
                // The group size should be 2 for master record which id is 2
                Assert.assertEquals(2, Integer.valueOf(rds[rds.length - 5]).intValue());
            } else if (rds[0].equals("7") && rds[11].equals("true")) { //$NON-NLS-1$ //$NON-NLS-2$
                // The group size should be 2 for master record which id is 7
                Assert.assertEquals(2, Integer.valueOf(rds[rds.length - 5]).intValue());
            } else if (rds[0].equals("1") && rds[11].equals("true")) { //$NON-NLS-1$ //$NON-NLS-2$
                // The group size should be 3 for master record which id is 1
                Assert.assertEquals(3, Integer.valueOf(rds[rds.length - 5]).intValue());
            }
        }
    }

    @Test
    public void testDoGroupMergeValues() throws IOException {
        InputStream in = this.getClass().getResourceAsStream("incoming_customers_swoosh2.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }
        recordGroup = createRecordGroup();
        recordGroup.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        SurvivorShipAlgorithmParams survivorShipAlgorithmParams = new SurvivorShipAlgorithmParams();
        SurvivorshipFunction func = survivorShipAlgorithmParams.new SurvivorshipFunction();
        func.setParameter(""); //$NON-NLS-1$
        func.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        survivorShipAlgorithmParams.setSurviorShipAlgos(new SurvivorshipFunction[] { func });
        recordGroup.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);

        // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<Integer, SurvivorshipFunction>();
        SurvivorshipFunction survFunc = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc.setParameter(StringUtils.EMPTY);
        survFunc.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        defaultSurvRules.put(0, survFunc);

        SurvivorshipFunction survFunc2 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc2.setParameter(StringUtils.EMPTY);
        survFunc2.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.LONGEST);
        defaultSurvRules.put(2, survFunc2);

        SurvivorshipFunction survFunc3 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc3.setParameter(StringUtils.EMPTY);
        survFunc3.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        defaultSurvRules.put(3, survFunc3);

        SurvivorshipFunction survFunc4 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc4.setParameter(StringUtils.EMPTY);
        survFunc4.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.LARGEST);
        defaultSurvRules.put(4, survFunc4);

        SurvivorshipFunction survFunc5 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc5.setParameter(StringUtils.EMPTY);
        survFunc5.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_RECENT);
        defaultSurvRules.put(5, survFunc5);

        SurvivorshipFunction survFunc6 = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc6.setParameter(StringUtils.EMPTY);
        survFunc6.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        defaultSurvRules.put(6, survFunc6);

        survivorShipAlgorithmParams.setDefaultSurviorshipRules(defaultSurvRules);

        recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        List<Map<String, String>> matchingRule = new ArrayList<Map<String, String>>();

        Map<String, String> lnameRecords = createTmpMap(null, "0.9", null, "NAME", "1", "JARO_WINKLER", "NO", "1", null, null,
                null);
        matchingRule.add(lnameRecords);

        recordGroup.addMatchRule(matchingRule);
        try {
            recordGroup.initialize();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        }

        recordGroup.setIsOutputDistDetails(true);
        recordGroup.setAcceptableThreshold(0.95f);
        // loop on all input rows.
        try {
            for (String[] inputRow : inputList) {
                recordGroup.doGroup(inputRow);
            }
            recordGroup.end();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        // Assertions
        for (String[] rds : groupingRecords) {
            if (rds[10].equals("true")) { //$NON-NLS-1$
                // Master record's group size is 4
                Assert.assertEquals(4, Integer.valueOf(rds[rds.length - 5]).intValue());
                // Group quality.
                Assert.assertEquals(0.9666666746139526, Double.valueOf(rds[rds.length - 2]).doubleValue(), 0d);
                // Assert the merged value is the "most common" value.
                Assert.assertEquals("Amburgay", rds[1]);
                // Longest
                Assert.assertEquals("Gregory", rds[2]);
                // Most common
                Assert.assertEquals("R.", rds[3]);
                // Largest
                Assert.assertEquals("4151", rds[4]);
                // Most recent. This is the logic from MDM , shoudn't be 2015-10-1 ?
                Assert.assertEquals("2014-10-1", rds[5]);
            }
        }
    }

    @Test
    public void testSwooshIntMatchGroup()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("customers_swoosh_tmatch.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        int n = 0;
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.GRP_SIZE == 7) {
                Assert.assertEquals("USAUSAUSAUSAUSAUSAUSA", one.country);

                for (int i = n + 1; i < n + 8; i++) {
                    Assert.assertEquals("USA", groupRows_tMatchGroup_1.get(i).country);
                }
                break;
            }
            n++;
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm

        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_tmatch.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE
            // + "--" + one.MASTER);
            if (one.GRP_SIZE == 7) {
                Assert.assertTrue(StringUtils.equals("BB", one.country) || StringUtils.equals("FFF", one.country));
                Assert.assertTrue(StringUtils.equals("YY", one.city) || StringUtils.equals("G", one.city));
            }
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup_3groups()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_swoosh.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // System.out.println("swoosh with multipass of 3 groups");
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.GRP_SIZE == 7) {
                Assert.assertTrue(StringUtils.equals("FFF", one.country) || StringUtils.equals("BB", one.country));
                Assert.assertTrue(StringUtils.equals("G", one.city) || StringUtils.equals("AAA", one.city));
            }
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup_withNoNewMasterIn2ndPass()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_swoosh_noNew.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.GRP_SIZE == 2) {
                Assert.assertTrue(StringUtils.equals("B", one.country) || StringUtils.equals("F", one.country));
                Assert.assertTrue(StringUtils.equals("YY", one.city) || StringUtils.equals("WW", one.city));
            }
        }
    }

    @Test
    public void testSwooshIntMatchGroup_withBlocks()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_with_blocks.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        List<String[]> inputList2 = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            if (StringUtils.equals("F", fields[1])) {
                inputList.add(fields);
            } else {
                inputList2.add(fields);
            }
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while
        recordGroupImp_tMatchGroup_1.end();

        for (String[] inputRow : inputList2) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while
        recordGroupImp_tMatchGroup_1.end();

        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
    }

    @Test
    public void testSwooshIntMatchGroup_withoutBlocks()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_without_blocks.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while
        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
    }

    @Test
    public void testSetDefaultSurvivorshipRules() {
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "BOOLEAN");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "MostCommon");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "STRING");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "MostRecent");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "NUMBER");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);

        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("MostCommon", "1", "", "stu_Address", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        Map<String, String> columnWithType_tMatchGroup_1 = new HashMap<String, String>();

        Map<String, String> columnWithIndex_tMatchGroup_1 = new HashMap<String, String>();

        columnWithType_tMatchGroup_1.put("id", "id_Integer");
        columnWithIndex_tMatchGroup_1.put("id", "0");
        columnWithType_tMatchGroup_1.put("stu_Gender", "id_String");
        columnWithIndex_tMatchGroup_1.put("stu_Gender", "1");
        columnWithType_tMatchGroup_1.put("stu_Address", "id_String");
        columnWithIndex_tMatchGroup_1.put("stu_Address", "2");
        columnWithType_tMatchGroup_1.put("stu_Text", "id_String");
        columnWithIndex_tMatchGroup_1.put("stu_Text", "3");
        columnWithType_tMatchGroup_1.put("stu_SmallInt", "id_Short");
        columnWithIndex_tMatchGroup_1.put("stu_SmallInt", "4");
        columnWithType_tMatchGroup_1.put("stu_tinyint", "id_Byte");
        columnWithIndex_tMatchGroup_1.put("stu_tinyint", "5");
        columnWithType_tMatchGroup_1.put("stu_bigInt", "id_Long");
        columnWithIndex_tMatchGroup_1.put("stu_bigInt", "6");
        columnWithType_tMatchGroup_1.put("stu_double", "id_Double");
        columnWithIndex_tMatchGroup_1.put("stu_double", "7");
        columnWithType_tMatchGroup_1.put("stu_BIT", "id_Boolean");
        columnWithIndex_tMatchGroup_1.put("stu_BIT", "8");
        columnWithType_tMatchGroup_1.put("stu_Char", "id_String");
        columnWithIndex_tMatchGroup_1.put("stu_Char", "9");
        columnWithType_tMatchGroup_1.put("stu_date", "id_Date");
        columnWithIndex_tMatchGroup_1.put("stu_date", "10");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithIndex_tMatchGroup_1.put("GID", "11");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "12");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithIndex_tMatchGroup_1.put("MASTER", "13");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithIndex_tMatchGroup_1.put("SCORE", "14");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "15");

        AnalysisSwooshMatchRecordGrouping analysisSwooshMatchRecordGrouping = new AnalysisSwooshMatchRecordGrouping();
        analysisSwooshMatchRecordGrouping.setOrginalInputColumnSize(11);
        analysisSwooshMatchRecordGrouping.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            analysisSwooshMatchRecordGrouping.addMatchRule(matcherList);
        }
        try {
            analysisSwooshMatchRecordGrouping.initialize();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Assert.fail("initial failed :" + e.getMessage());
        }
        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(analysisSwooshMatchRecordGrouping, matchingRulesAll_tMatchGroup_1,
                        defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1, columnWithIndex_tMatchGroup_1);

        Map<Integer, SurvivorshipFunction> defaultSurviorshipRules = survivorShipAlgorithmParams_tMatchGroup_1
                .getDefaultSurviorshipRules();
        Iterator<Integer> keys = defaultSurviorshipRules.keySet().iterator();
        boolean containID = false, containAdd = false, containBIT = false;
        while (keys.hasNext()) {
            Integer next = keys.next();
            SurvivorshipFunction survivorshipFunction = defaultSurviorshipRules.get(next);
            Assert.assertNotNull(survivorshipFunction);
            Assert.assertNotEquals("", survivorshipFunction.getSurvivorShipKey());
            Assert.assertNotNull(survivorshipFunction.getSurvivorShipKey());
            if ("id".equals(survivorshipFunction.getSurvivorShipKey())) {
                containID = true;
                Assert.assertTrue(next.intValue() == 0);
                Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_RECENT, survivorshipFunction.getSurvivorShipAlgoEnum());
            } else if ("stu_Address".equals(survivorshipFunction.getSurvivorShipKey())) {
                containAdd = true;
                Assert.assertTrue(next.intValue() == 2);
                Assert.assertEquals(SurvivorShipAlgorithmEnum.MOST_COMMON, survivorshipFunction.getSurvivorShipAlgoEnum());
            } else if ("stu_BIT".equals(survivorshipFunction.getSurvivorShipKey())) {
                containBIT = true;
                Assert.assertTrue(next.intValue() == 8);
                Assert.assertEquals(SurvivorShipAlgorithmEnum.CONCATENATE, survivorshipFunction.getSurvivorShipAlgoEnum());
            }
        }
        Assert.assertTrue(containID);
        Assert.assertTrue(containAdd);
        Assert.assertTrue(containBIT);
    }

    @Test
    public void testSwooshMultipasstMatchGroup_oneRecord()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_onerecord.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
    }

    @Test
    public void testSwooshMultipasstMatchGroup_differentRecord()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_different.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        System.out.println("swoosh with multipass :lost some record");
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER);
            if (one.MASTER) {
                Assert.assertTrue(one.GRP_SIZE == 12);
            }
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup_score()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "0.86", "", "country", "2", "Jaro", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_score.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            Assert.assertTrue(one.SCORE > 0);
        }
    }

    @Test
    public void testSwooshIntMatchGroup_multipleRules()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>(3);
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "stuID", "0", "dummy", "NO", 0 + "", "nullMatchNull", null,
                null);
        matcherList_tMatchGroup_1.add(0, tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1", "", "stuAddress", "1", "Exact", "NO", 1 + "", "nullMatchNull",
                0.86 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(1, tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1", "", "stuProvinceID", "2", "Exact", "NO", 2 + "", "nullMatchNull",
                0.86 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(2, tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();

        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "stuProvinceID", "2", "dummy", "NO", 1 + "",
                "nullMatchNull", 0 + "", null);
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "stuID", "0", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = createTmpMap("Concatenate", "1.0", "", "stuAddress", "1", "dummy", "NO", 0 + "", "nullMatchNull",
                null, null);
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        Collections.sort(matcherList_tMatchGroup_1, new Comparator<Map<String, String>>() {

            @Override
            public int compare(Map<String, String> map1, Map<String, String> map2) {
                return map1.get("COLUMN_IDX").compareTo(map2.get("COLUMN_IDX"));
            }
        });

        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multiRule_swoosh.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1); // assert
        int groupOfOne = 0;
        int groupOfTwo = 0;
        ;
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.GRP_SIZE == 1) {
                groupOfOne++;
            } else if (one.GRP_SIZE == 2) {
                groupOfTwo++;
            }
        }
        Assert.assertTrue(groupOfOne == 10);
        Assert.assertTrue(groupOfTwo == 2);
    }

    @Test
    public void testSwooshIntMatchGroup_withCustomMatcher()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "custom", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        tmpMap_tMatchGroup_1.put("CUSTOMER_MATCH_CLASS", "org.talend.dataquality.record.linkage.grouping.MyDistance" + "");
        tmpMap_tMatchGroup_1.put("JAR_PATH",
                "/home/runtime/TDQ_EE_NEW2/LOCAL_PROJECT/TDQ_Libraries/Indicators/User Defined Indicators/lib/test.mydistance.jar");

        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("customers_swoosh_tmatch.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        int n = 0;
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.GRP_SIZE == 7) {
                Assert.assertEquals("USAUSAUSAUSAUSAUSAUSA", one.country);

                for (int i = n + 1; i < n + 8; i++) {
                    Assert.assertEquals("USA", groupRows_tMatchGroup_1.get(i).country);
                }
                break;
            }
            n++;
        }
    }

    @Test
    public void testSwooshIntMatchGroup_withDisplayAttLabels()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                if (0 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = String.valueOf(row[1]);
                }
                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = String.valueOf(row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = String.valueOf(row[3]);
                }
                if (4 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = 0;
                    }
                }
                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = Boolean.valueOf((String) row[5]);
                }
                if (6 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }
                if (7 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    }
                }
                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MATCHING_DISTANCES = row[8] == null ? null : String.valueOf(row[8]);
                }
                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(true);
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("customers_swoosh_tmatch.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.MASTER == false) {
                Assert.assertEquals("country: 1.0", one.MATCHING_DISTANCES);
            }
        }
    }

    @Test
    public void testSwooshMultipasstMatchGroup_withOutputDD()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "-", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                if (0 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }
                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = String.valueOf(row[1]);
                }
                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = String.valueOf(row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = String.valueOf(row[3]);
                }
                if (4 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = 0;
                    }
                }
                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = Boolean.valueOf((String) row[5]);
                }
                if (6 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (NumberFormatException | NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }
                if (7 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (NumberFormatException | NullPointerException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    }
                }
                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MATCHING_DISTANCES = row[8] == null ? null : String.valueOf(row[8]);
                }
                if (9 < row.length) {
                    outStuct_tMatchGroup_1.MERGE_INFO = String.valueOf(row[9]);
                }

                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm

        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "9");
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter("|");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(true);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(true);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("multipass_outputDD.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        // assert
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER + "--" + one.GRP_QUALITY + "--" + one.SCORE + "--" + one.MERGE_INFO + "--"
                    + one.MATCHING_DISTANCES);
            if (StringUtils.equals("true", one.MERGE_INFO) && !one.MASTER) {
                Assert.assertEquals("country: 1.0", one.MATCHING_DISTANCES);
            } else if (StringUtils.equals("false", one.MERGE_INFO) && !one.MASTER) {
                Assert.assertEquals("address:1.0", one.MATCHING_DISTANCES);
            }
        }
    }

    @Test
    public void testSwooshIntMatchGroup_tdq11599Smallelst()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;

        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Smallest");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "NUMBER");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);

        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("tdq11599.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.MASTER) {
                Assert.assertEquals("1", one.city);

                break;
            }
        }
    }

    private void initialize(AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1) {
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType(true);
    }

    @Test
    public void testSwooshIntMatchGroup_tdq11599Largest()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;

        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Largest");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "NUMBER");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);

        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "country", "2", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = createComponent(masterRows_tMatchGroup_1,
                groupRows_tMatchGroup_1, indexMap_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_Integer", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);
        columnWithType_tMatchGroup_1.put("ORIGINAL_RECORD", "id_Object");
        columnWithIndex_tMatchGroup_1.put("ORIGINAL_RECORD", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("tdq11599.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);
        for (row2Struct one : groupRows_tMatchGroup_1) {
            if (one.MASTER) {
                Assert.assertEquals("12", one.city);

                break;
            }
        }

    }

    private IRecordGrouping<String> createRecordGroup() {
        return new AbstractRecordGrouping<String>() {

            /*
             * (non-Javadoc)
             * 
             * @see AbstractRecordGrouping#isMaster(java.lang.Object)
             */
            @Override
            protected boolean isMaster(String col) {
                return "true".equals(col);
            }

            /*
             * (non-Javadoc)
             * 
             * @see AbstractRecordGrouping#createTYPEArray(int)
             */
            @Override
            protected String[] createTYPEArray(int size) {
                return new String[size];
            }

            /*
             * (non-Javadoc)
             * 
             * @see AbstractRecordGrouping#outputRow(java.lang.String)
             */
            @Override
            protected void outputRow(String[] row) {
                groupingRecords.add(row);
            }

            @Override
            protected void outputRow(RichRecord row) {
                List<DQAttribute<?>> originRow = row.getOutputRow(swooshGrouping.getOldGID2New());
                String[] strRow = new String[originRow.size()];
                int idx = 0;
                for (DQAttribute<?> attr : originRow) {
                    strRow[idx] = attr.getValue();
                    idx++;
                }
                outputRow(strRow);

            }

            @Override
            protected String incrementGroupSize(String oldGroupSize) {
                return String.valueOf(Integer.parseInt(String.valueOf(oldGroupSize)) + 1);
            }

            @Override
            protected String castAsType(Object objectValue) {
                return String.valueOf(objectValue);
            }
        };
    }

    private Map<String, String> createTmpMap(String survivorShipFunction, String attributeThresold, String parameter,
            String attributeName, String columnId, String matchingType, String tokenType, String confidenceWeight,
            String handleNull, String recordMatchingThreshold, String matchAlgo) {
        Map<String, String> map = new HashMap<String, String>();
        if (survivorShipFunction != null)
            map.put("SURVIVORSHIP_FUNCTION", survivorShipFunction);
        if (attributeThresold != null)
            map.put("ATTRIBUTE_THRESHOLD", attributeThresold);
        if (parameter != null)
            map.put("PARAMETER", parameter);
        if (attributeName != null)
            map.put("ATTRIBUTE_NAME", attributeName);
        if (columnId != null)
            map.put("COLUMN_IDX", columnId);
        if (matchingType != null)
            map.put("MATCHING_TYPE", matchingType);
        if (tokenType != null)
            map.put("TOKENIZATION_TYPE", tokenType);
        if (confidenceWeight != null)
            map.put("CONFIDENCE_WEIGHT", confidenceWeight);
        if (handleNull != null)
            map.put("HANDLE_NULL", handleNull);
        if (recordMatchingThreshold != null)
            map.put("RECORD_MATCH_THRESHOLD", recordMatchingThreshold);
        if (matchAlgo != null)
            map.put("MATCHING_ALGORITHM", matchAlgo);

        return map;
    }

    private Map<String, String> fillColumn(String customerId, String city, String country, String gid, String grpSize,
            String master, String score, String grpQuality, String mergeInfo) {
        Map<String, String> map = new HashMap<String, String>();
        if (customerId != null)
            map.put("customer_id", customerId);
        if (city != null)
            map.put("city", city);
        if (country != null)
            map.put("country", country);
        if (gid != null)
            map.put("GID", gid);
        if (grpQuality != null)
            map.put("GRP_SIZE", grpQuality);
        if (master != null)
            map.put("MASTER", master);
        if (score != null)
            map.put("SCORE", score);
        if (grpQuality != null)
            map.put("GRP_QUALITY", grpQuality);
        if (mergeInfo != null)
            map.put("MERGE_INFO", mergeInfo);
        return map;
    }

    private AbstractRecordGrouping<Object> createComponent(final List<row2Struct> masterRows_tMatchGroup_1,
            final List<row2Struct> groupRows_tMatchGroup_1, final Map<String, Integer> indexMap_tMatchGroup_1) {
        return new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                if (0 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }
                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = String.valueOf(row[1]);
                }
                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = String.valueOf(row[2]);
                }

                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = String.valueOf(row[3]);
                }
                if (4 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = 0;
                    }
                }
                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = Boolean.valueOf((String) row[5]);
                }
                if (6 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (NumberFormatException | NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }
                if (7 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (NumberFormatException | NullPointerException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    }
                }
                if (8 < row.length) {
                    outStuct_tMatchGroup_1.MERGE_INFO = String.valueOf(row[8]);
                }
                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };
    }

    class row2Struct implements Comparable<row2Struct> {

        protected static final int DEFAULT_HASHCODE = 1;

        protected static final int PRIME = 31;

        protected int hashCode = DEFAULT_HASHCODE;

        public boolean hashCodeDirty = true;

        public String loopKey;

        public int customer_id;

        public String city;

        public String country;

        public String GID;

        public Integer GRP_SIZE;

        public Boolean MASTER;

        public Double SCORE;

        public Double GRP_QUALITY;

        public String MERGE_INFO;

        public String MATCHING_DISTANCES;

        public Object ORIGINAL_RECORD;

        public Object getORIGINAL_RECORD() {
            return this.ORIGINAL_RECORD;
        }

        @Override
        public int hashCode() {
            if (this.hashCodeDirty) {
                final int prime = PRIME;
                int result = DEFAULT_HASHCODE;

                result = prime * result + this.customer_id;

                this.hashCode = result;
                this.hashCodeDirty = false;
            }
            return this.hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final row2Struct other = (row2Struct) obj;

            if (this.customer_id != other.customer_id) {
                return false;
            }

            return true;
        }

        public void copyDataTo(row2Struct other) {

            other.customer_id = this.customer_id;
            other.city = this.city;
            other.country = this.country;
            other.GID = this.GID;
            other.GRP_SIZE = this.GRP_SIZE;
            other.MASTER = this.MASTER;
            other.SCORE = this.SCORE;
            other.GRP_QUALITY = this.GRP_QUALITY;
            other.ORIGINAL_RECORD = this.ORIGINAL_RECORD;
        }

        public void copyKeysDataTo(row2Struct other) {
            other.customer_id = this.customer_id;
        }

        @Override
        public int compareTo(row2Struct row2) {
            if (!(this.GID).equals(row2.GID)) {
                return (this.GID).compareTo(row2.GID);
            } else {
                // false < true
                return (row2.MASTER).compareTo(this.MASTER);
            }
        }
    }

    /**
     * after the first tmatchgroup, there is an column added on its output. To contain the original values(List<Attribute> attributes from Record). 
     */
    @Test
    public void testSwooshIntMatchGroup_passOriginal_1st()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "city", "1", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

                if (0 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = row[1] == null ? null : String.valueOf((String) row[1]);
                }

                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = row[2] == null ? null : String.valueOf((String) row[2]);
                }

                if (3 < row.length) {
                    outStuct_tMatchGroup_1.GID = row[3] == null ? null : String.valueOf((String) row[3]);
                }

                if (4 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[4]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = row[4] == null ? null : 0;
                    }
                }

                if (5 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = row[5] == null ? null : Boolean.valueOf((String) row[5]);
                }

                if (6 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[6]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = 0.0;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = 0.0;
                    }
                }

                if (8 < row.length) {
                    outStuct_tMatchGroup_1.ORIGINAL_RECORD = row[8] == null ? null : row[8];
                }

                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", null);
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", null);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((ComponentSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        //pass the original values
        //        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);
        recordGroupImp_tMatchGroup_1.setIsPassOriginalValue(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_pass_original.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        List<String[]> inputList2 = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            if (StringUtils.equals("X", fields[1])) {
                inputList.add(fields);
            } else {
                inputList2.add(fields);
            }
        }

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while
        recordGroupImp_tMatchGroup_1.end();

        for (String[] inputRow : inputList2) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while
        recordGroupImp_tMatchGroup_1.end();

        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);

        //assert
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER + "--" + one.ORIGINAL_RECORD);

            if (one.MASTER) {

                Assert.assertTrue(one.ORIGINAL_RECORD instanceof List);
                Assert.assertTrue(((List) one.ORIGINAL_RECORD).size() > 0);

            }
        }

    }

    /**
     * Input of the 2nd tmatchgroup contains List<Attribute>, need to be handled. And should not be outputed.
     */
    @Test
    public void testSwooshIntMatchGroup_passOriginal_2nd()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "city", "1", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

                if (0 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = row[1] == null ? null : String.valueOf((String) row[1]);
                }

                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = row[2] == null ? null : String.valueOf((String) row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.ORIGINAL_RECORD = row[3] == null ? null : row[3];
                }
                if (4 < row.length) {
                    outStuct_tMatchGroup_1.GID = row[4] == null ? null : String.valueOf((String) row[4]);
                }

                if (5 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[5]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = row[5] == null ? null : 0;
                    }
                }

                if (6 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = row[6] == null ? null : Boolean.valueOf((String) row[6]);
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = 0.0;
                    }
                }

                if (8 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[8]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = 0.0;
                    }
                }

                if (9 < row.length) {
                    outStuct_tMatchGroup_1.MERGE_INFO = row[9] == null ? null : String.valueOf(row[9]);
                }

                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();

        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((ComponentSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);
        //the "ORIGINAL_RECORD" is considered as an input column. 
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(4);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_passOriginal_on2nd.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        List<Object[]> inputList2 = new ArrayList<Object[]>();
        List<Object> list2 = new ArrayList<Object>();
        for (String line : listOfLines) {
            Object[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            Collections.addAll(list2, fields);
            List<Attribute> list = new ArrayList<Attribute>();
            if (StringUtils.equalsIgnoreCase("true", (String) fields[5])) { //add the list into masters
                Attribute att2 = new Attribute("city", 1);
                att2.setValue("AA");
                att2.getValues().get("A").increment();
                att2.getValues().get("A").increment();
                list.add(att2);

                //                Iterator<String> leftValues = new IteratorChain(Collections.singleton("A").iterator(),
                //                        att.getValues().iterator());
                //                while (leftValues.hasNext()) {
                //                    String leftValue = leftValues.next();
                //                    System.err.println(leftValue);
                //                }
            }
            list2.add(list);
            //add "Merge_info" at the end of the array
            list2.add((String) fields[5]);

            inputList2.add(list2.toArray());
            list2.clear();
        }

        for (Object[] inputRow : inputList2) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        }
        recordGroupImp_tMatchGroup_1.end();

        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);

        //assert
        System.err.println("--pass original---2---" + groupRows_tMatchGroup_1.size());
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER + "--");
            Assert.assertEquals("", one.ORIGINAL_RECORD);
            if (one.MASTER) {

                Assert.assertEquals("should be: AAA", "AAA", one.city);
                Assert.assertEquals("", one.ORIGINAL_RECORD);
            }
        }

    }

    /**
     * Input of the 2nd tmatchgroup contains List<Attribute>, need to be handled. And should not be outputed.
     */
    @Test
    public void testSwooshIntMatchGroup_passOriginal_withOutputDetails()
            throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<List<Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> matcherList_tMatchGroup_1 = null;
        Map<String, String> tmpMap_tMatchGroup_1 = null;
        List<Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        matcherList_tMatchGroup_1 = new ArrayList<Map<String, String>>();
        tmpMap_tMatchGroup_1 = createTmpMap("CONCATENATE", "1", "", "city", "1", "Exact", "NO", 1 + "", "nullMatchNull",
                0.85 + "", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        // master rows in a group
        final List<row2Struct> masterRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // all rows in a group
        final List<row2Struct> groupRows_tMatchGroup_1 = new ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final Map<String, Integer> indexMap_tMatchGroup_1 = new HashMap<String, Integer>();

        AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1 = new ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

                if (0 < row.length) {
                    try {
                        outStuct_tMatchGroup_1.customer_id = Integer.valueOf((String) row[0]);
                    } catch (NumberFormatException e) {
                        outStuct_tMatchGroup_1.customer_id = 0;
                    }
                }

                if (1 < row.length) {
                    outStuct_tMatchGroup_1.city = row[1] == null ? null : String.valueOf((String) row[1]);
                }

                if (2 < row.length) {
                    outStuct_tMatchGroup_1.country = row[2] == null ? null : String.valueOf((String) row[2]);
                }
                if (3 < row.length) {
                    outStuct_tMatchGroup_1.ORIGINAL_RECORD = row[3] == null ? null : row[3];
                }
                if (4 < row.length) {
                    outStuct_tMatchGroup_1.GID = row[4] == null ? null : String.valueOf((String) row[4]);
                }

                if (5 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_SIZE = Integer.valueOf((String) row[5]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_SIZE = row[5] == null ? null : 0;
                    }
                }

                if (6 < row.length) {
                    outStuct_tMatchGroup_1.MASTER = row[6] == null ? null : Boolean.valueOf((String) row[6]);
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.SCORE = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.SCORE = 0.0;
                    }
                }

                if (8 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[8]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = 0.0;
                    }
                }
                if (9 < row.length) {
                    outStuct_tMatchGroup_1.MATCHING_DISTANCES = row[9] == null ? null : String.valueOf(row[9]);
                }
                if (10 < row.length) {
                    outStuct_tMatchGroup_1.MERGE_INFO = row[10] == null ? null : String.valueOf(row[10]);
                }

                if (outStuct_tMatchGroup_1.MASTER == true) {
                    masterRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                    indexMap_tMatchGroup_1.put(String.valueOf(outStuct_tMatchGroup_1.GID), masterRows_tMatchGroup_1.size() - 1);
                } else {
                    groupRows_tMatchGroup_1.add(outStuct_tMatchGroup_1);
                }
            }

            @Override
            protected boolean isMaster(Object col) {
                return String.valueOf(col).equals("true");
            }
        };

        recordGroupImp_tMatchGroup_1.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (List<Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();

        // init the parameters of the tswoosh algorithm
        Map<String, String> columnWithType_tMatchGroup_1 = fillColumn("id_Integer", "id_String", "id_String", "id_String",
                "id_Integer", "id_Boolean", "id_Double", "id_Double", "id_String");
        columnWithType_tMatchGroup_1.put("MATCHING_DISTANCES", "id_String");
        Map<String, String> columnWithIndex_tMatchGroup_1 = fillColumn("0", "1", "2", "3", "4", "5", "6", "7", "9");
        columnWithIndex_tMatchGroup_1.put("MATCHING_DISTANCES", "8");

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = SurvivorshipUtils
                .createSurvivorShipAlgorithmParams((AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((ComponentSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        initialize(recordGroupImp_tMatchGroup_1);
        // use multipass
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(true);
        //the "ORIGINAL_RECORD" is considered as an input column. 
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(4);
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(true);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(true);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("swoosh_passOriginal_withoutput.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        List<Object[]> inputList2 = new ArrayList<Object[]>();
        List<Object> list2 = new ArrayList<Object>();
        for (String line : listOfLines) {
            Object[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            Collections.addAll(list2, fields);
            List<Attribute> list = new ArrayList<Attribute>();
            if (StringUtils.equalsIgnoreCase("true", (String) fields[5])) { //add the list into masters
                Attribute att2 = new Attribute("city", 1);
                att2.setValue("AA");
                att2.getValues().get("A").increment();
                att2.getValues().get("A").increment();
                list.add(att2);
            }
            list2.add(list);
            //add "Merge_info" at the end of the array
            list2.add((String) fields[5]);

            inputList2.add(list2.toArray());
            list2.clear();
        }

        for (Object[] inputRow : inputList2) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        }
        recordGroupImp_tMatchGroup_1.end();

        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        Collections.sort(groupRows_tMatchGroup_1);

        //assert
        System.err.println("--pass original---with output---" + groupRows_tMatchGroup_1.size());
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER + "--" + one.MATCHING_DISTANCES);

            if (one.MASTER) {

                Assert.assertEquals("should be: AAA", "AAA", one.city);
                Assert.assertEquals("", one.ORIGINAL_RECORD);
            }
        }

    }
}
