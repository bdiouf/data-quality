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
package org.talend.dataquality.record.linkage.grouping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
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
        recordGroup = new AbstractRecordGrouping<String>() {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#isMaster(java.lang.Object)
             */
            @Override
            protected boolean isMaster(String col) {
                return "true".equals(col);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#createTYPEArray(int)
             */
            @Override
            protected String[] createTYPEArray(int size) {
                return new String[size];
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(java.lang.String)
             */
            @Override
            protected void outputRow(String[] row) {
                // for (String c : row) {
                // System.out.print(c + ",");
                // }
                System.out.println();
                groupingRecords.add(row);
            }

            /*
             * (non-Javadoc)
             * 
             * @see
             * org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(org.talend.dataquality
             * .record.linkage.grouping.swoosh.RichRecord)
             */
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

        Map<String, String> lnameRecords = new HashMap<String, String>();
        lnameRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(0));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_NAME, "ID");
        lnameRecords.put(IRecordGrouping.MATCHING_TYPE, "JARO_WINKLER"); //$NON-NLS-1$
        lnameRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(1));

        matchingRule.add(lnameRecords);

        recordGroup.addMatchRule(matchingRule);
        try {
            recordGroup.initialize();
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        } catch (ClassNotFoundException e) {
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
        recordGroup = new AbstractRecordGrouping<String>() {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#isMaster(java.lang.Object)
             */
            @Override
            protected boolean isMaster(String col) {
                return "true".equals(col);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#createTYPEArray(int)
             */
            @Override
            protected String[] createTYPEArray(int size) {
                return new String[size];
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(java.lang.String)
             */
            @Override
            protected void outputRow(String[] row) {
                for (String c : row) {
                    System.out.print(c + ",");
                }
                System.out.println();
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

        Map<String, String> lnameRecords = new HashMap<String, String>();
        lnameRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_NAME, "NAME");
        lnameRecords.put(IRecordGrouping.MATCHING_TYPE, "JARO_WINKLER"); //$NON-NLS-1$
        lnameRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(0.9));

        matchingRule.add(lnameRecords);

        recordGroup.addMatchRule(matchingRule);
        try {
            recordGroup.initialize();
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            Assert.fail();
        } catch (ClassNotFoundException e) {
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
    public void testSwooshIntMatchGroup() throws IOException, InterruptedException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "CONCATENATE");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "country");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final java.util.List<row2Struct> masterRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // all rows in a group
        final java.util.List<row2Struct> groupRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final java.util.Map<String, Integer> indexMap_tMatchGroup_1 = new java.util.HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

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
                // System.out.println("--->" + outStuct_tMatchGroup_1.GID + "--" + outStuct_tMatchGroup_1.country);
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

        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();

        columnWithType_tMatchGroup_1.put("customer_id", "id_Integer");
        columnWithType_tMatchGroup_1.put("city", "id_String");
        columnWithType_tMatchGroup_1.put("country", "id_String");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();
        columnWithIndex_tMatchGroup_1.put("customer_id", "0");
        columnWithIndex_tMatchGroup_1.put("city", "1");
        columnWithIndex_tMatchGroup_1.put("country", "2");
        columnWithIndex_tMatchGroup_1.put("GID", "3");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "4");
        columnWithIndex_tMatchGroup_1.put("MASTER", "5");
        columnWithIndex_tMatchGroup_1.put("SCORE", "6");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "7");

        org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType("true".equals("true") ? true : false);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("customers_swoosh_tmatch.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        // Long gid_tMatchGroup_1 = 0L;
        // boolean forceLoop_tMatchGroup_1 = true;
        // tHash_Lookup_row1.lookup(hashKey_row1);
        // masterRows_tMatchGroup_1.clear();
        // groupRows_tMatchGroup_1.clear();
        // indexMap_tMatchGroup_1.clear();

        // add mutch rules
        // for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
        // recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        // }
        // recordGroupImp_tMatchGroup_1.initialize();

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        java.util.Collections.sort(groupRows_tMatchGroup_1, new Comparator<row2Struct>() {

            @Override
            public int compare(row2Struct row1, row2Struct row2) {
                if (!(row1.GID).equals(row2.GID)) {
                    return (row1.GID).compareTo(row2.GID);
                } else {
                    // false < true
                    return (row2.MASTER).compareTo(row1.MASTER);
                }
            }
        });

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
    public void testSwooshMultipasstMatchGroup() throws IOException, InterruptedException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.Map<String, String> paramMapTmp_tMatchGroup_1 = null;
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "CONCATENATE");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "country");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;

        row2Struct masterRow_tMatchGroup_1 = null; // a master-row in a
                                                   // group
        row2Struct subRow_tMatchGroup_1 = null; // a sub-row in a group.
        // master rows in a group
        final java.util.List<row2Struct> masterRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // all rows in a group
        final java.util.List<row2Struct> groupRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final java.util.Map<String, Integer> indexMap_tMatchGroup_1 = new java.util.HashMap<String, Integer>();
        final double CONFIDENCE_THRESHOLD_tMatchGroup_1 = Double.valueOf(0.9);

        // TDQ-9172 reuse JAVA API at here.
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                // for (Object element : row) {
                // System.err.print("-" + element);
                // }
                // System.err.println(";");
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

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
                // System.out.println("--->" + outStuct_tMatchGroup_1.GID + "--" + outStuct_tMatchGroup_1.country);
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
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
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

        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();

        columnWithType_tMatchGroup_1.put("customer_id", "id_Integer");
        columnWithType_tMatchGroup_1.put("city", "id_String");
        columnWithType_tMatchGroup_1.put("country", "id_String");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        columnWithType_tMatchGroup_1.put("MERGE_INFO", "id_String");
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();
        columnWithIndex_tMatchGroup_1.put("customer_id", "0");
        columnWithIndex_tMatchGroup_1.put("city", "1");
        columnWithIndex_tMatchGroup_1.put("country", "2");
        columnWithIndex_tMatchGroup_1.put("GID", "3");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "4");
        columnWithIndex_tMatchGroup_1.put("MASTER", "5");
        columnWithIndex_tMatchGroup_1.put("SCORE", "6");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "7");
        columnWithIndex_tMatchGroup_1.put("MERGE_INFO", "8");

        org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType("true".equals("true") ? true : false);
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

        java.util.Collections.sort(groupRows_tMatchGroup_1, new Comparator<row2Struct>() {

            @Override
            public int compare(row2Struct row1, row2Struct row2) {
                if (!(row1.GID).equals(row2.GID)) {
                    return (row1.GID).compareTo(row2.GID);
                } else {
                    // false < true
                    return (row2.MASTER).compareTo(row1.MASTER);
                }
            }
        });

        // assert
        int n = 0;
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE
            // + "--" + one.MASTER);
            if (one.GRP_SIZE == 7) {
                Assert.assertTrue(StringUtils.equals("BB", one.country) || StringUtils.equals("FFF", one.country));
                Assert.assertTrue(StringUtils.equals("YY", one.city) || StringUtils.equals("G", one.city));
            }
            n++;
        }

    }

    @Test
    public void testSwooshMultipasstMatchGroup_3groups() throws IOException, InterruptedException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.Map<String, String> paramMapTmp_tMatchGroup_1 = null;
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "CONCATENATE");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "country");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;

        row2Struct masterRow_tMatchGroup_1 = null; // a master-row in a
                                                   // group
        row2Struct subRow_tMatchGroup_1 = null; // a sub-row in a group.
        // master rows in a group
        final java.util.List<row2Struct> masterRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // all rows in a group
        final java.util.List<row2Struct> groupRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final java.util.Map<String, Integer> indexMap_tMatchGroup_1 = new java.util.HashMap<String, Integer>();
        final double CONFIDENCE_THRESHOLD_tMatchGroup_1 = Double.valueOf(0.9);

        // TDQ-9172 reuse JAVA API at here.
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                // for (Object element : row) {
                // System.err.print("-" + element);
                // }
                // System.err.println(";");
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

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
                // System.out.println("--->" + outStuct_tMatchGroup_1.GID + "--" + outStuct_tMatchGroup_1.country);
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
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
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

        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();

        columnWithType_tMatchGroup_1.put("customer_id", "id_Integer");
        columnWithType_tMatchGroup_1.put("city", "id_String");
        columnWithType_tMatchGroup_1.put("country", "id_String");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        columnWithType_tMatchGroup_1.put("MERGE_INFO", "id_String");
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();
        columnWithIndex_tMatchGroup_1.put("customer_id", "0");
        columnWithIndex_tMatchGroup_1.put("city", "1");
        columnWithIndex_tMatchGroup_1.put("country", "2");
        columnWithIndex_tMatchGroup_1.put("GID", "3");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "4");
        columnWithIndex_tMatchGroup_1.put("MASTER", "5");
        columnWithIndex_tMatchGroup_1.put("SCORE", "6");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "7");
        columnWithIndex_tMatchGroup_1.put("MERGE_INFO", "8");

        org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType("true".equals("true") ? true : false);
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

        java.util.Collections.sort(groupRows_tMatchGroup_1, new Comparator<row2Struct>() {

            @Override
            public int compare(row2Struct row1, row2Struct row2) {
                if (!(row1.GID).equals(row2.GID)) {
                    return (row1.GID).compareTo(row2.GID);
                } else {
                    // false < true
                    return (row2.MASTER).compareTo(row1.MASTER);
                }
            }
        });

        // assert
        int n = 0;
        System.out.println("swoosh with multipass of 3 groups");
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER + "--" + one.MERGE_INFO);
            if (one.GRP_SIZE == 7) {
                Assert.assertTrue(StringUtils.equals("FFF", one.country) || StringUtils.equals("BB", one.country));
                Assert.assertTrue(StringUtils.equals("G", one.city) || StringUtils.equals("AAA", one.city));

            }
            n++;
        }
        System.out.println("");

    }

    @Test
    public void testSwooshMultipasstMatchGroup_withNoNewMasterIn2ndPass() throws IOException, InterruptedException,
            InstantiationException, IllegalAccessException, ClassNotFoundException {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.Map<String, String> paramMapTmp_tMatchGroup_1 = null;
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "CONCATENATE");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "country");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;

        row2Struct masterRow_tMatchGroup_1 = null; // a master-row in a
                                                   // group
        row2Struct subRow_tMatchGroup_1 = null; // a sub-row in a group.
        // master rows in a group
        final java.util.List<row2Struct> masterRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // all rows in a group
        final java.util.List<row2Struct> groupRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final java.util.Map<String, Integer> indexMap_tMatchGroup_1 = new java.util.HashMap<String, Integer>();
        final double CONFIDENCE_THRESHOLD_tMatchGroup_1 = Double.valueOf(0.9);

        // TDQ-9172 reuse JAVA API at here.
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                // for (Object element : row) {
                // System.err.print("-" + element);
                // }
                // System.err.println(";");
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

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
                // System.out.println("--->" + outStuct_tMatchGroup_1.GID + "--" + outStuct_tMatchGroup_1.country);
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
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
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

        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();

        columnWithType_tMatchGroup_1.put("customer_id", "id_Integer");
        columnWithType_tMatchGroup_1.put("city", "id_String");
        columnWithType_tMatchGroup_1.put("country", "id_String");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        columnWithType_tMatchGroup_1.put("MERGE_INFO", "id_String");
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();
        columnWithIndex_tMatchGroup_1.put("customer_id", "0");
        columnWithIndex_tMatchGroup_1.put("city", "1");
        columnWithIndex_tMatchGroup_1.put("country", "2");
        columnWithIndex_tMatchGroup_1.put("GID", "3");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "4");
        columnWithIndex_tMatchGroup_1.put("MASTER", "5");
        columnWithIndex_tMatchGroup_1.put("SCORE", "6");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "7");
        columnWithIndex_tMatchGroup_1.put("MERGE_INFO", "8");

        org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType("true".equals("true") ? true : false);
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

        java.util.Collections.sort(groupRows_tMatchGroup_1, new Comparator<row2Struct>() {

            @Override
            public int compare(row2Struct row1, row2Struct row2) {
                if (!(row1.GID).equals(row2.GID)) {
                    return (row1.GID).compareTo(row2.GID);
                } else {
                    // false < true
                    return (row2.MASTER).compareTo(row1.MASTER);
                }
            }
        });

        // assert
        int n = 0;
        System.out.println("swoosh with multipass without new masters in 2nd pass");
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER);
            if (one.GRP_SIZE == 2) {
                Assert.assertTrue(StringUtils.equals("B", one.country) || StringUtils.equals("F", one.country));
                Assert.assertTrue(StringUtils.equals("YY", one.city) || StringUtils.equals("WW", one.city));
            }
            n++;
        }
        System.out.println("");

    }

    class row2Struct {

        protected static final int DEFAULT_HASHCODE = 1;

        protected static final int PRIME = 31;

        protected int hashCode = DEFAULT_HASHCODE;

        public boolean hashCodeDirty = true;

        public String loopKey;

        public int customer_id;

        public int getCustomer_id() {
            return this.customer_id;
        }

        public String city;

        public String getCity() {
            return this.city;
        }

        public String country;

        public String getCountry() {
            return this.country;
        }

        public String GID;

        public String getGID() {
            return this.GID;
        }

        public Integer GRP_SIZE;

        public Integer getGRP_SIZE() {
            return this.GRP_SIZE;
        }

        public Boolean MASTER;

        public Boolean getMASTER() {
            return this.MASTER;
        }

        public Double SCORE;

        public Double getSCORE() {
            return this.SCORE;
        }

        public Double GRP_QUALITY;

        public Double getGRP_QUALITY() {
            return this.GRP_QUALITY;
        }

        public String MERGE_INFO;

        public String getMERGE_INFO() {
            return this.MERGE_INFO;
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

        }

        public void copyKeysDataTo(row2Struct other) {

            other.customer_id = this.customer_id;

        }

    }

    @Test
    public void testSwooshIntMatchGroup_withBlocks() throws IOException, InterruptedException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.Map<String, String> paramMapTmp_tMatchGroup_1 = null;
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "CONCATENATE");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "country");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;

        row2Struct masterRow_tMatchGroup_1 = null; // a master-row in a
                                                   // group
        row2Struct subRow_tMatchGroup_1 = null; // a sub-row in a group.
        // master rows in a group
        final java.util.List<row2Struct> masterRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // all rows in a group
        final java.util.List<row2Struct> groupRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final java.util.Map<String, Integer> indexMap_tMatchGroup_1 = new java.util.HashMap<String, Integer>();
        final double CONFIDENCE_THRESHOLD_tMatchGroup_1 = Double.valueOf(0.9);

        // TDQ-9172 reuse JAVA API at here.
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

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
                // System.out.println("--->" + outStuct_tMatchGroup_1.GID + "--" + outStuct_tMatchGroup_1.country);
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

        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();

        columnWithType_tMatchGroup_1.put("customer_id", "id_Integer");
        columnWithType_tMatchGroup_1.put("city", "id_String");
        columnWithType_tMatchGroup_1.put("country", "id_String");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();
        columnWithIndex_tMatchGroup_1.put("customer_id", "0");
        columnWithIndex_tMatchGroup_1.put("city", "1");
        columnWithIndex_tMatchGroup_1.put("country", "2");
        columnWithIndex_tMatchGroup_1.put("GID", "3");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "4");
        columnWithIndex_tMatchGroup_1.put("MASTER", "5");
        columnWithIndex_tMatchGroup_1.put("SCORE", "6");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "7");

        org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType(true);

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

        java.util.Collections.sort(groupRows_tMatchGroup_1, new Comparator<row2Struct>() {

            @Override
            public int compare(row2Struct row1, row2Struct row2) {
                if (!(row1.GID).equals(row2.GID)) {
                    return (row1.GID).compareTo(row2.GID);
                } else {
                    // false < true
                    return (row2.MASTER).compareTo(row1.MASTER);
                }
            }
        });

        // assert
        int n = 0;
        System.out.println("swoosh in tmatchgroup with blocks...");

        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER);
            // if (one.GRP_SIZE == 7) {
            // Assert.assertEquals("USAUSAUSAUSAUSAUSAUSA", one.country);
            //
            // for (int i = n + 1; i < n + 8; i++) {
            // Assert.assertEquals("USA", groupRows_tMatchGroup_1.get(i).country);
            // }
            // break;
            // }
            n++;
        }
        System.out.println("");

    }

    @Test
    public void testSwooshIntMatchGroup_withoutBlocks() throws IOException, InterruptedException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.Map<String, String> paramMapTmp_tMatchGroup_1 = null;
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "CONCATENATE");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "country");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;

        row2Struct masterRow_tMatchGroup_1 = null; // a master-row in a
                                                   // group
        row2Struct subRow_tMatchGroup_1 = null; // a sub-row in a group.
        // master rows in a group
        final java.util.List<row2Struct> masterRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // all rows in a group
        final java.util.List<row2Struct> groupRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final java.util.Map<String, Integer> indexMap_tMatchGroup_1 = new java.util.HashMap<String, Integer>();
        final double CONFIDENCE_THRESHOLD_tMatchGroup_1 = Double.valueOf(0.9);

        // TDQ-9172 reuse JAVA API at here.
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

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
                // System.out.println("--->" + outStuct_tMatchGroup_1.GID + "--" + outStuct_tMatchGroup_1.country);
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

        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();

        columnWithType_tMatchGroup_1.put("customer_id", "id_Integer");
        columnWithType_tMatchGroup_1.put("city", "id_String");
        columnWithType_tMatchGroup_1.put("country", "id_String");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();
        columnWithIndex_tMatchGroup_1.put("customer_id", "0");
        columnWithIndex_tMatchGroup_1.put("city", "1");
        columnWithIndex_tMatchGroup_1.put("country", "2");
        columnWithIndex_tMatchGroup_1.put("GID", "3");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "4");
        columnWithIndex_tMatchGroup_1.put("MASTER", "5");
        columnWithIndex_tMatchGroup_1.put("SCORE", "6");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "7");

        org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType("true".equals("true") ? true : false);

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

        java.util.Collections.sort(groupRows_tMatchGroup_1, new Comparator<row2Struct>() {

            @Override
            public int compare(row2Struct row1, row2Struct row2) {
                if (!(row1.GID).equals(row2.GID)) {
                    return (row1.GID).compareTo(row2.GID);
                } else {
                    // false < true
                    return (row2.MASTER).compareTo(row1.MASTER);
                }
            }
        });

        // assert
        int n = 0;
        System.out.println("swoosh in tmatchgroup without groups");
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER);
            // if (one.GRP_SIZE == 7) {
            // Assert.assertEquals("USAUSAUSAUSAUSAUSAUSA", one.country);
            //
            // for (int i = n + 1; i < n + 8; i++) {
            // Assert.assertEquals("USA", groupRows_tMatchGroup_1.get(i).country);
            // }
            // break;
            // }
            n++;
        }
        System.out.println("");

    }

    @Test
    public void testSetDefaultSurvivorshipRules() {
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;
        realSurShipMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "BOOLEAN");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);
        realSurShipMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "MostCommon");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "STRING");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);
        realSurShipMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        realSurShipMap_tMatchGroup_1.put("PARAMETER", "");
        realSurShipMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "MostRecent");
        realSurShipMap_tMatchGroup_1.put("DATA_TYPE", "NUMBER");
        defaultSurvivorshipRules_tMatchGroup_1.add(realSurShipMap_tMatchGroup_1);

        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.Map<String, String> paramMapTmp_tMatchGroup_1 = null;
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "MostCommon");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1.0");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "stu_Address");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();

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
        analysisSwooshMatchRecordGrouping
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            analysisSwooshMatchRecordGrouping.addMatchRule(matcherList);
        }
        try {
            analysisSwooshMatchRecordGrouping.initialize();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Assert.fail("initial failed :" + e.getMessage());
        }
        SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
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
    public void testSwooshMultipasstMatchGroup_oneRecord() throws IOException, InterruptedException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.Map<String, String> paramMapTmp_tMatchGroup_1 = null;
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "CONCATENATE");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "country");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;

        row2Struct masterRow_tMatchGroup_1 = null; // a master-row in a
                                                   // group
        row2Struct subRow_tMatchGroup_1 = null; // a sub-row in a group.
        // master rows in a group
        final java.util.List<row2Struct> masterRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // all rows in a group
        final java.util.List<row2Struct> groupRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final java.util.Map<String, Integer> indexMap_tMatchGroup_1 = new java.util.HashMap<String, Integer>();
        final double CONFIDENCE_THRESHOLD_tMatchGroup_1 = Double.valueOf(0.9);

        // TDQ-9172 reuse JAVA API at here.
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                // for (Object element : row) {
                // System.err.print("-" + element);
                // }
                // System.err.println(";");
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

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
                // System.out.println("--->" + outStuct_tMatchGroup_1.GID + "--" + outStuct_tMatchGroup_1.country);
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
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
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

        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();

        columnWithType_tMatchGroup_1.put("customer_id", "id_Integer");
        columnWithType_tMatchGroup_1.put("city", "id_String");
        columnWithType_tMatchGroup_1.put("country", "id_String");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        columnWithType_tMatchGroup_1.put("MERGE_INFO", "id_String");
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();
        columnWithIndex_tMatchGroup_1.put("customer_id", "0");
        columnWithIndex_tMatchGroup_1.put("city", "1");
        columnWithIndex_tMatchGroup_1.put("country", "2");
        columnWithIndex_tMatchGroup_1.put("GID", "3");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "4");
        columnWithIndex_tMatchGroup_1.put("MASTER", "5");
        columnWithIndex_tMatchGroup_1.put("SCORE", "6");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "7");
        columnWithIndex_tMatchGroup_1.put("MERGE_INFO", "8");

        org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType("true".equals("true") ? true : false);
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

        java.util.Collections.sort(groupRows_tMatchGroup_1, new Comparator<row2Struct>() {

            @Override
            public int compare(row2Struct row1, row2Struct row2) {
                if (!(row1.GID).equals(row2.GID)) {
                    return (row1.GID).compareTo(row2.GID);
                } else {
                    // false < true
                    return (row2.MASTER).compareTo(row1.MASTER);
                }
            }
        });

        // assert
        int n = 0;
        System.out.println("swoosh with multipass :only one record");
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER);
            Assert.assertTrue(one.GRP_SIZE == 1);
            Assert.assertTrue(StringUtils.isNotBlank(one.GID));
        }
        System.out.println("");

    }

    @Test
    public void testSwooshMultipasstMatchGroup_differentRecord() throws IOException, InterruptedException,
            InstantiationException, IllegalAccessException, ClassNotFoundException {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.Map<String, String> paramMapTmp_tMatchGroup_1 = null;
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "CONCATENATE");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "country");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;

        row2Struct masterRow_tMatchGroup_1 = null; // a master-row in a
                                                   // group
        row2Struct subRow_tMatchGroup_1 = null; // a sub-row in a group.
        // master rows in a group
        final java.util.List<row2Struct> masterRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // all rows in a group
        final java.util.List<row2Struct> groupRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final java.util.Map<String, Integer> indexMap_tMatchGroup_1 = new java.util.HashMap<String, Integer>();
        final double CONFIDENCE_THRESHOLD_tMatchGroup_1 = Double.valueOf(0.9);

        // TDQ-9172 reuse JAVA API at here.
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                // for (Object element : row) {
                // System.err.print("-" + element);
                // }
                // System.err.println(";");
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

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
                // System.out.println("--->" + outStuct_tMatchGroup_1.GID + "--" + outStuct_tMatchGroup_1.country);
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
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
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

        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();

        columnWithType_tMatchGroup_1.put("customer_id", "id_Integer");
        columnWithType_tMatchGroup_1.put("city", "id_String");
        columnWithType_tMatchGroup_1.put("country", "id_String");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        columnWithType_tMatchGroup_1.put("MERGE_INFO", "id_String");
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();
        columnWithIndex_tMatchGroup_1.put("customer_id", "0");
        columnWithIndex_tMatchGroup_1.put("city", "1");
        columnWithIndex_tMatchGroup_1.put("country", "2");
        columnWithIndex_tMatchGroup_1.put("GID", "3");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "4");
        columnWithIndex_tMatchGroup_1.put("MASTER", "5");
        columnWithIndex_tMatchGroup_1.put("SCORE", "6");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "7");
        columnWithIndex_tMatchGroup_1.put("MERGE_INFO", "8");

        org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType("true".equals("true") ? true : false);
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

        java.util.Collections.sort(groupRows_tMatchGroup_1, new Comparator<row2Struct>() {

            @Override
            public int compare(row2Struct row1, row2Struct row2) {
                if (!(row1.GID).equals(row2.GID)) {
                    return (row1.GID).compareTo(row2.GID);
                } else {
                    // false < true
                    return (row2.MASTER).compareTo(row1.MASTER);
                }
            }
        });

        // assert
        int n = 0;
        System.out.println("swoosh with multipass :lost some record");
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_SIZE
                    + "--" + one.MASTER);
        }
        Assert.assertTrue(groupRows_tMatchGroup_1.get(0).GRP_SIZE == 12);
        System.out.println("");

    }

    @Test
    public void testSwooshMultipasstMatchGroup_score() throws IOException, InterruptedException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.Map<String, String> paramMapTmp_tMatchGroup_1 = null;
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "CONCATENATE");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "0.86");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "country");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Jaro");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        java.util.Map<String, String> realSurShipMap_tMatchGroup_1 = null;

        row2Struct masterRow_tMatchGroup_1 = null; // a master-row in a
                                                   // group
        row2Struct subRow_tMatchGroup_1 = null; // a sub-row in a group.
        // master rows in a group
        final java.util.List<row2Struct> masterRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // all rows in a group
        final java.util.List<row2Struct> groupRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final java.util.Map<String, Integer> indexMap_tMatchGroup_1 = new java.util.HashMap<String, Integer>();
        final double CONFIDENCE_THRESHOLD_tMatchGroup_1 = Double.valueOf(0.9);

        // TDQ-9172 reuse JAVA API at here.
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                // for (Object element : row) {
                // System.err.print("-" + element);
                // }
                // System.err.println(";");
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

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
                // System.out.println("--->" + outStuct_tMatchGroup_1.GID + "--" + outStuct_tMatchGroup_1.country);
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
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
                    }
                }

                if (7 < row.length) {

                    try {
                        outStuct_tMatchGroup_1.GRP_QUALITY = Double.valueOf((String) row[7]);
                    } catch (java.lang.NumberFormatException e) {
                        outStuct_tMatchGroup_1.GRP_QUALITY = null;
                    } catch (java.lang.NullPointerException e) {
                        outStuct_tMatchGroup_1.SCORE = null;
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

        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();

        columnWithType_tMatchGroup_1.put("customer_id", "id_Integer");
        columnWithType_tMatchGroup_1.put("city", "id_String");
        columnWithType_tMatchGroup_1.put("country", "id_String");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        columnWithType_tMatchGroup_1.put("MERGE_INFO", "id_String");
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();
        columnWithIndex_tMatchGroup_1.put("customer_id", "0");
        columnWithIndex_tMatchGroup_1.put("city", "1");
        columnWithIndex_tMatchGroup_1.put("country", "2");
        columnWithIndex_tMatchGroup_1.put("GID", "3");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "4");
        columnWithIndex_tMatchGroup_1.put("MASTER", "5");
        columnWithIndex_tMatchGroup_1.put("SCORE", "6");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "7");
        columnWithIndex_tMatchGroup_1.put("MERGE_INFO", "8");

        org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType("true".equals("true") ? true : false);
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

        java.util.Collections.sort(groupRows_tMatchGroup_1, new Comparator<row2Struct>() {

            @Override
            public int compare(row2Struct row1, row2Struct row2) {
                if (!(row1.GID).equals(row2.GID)) {
                    return (row1.GID).compareTo(row2.GID);
                } else {
                    // false < true
                    return (row2.MASTER).compareTo(row1.MASTER);
                }
            }
        });

        // assert
        int n = 0;
        System.out.println("swoosh with multipass :score not correct");
        for (row2Struct one : groupRows_tMatchGroup_1) {
            System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" + one.GRP_QUALITY
                    + "--" + one.MASTER + "--" + one.SCORE);
            // Assert.assertTrue(one.SCORE <= 1);
            Assert.assertTrue(one.SCORE > 0);
        }
        System.out.println("");

    }

    @Test
    public void testSwooshIntMatchGroup_multipleRules() throws IOException, InterruptedException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        java.util.List<java.util.List<java.util.Map<String, String>>> matchingRulesAll_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();
        java.util.List<java.util.Map<String, String>> matcherList_tMatchGroup_1 = null;
        java.util.Map<String, String> tmpMap_tMatchGroup_1 = null;
        java.util.List<java.util.Map<String, String>> defaultSurvivorshipRules_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchAll");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1.0");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "stuAddress");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.86 + "");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 0 + "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1.0");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "dummy");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "city");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "1");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);
        matcherList_tMatchGroup_1 = new java.util.ArrayList<java.util.Map<String, String>>();
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchAll");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 0 + "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1.0");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "dummy");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "stuAddress");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "2");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        tmpMap_tMatchGroup_1 = new java.util.HashMap<String, String>();
        tmpMap_tMatchGroup_1.put("SURVIVORSHIP_FUNCTION", "Concatenate");
        tmpMap_tMatchGroup_1.put("HANDLE_NULL", "nullMatchNull");
        tmpMap_tMatchGroup_1.put("CONFIDENCE_WEIGHT", 1 + "");
        tmpMap_tMatchGroup_1.put("MATCHING_TYPE", "Exact");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_THRESHOLD", "1.0");
        tmpMap_tMatchGroup_1.put("ATTRIBUTE_NAME", "city");
        tmpMap_tMatchGroup_1.put("COLUMN_IDX", "1");
        tmpMap_tMatchGroup_1.put("PARAMETER", "");
        tmpMap_tMatchGroup_1.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");
        tmpMap_tMatchGroup_1.put("RECORD_MATCH_THRESHOLD", 0.85 + "");
        matcherList_tMatchGroup_1.add(tmpMap_tMatchGroup_1);
        matchingRulesAll_tMatchGroup_1.add(matcherList_tMatchGroup_1);

        // master rows in a group
        final java.util.List<row2Struct> masterRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // all rows in a group
        final java.util.List<row2Struct> groupRows_tMatchGroup_1 = new java.util.ArrayList<row2Struct>();
        // this Map key is MASTER GID,value is this MASTER index of all
        // MASTERS.it will be used to get DUPLICATE GRP_QUALITY from
        // MASTER and only in case of separate output.
        final java.util.Map<String, Integer> indexMap_tMatchGroup_1 = new java.util.HashMap<String, Integer>();

        // TDQ-9172 reuse JAVA API at here.
        org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping<Object> recordGroupImp_tMatchGroup_1;
        recordGroupImp_tMatchGroup_1 = new org.talend.dataquality.record.linkage.grouping.swoosh.ComponentSwooshMatchRecordGrouping() {

            @Override
            protected void outputRow(Object[] row) {
                row2Struct outStuct_tMatchGroup_1 = new row2Struct();
                boolean isMaster = false;

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
                // System.out.println("--->" + outStuct_tMatchGroup_1.GID + "--" + outStuct_tMatchGroup_1.country);
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

        recordGroupImp_tMatchGroup_1
                .setRecordLinkAlgorithm(org.talend.dataquality.record.linkage.constant.RecordMatcherType.T_SwooshAlgorithm);
        // add mutch rules
        for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
            recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        }
        recordGroupImp_tMatchGroup_1.initialize();
        // init the parameters of the tswoosh algorithm
        java.util.Map<String, String> columnWithType_tMatchGroup_1 = new java.util.HashMap<String, String>();
        java.util.List<java.util.List<java.util.Map<String, String>>> allRules_tMatchGroup_1 = new java.util.ArrayList<java.util.List<java.util.Map<String, String>>>();

        columnWithType_tMatchGroup_1.put("customer_id", "id_Integer");
        columnWithType_tMatchGroup_1.put("city", "id_String");
        columnWithType_tMatchGroup_1.put("country", "id_String");
        columnWithType_tMatchGroup_1.put("GID", "id_String");
        columnWithType_tMatchGroup_1.put("GRP_SIZE", "id_Integer");
        columnWithType_tMatchGroup_1.put("MASTER", "id_Boolean");
        columnWithType_tMatchGroup_1.put("SCORE", "id_Double");
        columnWithType_tMatchGroup_1.put("GRP_QUALITY", "id_Double");
        java.util.Map<String, String> columnWithIndex_tMatchGroup_1 = new java.util.HashMap<String, String>();
        columnWithIndex_tMatchGroup_1.put("customer_id", "0");
        columnWithIndex_tMatchGroup_1.put("city", "1");
        columnWithIndex_tMatchGroup_1.put("country", "2");
        columnWithIndex_tMatchGroup_1.put("GID", "3");
        columnWithIndex_tMatchGroup_1.put("GRP_SIZE", "4");
        columnWithIndex_tMatchGroup_1.put("MASTER", "5");
        columnWithIndex_tMatchGroup_1.put("SCORE", "6");
        columnWithIndex_tMatchGroup_1.put("GRP_QUALITY", "7");

        org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams survivorShipAlgorithmParams_tMatchGroup_1 = org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorshipUtils
                .createSurvivorShipAlgorithmParams(
                        (org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1,
                        matchingRulesAll_tMatchGroup_1, defaultSurvivorshipRules_tMatchGroup_1, columnWithType_tMatchGroup_1,
                        columnWithIndex_tMatchGroup_1);
        ((org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping) recordGroupImp_tMatchGroup_1)
                .setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams_tMatchGroup_1);
        recordGroupImp_tMatchGroup_1.setColumnDelimiter(";");
        recordGroupImp_tMatchGroup_1.setIsOutputDistDetails(false);
        recordGroupImp_tMatchGroup_1.setIsComputeGrpQuality(true);
        recordGroupImp_tMatchGroup_1.setAcceptableThreshold(Float.valueOf(0.85 + ""));
        recordGroupImp_tMatchGroup_1.setIsLinkToPrevious(false);
        recordGroupImp_tMatchGroup_1.setOrginalInputColumnSize(3);
        recordGroupImp_tMatchGroup_1.setIsDisplayAttLabels(false);
        recordGroupImp_tMatchGroup_1.setIsGIDStringType("true".equals("true") ? true : false);

        // read the data from the file
        InputStream in = this.getClass().getResourceAsStream("customers_swoosh_tmatch.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        inputList = new ArrayList<String[]>();
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            inputList.add(fields);
        }

        // Long gid_tMatchGroup_1 = 0L;
        // boolean forceLoop_tMatchGroup_1 = true;
        // tHash_Lookup_row1.lookup(hashKey_row1);
        // masterRows_tMatchGroup_1.clear();
        // groupRows_tMatchGroup_1.clear();
        // indexMap_tMatchGroup_1.clear();

        // add mutch rules
        // for (java.util.List<java.util.Map<String, String>> matcherList : matchingRulesAll_tMatchGroup_1) {
        // recordGroupImp_tMatchGroup_1.addMatchRule(matcherList);
        // }
        // recordGroupImp_tMatchGroup_1.initialize();

        for (String[] inputRow : inputList) { // loop on each data
            recordGroupImp_tMatchGroup_1.doGroup(inputRow);
        } // while

        recordGroupImp_tMatchGroup_1.end();
        groupRows_tMatchGroup_1.addAll(masterRows_tMatchGroup_1);

        java.util.Collections.sort(groupRows_tMatchGroup_1, new Comparator<row2Struct>() {

            @Override
            public int compare(row2Struct row1, row2Struct row2) {
                if (!(row1.GID).equals(row2.GID)) {
                    return (row1.GID).compareTo(row2.GID);
                } else {
                    // false < true
                    return (row2.MASTER).compareTo(row1.MASTER);
                }
            }
        });

        // assert
        boolean ismerged = false;
        for (row2Struct one : groupRows_tMatchGroup_1) {
            // System.out.println(one.customer_id + "--" + one.city + "--" + one.country + "--" + one.GID + "--" +
            // one.GRP_SIZE
            // + "--" + one.MASTER + "--" + one.SCORE);
            if (one.GRP_SIZE == 20) {
                ismerged = true;
            }
        }
        Assert.assertTrue(ismerged);

    }
}
