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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.matchmerge.mfb.RecordGenerator;
import org.talend.dataquality.matchmerge.mfb.RecordIterator;
import org.talend.dataquality.matchmerge.mfb.RecordIterator.ValueGenerator;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQMFB;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQMFBRecordMerger;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQRecordIterator;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;
import org.talend.utils.collections.BidiMultiMap;

/**
 * Record grouping class with t-swoosh algorithm.
 * 
 */
public class TSwooshGrouping<TYPE> {

    List<RecordGenerator> rcdsGenerators = new ArrayList<RecordGenerator>();

    int totalCount = 0;

    AbstractRecordGrouping<TYPE> recordGrouping;

    BidiMultiMap<String, String> oldGID2New = new BidiMultiMap<String, String>();

    // Added TDQ-9320: to use the algorithm handle the record one by one
    private DQMFB algorithm;

    /**
     * DOC zhao TSwooshGrouping constructor comment.
     */
    public TSwooshGrouping(AbstractRecordGrouping<TYPE> recordGrouping) {
        this.recordGrouping = recordGrouping;
    }

    /**
     * Getter for oldGID2New.
     * 
     * @return the oldGID2New
     */
    public Map<String, String> getOldGID2New() {
        return this.oldGID2New;
    }

    /**
     * Recording matching with t-swoosh algorithm.
     * 
     * @param inputRow
     * @param matchingRule
     */
    public void addToList(final TYPE[] inputRow, List<List<Map<java.lang.String, java.lang.String>>> multiMatchRules) {
        totalCount++;
        java.lang.String attributeName = null;
        Map<java.lang.String, ValueGenerator> rcdMap = new LinkedHashMap<String, RecordIterator.ValueGenerator>();
        for (List<Map<java.lang.String, java.lang.String>> matchRule : multiMatchRules) {
            for (final Map<java.lang.String, java.lang.String> recordMap : matchRule) {
                attributeName = recordMap.get(IRecordGrouping.ATTRIBUTE_NAME);
                if (attributeName == null) {
                    // Dummy matcher
                    continue;
                }
                rcdMap.put(attributeName, new ValueGenerator() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see org.talend.dataquality.matchmerge.mfb.RecordIterator.ValueGenerator#getColumnIndex()
                     */
                    @Override
                    public int getColumnIndex() {
                        return Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX));
                    }

                    @Override
                    public java.lang.String newValue() {
                        return (java.lang.String) inputRow[Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX))];
                    }
                });
            }
        }
        RecordGenerator rcdGen = new RecordGenerator();
        rcdGen.setMatchKeyMap(rcdMap);
        List<DQAttribute<?>> rowList = new ArrayList<DQAttribute<?>>();
        int colIdx = 0;
        for (TYPE attribute : inputRow) {
            DQAttribute<TYPE> attri = new DQAttribute<TYPE>(StringUtils.EMPTY, colIdx, attribute);
            rowList.add(attri);
            colIdx++;
        }
        rcdGen.setOriginalRow(rowList);
        rcdsGenerators.add(rcdGen);
    }

    public void swooshMatch(IRecordMatcher combinedRecordMatcher, SurvivorShipAlgorithmParams survParams) {
        MatchMergeAlgorithm malgorithm = createTswooshAlgorithm(combinedRecordMatcher, survParams, null);

        Iterator<Record> iterator = new DQRecordIterator(totalCount, rcdsGenerators);
        List<Record> mergedRecords = malgorithm.execute(iterator, new GroupingCallBack());
        outputResult(mergedRecords);
    }

    /**
     * DOC yyin Comment method "createTswooshAlgorithm".
     * 
     * @param combinedRecordMatcher
     * @param survParams
     * @return
     */
    private MatchMergeAlgorithm createTswooshAlgorithm(IRecordMatcher combinedRecordMatcher,
            SurvivorShipAlgorithmParams survParams, MatchMergeAlgorithm.Callback callback) {
        SurvivorShipAlgorithmEnum[] surviorShipAlgos = new SurvivorShipAlgorithmEnum[survParams.getSurviorShipAlgos().length];
        String[] funcParams = new String[surviorShipAlgos.length];
        int idx = 0;
        for (SurvivorshipFunction func : survParams.getSurviorShipAlgos()) {
            surviorShipAlgos[idx] = func.getSurvivorShipAlgoEnum();
            funcParams[idx] = func.getParameter();
            idx++;
        }
        return new DQMFB(combinedRecordMatcher, new DQMFBRecordMerger("MFB", funcParams, //$NON-NLS-1$
                surviorShipAlgos, survParams), callback);
    }

    // init the algorithm before do matching.
    public void initialMFBForOneRecord(IRecordMatcher combinedRecordMatcher, SurvivorShipAlgorithmParams survParams) {
        algorithm = (DQMFB) createTswooshAlgorithm(combinedRecordMatcher, survParams, new GroupingCallBack());
    }

    // do match on one single record
    public void oneRecordMatch(RichRecord printRcd) {
        algorithm.matchOneRecord(printRcd);
    }

    // get and output all result after all records finished
    public void afterAllRecordFinished() {
        List<Record> result = algorithm.getResult();
        outputResult(result);
    }

    /**
     * Output the result Result after all finished.
     * 
     * @param result
     */
    private void outputResult(List<Record> result) {
        for (Record rcd : result) {
            RichRecord printRcd = (RichRecord) rcd;
            output(printRcd);
        }
        totalCount = 0;
        rcdsGenerators.clear();
    }

    class GroupingCallBack implements MatchMergeAlgorithm.Callback {

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onBeginRecord(org.talend.dataquality.matchmerge
         * .Record)
         */
        @Override
        public void onBeginRecord(Record record) {
            // Nothing todo
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onMatch(org.talend.dataquality.matchmerge.
         * Record, org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.mfb.MatchResult)
         */
        @Override
        public void onMatch(Record record1, Record record2, MatchResult matchResult) {

            // record1 and record2 must be RichRecord from DQ grouping implementation.
            RichRecord richRecord1 = (RichRecord) record1;
            RichRecord richRecord2 = (RichRecord) record2;

            String grpId1 = richRecord1.getGroupId();
            String grpId2 = richRecord2.getGroupId();
            if (grpId1 == null && grpId2 == null) {
                // Both records are original records.
                String gid = UUID.randomUUID().toString(); // Generate a new GID.
                richRecord1.setGroupId(gid);
                richRecord2.setGroupId(gid);
                // group size is 0 for none-master record
                richRecord1.setGrpSize(0);
                richRecord2.setGrpSize(0);

                richRecord1.setMaster(false);
                richRecord2.setMaster(false);

                output(richRecord1);
                output(richRecord2);

            } else if (grpId1 != null && grpId2 != null) {
                // Both records are merged records.
                richRecord2.setGroupId(grpId1);
                // Put into the map: <gid2,gid1>
                oldGID2New.put(grpId2, grpId1);
                // Update map where value equals to gid2
                List<String> keysOfGID2 = oldGID2New.getKeys(grpId2);
                if (keysOfGID2 != null) {
                    for (String key : keysOfGID2) {
                        oldGID2New.put(key, grpId1);
                    }
                }

            } else if (grpId1 == null) {
                // richRecord1 is original record
                // GID is the gid of record 2.
                richRecord1.setGroupId(richRecord2.getGroupId());
                // group size is 0 for none-master record
                richRecord1.setGrpSize(0);
                richRecord1.setMaster(false);

                output(richRecord1);

            } else {
                // richRecord2 is original record.
                // GID
                richRecord2.setGroupId(richRecord1.getGroupId());
                // group size is 0 for none-master record
                richRecord2.setGrpSize(0);
                richRecord2.setMaster(false);

                output(richRecord2);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onNewMerge(org.talend.dataquality.matchmerge
         * .Record)
         */
        @Override
        public void onNewMerge(Record record) {
            // record must be RichRecord from DQ grouping implementation.
            RichRecord richRecord = (RichRecord) record;
            richRecord.setMaster(true);
            richRecord.setScore(1.0);
            if (record.getGroupId() != null) {
                richRecord.setMerged(true);
                richRecord.setGrpSize(richRecord.getRelatedIds().size());
                if (richRecord.getGroupQuality() == 0) {
                    // group quality will be the confidence (score) .
                    richRecord.setGroupQuality(record.getConfidence());
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onRemoveMerge(org.talend.dataquality.matchmerge
         * .Record)
         */
        @Override
        public void onRemoveMerge(Record record) {
            // record must be RichRecord from DQ grouping implementation.
            RichRecord richRecord = (RichRecord) record;
            if (richRecord.isMerged()) {
                richRecord.setOriginRow(null); // set null original row, won't be usefull anymore after another merge.
                richRecord.setGroupQuality(0);
            }
            richRecord.setMerged(false);
            richRecord.setMaster(false);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onDifferent(org.talend.dataquality.matchmerge
         * .Record, org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.mfb.MatchResult)
         */
        @Override
        public void onDifferent(Record record1, Record record2, MatchResult matchResult) {
            RichRecord currentRecord = (RichRecord) record2;
            currentRecord.setMaster(true);
            // The rest of group properties will be set in RichRecord$getOutputRow()
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onEndRecord(org.talend.dataquality.matchmerge
         * .Record)
         */
        @Override
        public void onEndRecord(Record record) {
            // Nothing todo
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#isInterrupted()
         */
        @Override
        public boolean isInterrupted() {
            // Nothing todo
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onBeginProcessing()
         */
        @Override
        public void onBeginProcessing() {
            // Nothing todo

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onEndProcessing()
         */
        @Override
        public void onEndProcessing() {
            // Nothing todo

        }

    }

    private void output(RichRecord record) {
        recordGrouping.outputRow(record);
    }
}
