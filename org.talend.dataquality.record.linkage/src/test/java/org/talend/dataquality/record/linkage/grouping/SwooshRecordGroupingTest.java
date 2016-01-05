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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
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
                for (String c : row) {
                    System.out.print(c + ",");
                }
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
}
