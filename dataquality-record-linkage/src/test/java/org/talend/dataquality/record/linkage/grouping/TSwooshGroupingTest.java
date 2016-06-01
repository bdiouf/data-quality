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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.matchmerge.mfb.MFBAttributeMatcher;
import org.talend.dataquality.matchmerge.mfb.MFBRecordMatcher;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.LevenshteinMatcher;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * normal result should like this
 * 
 * <pre>
.-----+-----+------------+------------------------------------+--------+------+-----+-----------+------------------+----------.
|                                                          tLogRow_1                                                          |
|=----+-----+------------+------------------------------------+--------+------+-----+-----------+------------------+---------=|
|id_pk|id   |name        |GID                                 |GRP_SIZE|MASTER|SCORE|GRP_QUALITY|MATCHING_DISTANCES|MERGE_INFO|
|=----+-----+------------+------------------------------------+--------+------+-----+-----------+------------------+---------=|
|12   |12345|singlerecord|f3f3dbbe-8b45-490e-a154-9ac1ef5ec6a6|1       |true  |1.0  |1.0        |                  |true      |
'-----+-----+------------+------------------------------------+--------+------+-----+-----------+------------------+----------'

.-----+--+----+------------------------------------+--------+------+-----+-----------+------------------+----------.
|                                                    tLogRow_2                                                     |
|=----+--+----+------------------------------------+--------+------+-----+-----------+------------------+---------=|
|id_pk|id|name|GID                                 |GRP_SIZE|MASTER|SCORE|GRP_QUALITY|MATCHING_DISTANCES|MERGE_INFO|
|=----+--+----+------------------------------------+--------+------+-----+-----------+------------------+---------=|
|6    |33|null|650ce205-16cf-4c7a-9272-b89587929ca8|2       |true  |1.0  |1.0        |                  |true      |
|5    |3 |null|650ce205-16cf-4c7a-9272-b89587929ca8|0       |false |1.0  |0.0        |id: 1.0           |false     |
|6    |3 |null|650ce205-16cf-4c7a-9272-b89587929ca8|0       |false |1.0  |0.0        |id: 1.0           |false     |
'-----+--+----+------------------------------------+--------+------+-----+-----------+------------------+----------'

.-----+-----------+----------+------------------------------------+--------+------+------------------+------------------+------------------------+----------.
|                                                                         tLogRow_3                                                                         |
|=----+-----------+----------+------------------------------------+--------+------+------------------+------------------+------------------------+---------=|
|id_pk|id         |name      |GID                                 |GRP_SIZE|MASTER|SCORE             |GRP_QUALITY       |MATCHING_DISTANCES      |MERGE_INFO|
|=----+-----------+----------+------------------------------------+--------+------+------------------+------------------+------------------------+---------=|
|2    |111        |lilis     |10335353-cddb-4a97-98d6-fdc828caa064|5       |true  |1.0               |0.6666666666666667|                        |true      |
|4    |2          |lis       |10335353-cddb-4a97-98d6-fdc828caa064|0       |false |0.6666666666666667|0.0               |name: 0.6666666666666667|true      |
|2    |111        |li        |10335353-cddb-4a97-98d6-fdc828caa064|0       |false |0.6666666666666667|0.0               |name: 0.6666666666666667|true      |
|1    |1          |li        |10335353-cddb-4a97-98d6-fdc828caa064|0       |false |1.0               |0.0               |id: 1.0                 |false     |
|2    |1          |wang      |10335353-cddb-4a97-98d6-fdc828caa064|0       |false |1.0               |0.0               |id: 1.0                 |false     |
|3    |1          |zhang     |10335353-cddb-4a97-98d6-fdc828caa064|0       |false |1.0               |0.0               |id: 1.0                 |false     |
|8    |13         |zhaoszhao |229e5a16-0599-4b51-919f-ae38ffebbb60|2       |true  |1.0               |0.8               |                        |true      |
|7    |12         |zhao      |229e5a16-0599-4b51-919f-ae38ffebbb60|0       |false |0.8               |0.0               |name: 0.8               |true      |
|8    |13         |zhaos     |229e5a16-0599-4b51-919f-ae38ffebbb60|0       |false |0.8               |0.0               |name: 0.8               |true      |
|10   |nihaosnihao|hellohello|b4a6cad4-bd57-4a5f-acef-dd1215c1ac37|4       |true  |1.0               |0.8333333333333334|                        |true      |
|11   |16         |hello     |b4a6cad4-bd57-4a5f-acef-dd1215c1ac37|0       |false |1.0               |0.0               |name: 1.0               |true      |
|10   |nihaosnihao|hello     |b4a6cad4-bd57-4a5f-acef-dd1215c1ac37|0       |false |1.0               |0.0               |name: 1.0               |true      |
|9    |nihao      |gogogo    |b4a6cad4-bd57-4a5f-acef-dd1215c1ac37|0       |false |0.8333333333333334|0.0               |id: 0.8333333333333334  |false     |
|10   |nihaos     |hello     |b4a6cad4-bd57-4a5f-acef-dd1215c1ac37|0       |false |0.8333333333333334|0.0               |id: 0.8333333333333334  |false     |
'-----+-----------+----------+------------------------------------+--------+------+------------------+------------------+------------------------+----------'
 * </pre>
 */
public class TSwooshGroupingTest {

    public List<RichRecord> result = new ArrayList<>();

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.grouping.TSwooshGrouping#swooshMatchWithMultipass(org.talend.dataquality.record.linkage.record.CombinedRecordMatcher, org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams, int)}
     * .
     */
    @Test
    public void testSwooshMatchWithMultipass() {
        // init data
        List<String[]> inputDataList = new ArrayList<>();
        inputDataList.add(new String[] { "10", "nihaosnihao", "hello", "0ca5ee30-6377-4995-8f93-7f167bc5e16e", "2", "true", "1.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                "0.8333333333333334", "", "true" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        inputDataList.add(new String[] { "9", "nihao", "gogogo", "0ca5ee30-6377-4995-8f93-7f167bc5e16e", "0", "false", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                "0.8333333333333334", "0.0", "id:0.8333333333333334", "false" });//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        inputDataList.add(new String[] { "10", "nihaos", "hello", "0ca5ee30-6377-4995-8f93-7f167bc5e16e", "0", "false", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                "0.8333333333333334", "0.0", "id:0.8333333333333334", "false" });//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        inputDataList
                .add(new String[] { "12", "12345", "singlerecord", "3f1839cf-bf43-449e-9003-84747cc92a84", "1", "true", "1.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                        "1.0", "", "true" });//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        inputDataList
                .add(new String[] { "11", "16", "hello", "4149c23f-046d-4a2b-a04d-354e5fbf6afc", "1", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                        "true" });//$NON-NLS-1$
        inputDataList.add(new String[] { "6", "33", null, "66cf5b44-f290-46e9-ab88-3c0779d9fc7b", "2", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "true" });//$NON-NLS-1$
        inputDataList.add(new String[] { "5", "3", null, "66cf5b44-f290-46e9-ab88-3c0779d9fc7b", "0", "false", "1.0", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                "id:1.0", "false" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList.add(new String[] { "6", "3", null, "66cf5b44-f290-46e9-ab88-3c0779d9fc7b", "0", "false", "1.0", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                "id:1.0", "false" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList.add(new String[] { "4", "2", "lis", "70ae237d-c24d-4ffc-b82e-bf111b9c441f", "1", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                "true" });//$NON-NLS-1$
        inputDataList.add(new String[] { "2", "111", "li", "7da41ca8-a4b0-4bf4-8d4d-bb53edfd97ef", "3", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                "true" });//$NON-NLS-1$
        inputDataList.add(new String[] { "1", "1", "li", "7da41ca8-a4b0-4bf4-8d4d-bb53edfd97ef", "0", "false", "1.0", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "id:1.0", "false" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList.add(new String[] { "2", "1", "wang", "7da41ca8-a4b0-4bf4-8d4d-bb53edfd97ef", "0", "false", "1.0", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "id:1.0", "false" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList.add(new String[] { "3", "1", "zhang", "7da41ca8-a4b0-4bf4-8d4d-bb53edfd97ef", "0", "false", "1.0", "0.0", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "id:1.0", "false" });//$NON-NLS-1$ //$NON-NLS-2$
        inputDataList.add(new String[] { "7", "12", "zhao", "aa210319-0ed9-4075-903e-9cd6b25735e1", "1", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                "true" });//$NON-NLS-1$
        inputDataList
                .add(new String[] { "8", "13", "zhaos", "e5e55f2f-2dfd-4773-8c02-9acbaa13c278", "1", "true", "1.0", "1.0", "", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                        "true" });//$NON-NLS-1$

        Map<String, String> matchRuleMap = new HashMap<>();
        matchRuleMap.put("MATCHING_TYPE", "Levenshtein"); //$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("RECORD_MATCH_THRESHOLD", "0.25");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("ATTRIBUTE_NAME", "name");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("SURVIVORSHIP_FUNCTION", "Concatenate");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("CONFIDENCE_WEIGHT", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("HANDLE_NULL", "nullMatchNull");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("ATTRIBUTE_THRESHOLD", "0.25");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("COLUMN_IDX", "2");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("MATCHING_ALGORITHM", "TSWOOSH_MATCHER");//$NON-NLS-1$ //$NON-NLS-2$
        matchRuleMap.put("PARAMETER", "");//$NON-NLS-1$ //$NON-NLS-2$
        List<Map<String, String>> ruleMapList = new ArrayList<>();
        List<List<Map<String, String>>> ruleMapListList = new ArrayList<>();
        ruleMapList.add(matchRuleMap);
        ruleMapListList.add(ruleMapList);
        TSwooshGrouping<String> tSwooshGrouping = new TSwooshGrouping<>(new TempRecordGrouping());
        for (String[] stringRow : inputDataList) {
            tSwooshGrouping.addToList(stringRow, ruleMapListList);
        }

        int originalInputColumnSize = 3;

        // init CombinedRecordMatcher
        CombinedRecordMatcher combinMatcher = new CombinedRecordMatcher();
        combinMatcher.setblockingThreshold(1.0);
        combinMatcher.setRecordSize(1);
        MFBRecordMatcher mfbRecordMatcher = new MFBRecordMatcher(0.25);
        mfbRecordMatcher.setAttributeWeights(new double[] { 1.0 });
        mfbRecordMatcher.setRecordMatchThreshold(0.25);
        mfbRecordMatcher.setRecordSize(1);
        LevenshteinMatcher levenshteinMatcher = new LevenshteinMatcher();
        levenshteinMatcher.setAttributeName("name"); //$NON-NLS-1$
        MFBAttributeMatcher mfbattMatcher = MFBAttributeMatcher.wrap(levenshteinMatcher, 1.0, 0.25, new SubString(-1, 0));
        mfbRecordMatcher.setAttributeMatchers(new IAttributeMatcher[] { mfbattMatcher });
        combinMatcher.add(mfbRecordMatcher);
        // mfbRecordMatcher.getMatchingWeight(null, null);

        SurvivorShipAlgorithmParams survivorParams = new SurvivorShipAlgorithmParams();
        // init SurvivorShipAlgorithmParams
        Map<Integer, SurvivorshipFunction> defaultSSRuleMap = new HashMap<>();
        survivorParams.setDefaultSurviorshipRules(defaultSSRuleMap);
        survivorParams.setRecordMatcher(combinMatcher);
        SurvivorShipAlgorithmParams.SurvivorshipFunction ssFunction = survivorParams.new SurvivorshipFunction();
        ssFunction.setSurvivorShipKey("name"); //$NON-NLS-1$
        ssFunction.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.CONCATENATE);
        SurvivorshipFunction[] SSAlgos = new SurvivorshipFunction[] { ssFunction };
        survivorParams.setSurviorShipAlgos(SSAlgos);
        Map<IRecordMatcher, SurvivorshipFunction[]> SSAlgosMap = new HashMap<>();
        SSAlgosMap.put(mfbRecordMatcher, SSAlgos);
        survivorParams.setSurvivorshipAlgosMap(SSAlgosMap);

        // call the test method
        tSwooshGrouping.swooshMatchWithMultipass(combinMatcher, survivorParams, originalInputColumnSize);
        tSwooshGrouping.afterAllRecordFinished();
        // check result
        boolean singlerecordMasterIsExist = false;
        boolean nullMasterIsExist = false;
        boolean lilisMasterIsExist = false;
        boolean zhaoszhaoMasterIsExist = false;
        boolean hellohelloMasterIsExist = false;
        Assert.assertEquals("Expect the size of result items is 18 but it is " + result.size(), 18, result.size()); //$NON-NLS-1$
        for (RichRecord rr : result) {
            if (rr.isMaster()) {
                Attribute attribute = rr.getAttributes().get(0);
                if ("singlerecord".equals(attribute.getValue()) && rr.getGrpSize() == 0 && rr.getGroupQuality() == 1.0) { //$NON-NLS-1$
                    singlerecordMasterIsExist = true;
                } else if (null == rr.getAttributes().get(0).getValue() && rr.getGrpSize() == 2 && rr.getGroupQuality() == 1.0) {
                    nullMasterIsExist = true;
                } else if ("lilis".equals(rr.getAttributes().get(0).getValue()) && rr.getGrpSize() == 5 //$NON-NLS-1$
                        && rr.getGroupQuality() == 0.6666666666666667) {
                    lilisMasterIsExist = true;
                } else if ("zhaoszhao".equals(rr.getAttributes().get(0).getValue()) && rr.getGrpSize() == 2 //$NON-NLS-1$
                        && rr.getGroupQuality() == 0.8) {
                    zhaoszhaoMasterIsExist = true;
                } else if ("hellohello".equals(rr.getAttributes().get(0).getValue()) && rr.getGrpSize() == 4 //$NON-NLS-1$
                        && rr.getGroupQuality() == 0.8333333333333334) {
                    hellohelloMasterIsExist = true;
                }
            }
        }
        Assert.assertTrue(
                "There is a master data which name is singlerecord should be show at here and which group size should be 0 which group quality should be 1.0", //$NON-NLS-1$
                singlerecordMasterIsExist);
        Assert.assertTrue(
                "There is a master data which name is null should be show at here and which group size should be 2 which group quality should be 1.0",
                nullMasterIsExist);
        Assert.assertTrue(
                "There is a master data which name is lilis should be show at here and which group size should be 5 which group quality should be 0.6666666666666667",
                lilisMasterIsExist);
        Assert.assertTrue(
                "There is a master data which name is zhaoszhao should be show at here and which group size should be 2 which group quality should be 0.8",
                zhaoszhaoMasterIsExist);
        Assert.assertTrue(
                "There is a master data which name is hellohello should be show at here and which group size should be 4 which group quality should be 0.8333333333333334",
                hellohelloMasterIsExist);
    }

    class TempRecordGrouping extends AbstractRecordGrouping<String> {

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(java.lang.Object[])
         */
        @Override
        protected void outputRow(String[] row) {
            // result.add(row);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#outputRow(org.talend.dataquality.record
         * .linkage.grouping.swoosh.RichRecord)
         */
        @Override
        protected void outputRow(RichRecord row) {
            result.add(row);

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#isMaster(java.lang.Object)
         */
        @Override
        protected boolean isMaster(String col) {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#incrementGroupSize(java.lang.Object)
         */
        @Override
        protected String incrementGroupSize(String oldGroupSize) {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#createTYPEArray(int)
         */
        @Override
        protected String[] createTYPEArray(int size) {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping#castAsType(java.lang.Object)
         */
        @Override
        protected String castAsType(Object objectValue) {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
