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
package org.talend.dataquality.record.linkage.grouping.swoosh;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;

/**
 * A record with original information. (including the columns which is not designated to be match keys) Detailled
 * comment
 * 
 */
public class RichRecord extends Record {

    /**
     * The original row. It is an useful information when the application want to know the original information.
     */
    private List<DQAttribute<?>> originRow = null;

    private boolean isMerged = false;

    private boolean isMaster = false;

    private int grpSize = 0;

    // By default for master, score always equals to 1. For the rest of records, it is the score of a record pairs
    // matching score.
    private double score = 0d;

    // The group quality is a indicator to measure matching quality in a group. It takes a value of the minimum matching
    // score among all record pair matching scores. Only the master (merged record) has this value.
    private double groupQuality = 0d;

    // Matching distance details. Only none-master records has this attribute.
    private String labeledAttributeScores = StringUtils.EMPTY;

    private int recordSize = 0;

    /**
     * DOC zhao RichRecord constructor .
     * 
     * @param attributes
     * @param id
     * @param timestamp
     * @param source
     */
    public RichRecord(List<Attribute> attributes, String id, long timestamp, String source) {
        super(attributes, id, timestamp, source);
    }

    public RichRecord(String id, long timestamp, String source) {
        super(id, timestamp, source);
    }

    /**
     * Getter for originRow.
     * 
     * @return the originRow
     */
    public List<DQAttribute<?>> getOriginRow() {
        return this.originRow;
    }

    /**
     * Sets the originRow.
     * 
     * @param originRow the originRow to set
     */
    public void setOriginRow(List<DQAttribute<?>> originRow) {
        this.originRow = originRow;
    }

    /**
     * Sets the recordSize.
     * 
     * @param recordSize the recordSize to set
     */
    public void setRecordSize(int recordSize) {
        this.recordSize = recordSize;
    }

    /**
     * Getter for recordSize.
     * 
     * @return the recordSize
     */
    public int getRecordSize() {
        return this.recordSize;
    }

    public void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Getter for isMerged.
     * 
     * @return the isMerged
     */
    public boolean isMerged() {
        return this.isMerged;
    }

    /**
     * Sets the isMerged.
     * 
     * @param isMerged the isMerged to set
     */
    public void setMerged(boolean isMerged) {
        this.isMerged = isMerged;
    }

    /**
     * Getter for grpSize.
     * 
     * @return the grpSize
     */
    public int getGrpSize() {
        return this.grpSize;
    }

    /**
     * Sets the grpSize.
     * 
     * @param grpSize the grpSize to set
     */
    public void setGrpSize(int grpSize) {
        this.grpSize = grpSize;
    }

    /**
     * Getter for isMaster.
     * 
     * @return the isMaster
     */
    public boolean isMaster() {
        return this.isMaster;
    }

    /**
     * Getter for score.
     * 
     * @return the score
     */
    public double getScore() {
        return this.score;
    }

    /**
     * Getter for groupQuality.
     * 
     * @return the groupQuality
     */
    public double getGroupQuality() {
        return this.groupQuality;
    }

    /**
     * Sets the groupQuality.
     * 
     * @param groupQuality the groupQuality to set
     */
    public void setGroupQuality(double groupQuality) {
        this.groupQuality = groupQuality;
    }

    /**
     * Getter for labeledAttributeScores.
     * 
     * @return the labeledAttributeScores
     */
    public String getLabeledAttributeScores() {
        return this.labeledAttributeScores;
    }

    /**
     * Sets the labeledAttributeScores.
     * 
     * @param labeledAttributeScores the labeledAttributeScores to set
     */
    public void setLabeledAttributeScores(String labeledAttributeScores) {
        this.labeledAttributeScores = labeledAttributeScores;
    }

    public List<DQAttribute<?>> getOutputRow(Map<String, String> oldGID2New) {
        if (originRow == null) {
            return null;
        }
        if (isMerged()) {
            // Update the matching key field by the merged attributes.
            List<Attribute> matchKeyAttrs = getAttributes();
            for (Attribute attribute : matchKeyAttrs) {
                originRow.get(attribute.getColumnIndex()).setValue(attribute.getValue());
            }
        }
        /**
         * Else The columns that are not maching keys will be merged at {@link DQMFBRecordMerger#createNewRecord()}
         */
        if (isMaster()) {
            if (isMerged) {// Master records
                // Update group id.
                String finalGID = computeGID(oldGID2New);
                if (recordSize == originRow.size()) {
                    // The output is wait until the matching finished. (e.g. chart display in match analysis)
                    addOtherAttributesForFinished(finalGID);
                } else {
                    setOtherAttributeForMerge(finalGID);
                }
            } else {// Unique records
                        // GID
                addRandomGIDAndOthers();
            }

        } else {// Matched records (with records regardless of group quality)
            addOtherAttributeForNotMaster(oldGID2New);
        }
        return originRow;

    }

    /**
     * DOC yyin Comment method "addOtherAttributeForNotMaster".
     * 
     * @param oldGID2New
     */
    private void addOtherAttributeForNotMaster(Map<String, String> oldGID2New) {
        // GID
        originRow.add(new DQAttribute<>(SwooshConstants.GID, originRow.size(), computeGID(oldGID2New)));
        // Group size
        originRow.add(new DQAttribute<>(SwooshConstants.GROUP_SIZE, originRow.size(), 0));
        // Master
        originRow.add(new DQAttribute<>(SwooshConstants.IS_MASTER, originRow.size(), false));
        // Score
        originRow.add(new DQAttribute<>(SwooshConstants.SCORE2, originRow.size(), getScore()));
        // Group quality
        originRow.add(new DQAttribute<>(SwooshConstants.GROUP_QUALITY, originRow.size(), StringUtils.EMPTY));
        // destance details.
        originRow.add(new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, originRow.size(), getLabeledAttributeScores()));
    }

    /**
     * DOC yyin Comment method "addOtherAttributeForMerge".
     * 
     * @param finalGID
     */
    private void setOtherAttributeForMerge(String finalGID) {
        int extSize = 6;
        originRow.get(originRow.size() - extSize).setValue(finalGID);
        extSize--;
        // group size
        originRow.get(originRow.size() - extSize).setValue(String.valueOf(getGrpSize()));
        extSize--;
        // is master
        originRow.get(originRow.size() - extSize).setValue(String.valueOf(true));
        extSize--;
        // Score
        originRow.get(originRow.size() - extSize).setValue(String.valueOf(1.0));
        extSize--;
        // group quality
        originRow.get(originRow.size() - extSize).setValue(String.valueOf(getGroupQuality()));
        extSize--;
        // attribute scores (distance details).
        originRow.get(originRow.size() - extSize).setValue(StringUtils.EMPTY);
    }

    /**
     * DOC yyin Comment method "addOtherAttributesForFinished".
     * 
     * @param finalGID
     */
    private void addOtherAttributesForFinished(String finalGID) {
        originRow.add(new DQAttribute<>(SwooshConstants.GID, originRow.size(), finalGID));
        // group size
        originRow.add(new DQAttribute<>(SwooshConstants.GROUP_SIZE, originRow.size(), getGrpSize()));
        // is master
        originRow.add(new DQAttribute<>(SwooshConstants.IS_MASTER, originRow.size(), true));
        // Score
        originRow.add(new DQAttribute<>(SwooshConstants.SCORE2, originRow.size(), 1.0));
        // group quality
        originRow.add(new DQAttribute<>(SwooshConstants.GROUP_QUALITY, originRow.size(), String.valueOf(getGroupQuality())));
        // attribute scores (distance details).
        originRow.add(new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, originRow.size(), StringUtils.EMPTY));
    }

    /**
     * DOC yyin Comment method "addRandomGIDAndOthers".
     */
    private void addRandomGIDAndOthers() {
        originRow.add(new DQAttribute<>(SwooshConstants.GID, originRow.size(), UUID.randomUUID().toString()));
        // Group size
        originRow.add(new DQAttribute<>(SwooshConstants.GROUP_SIZE, originRow.size(), 1));
        // Master
        originRow.add(new DQAttribute<>(SwooshConstants.IS_MASTER, originRow.size(), true));
        // Score
        originRow.add(new DQAttribute<>(SwooshConstants.SCORE2, originRow.size(), 1.0));
        // Group quality
        originRow.add(new DQAttribute<>(SwooshConstants.GROUP_QUALITY, originRow.size(), String.valueOf(1.0)));
        // destance details.
        originRow.add(new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, originRow.size(), StringUtils.EMPTY));
    }

    /**
     * DOC zhao Comment method "computeGID".
     * 
     * @param oldGID2New
     * @return
     */
    private String computeGID(Map<String, String> oldGID2New) {
        String groupId = getGroupId();
        String finalGID = oldGID2New.get(groupId) == null ? groupId : oldGID2New.get(groupId);
        return finalGID;
    }

    /**
     * for the merged master rows from multipass, no need to add, only replace. Add one fixed output column: MERGE_INFO
     * 
     * @param oldGID2New
     * @param originalInputColumnSize
     * @return
     */
    public List<DQAttribute<?>> getOutputRow(Map<String, String> oldGID2New, int originalInputColumnSize, boolean withDetails) {
        if (isMerged()) {
            // Update the matching key field by the merged attributes.
            List<Attribute> matchKeyAttrs = getAttributes();
            for (Attribute attribute : matchKeyAttrs) {
                originRow.get(attribute.getColumnIndex()).setValue(attribute.getValue());
            }
        }
        /**
         * Else The columns that are not maching keys will be merged at {@link DQMFBRecordMerger#createNewRecord()}
         */
        int EXT_SIZE = 6;
        if (withDetails) {
            EXT_SIZE = 7;
        }
        if (isMerged || isMaster) {// Master records
            // Update group id.
            String finalGID = computeGID(oldGID2New);
            if (StringUtils.isBlank(finalGID)) {
                finalGID = UUID.randomUUID().toString();
            }
            if (getGrpSize() == 0) {
                setGrpSize(1);
            }
            setGroupId(finalGID);
            if (recordSize == originRow.size()) {
                int extSize = EXT_SIZE;
                originRow.set((originRow.size() - extSize), new DQAttribute<>(SwooshConstants.GID, originRow.size(), finalGID));
                extSize--;
                // group size
                originRow.set(originRow.size() - extSize,
                        new DQAttribute<>(SwooshConstants.GROUP_SIZE, originRow.size(), getGrpSize()));
                extSize--;
                // is master
                originRow.set(originRow.size() - extSize, new DQAttribute<>(SwooshConstants.IS_MASTER, originRow.size(), true));
                extSize--;
                // Score
                originRow.set(originRow.size() - extSize, new DQAttribute<>(SwooshConstants.SCORE2, originRow.size(), 1.0));
                extSize--;
                // group quality
                originRow.set(originRow.size() - extSize,
                        new DQAttribute<>(SwooshConstants.GROUP_QUALITY, originRow.size(), String.valueOf(1.0)));
                //
            }
        } else {
            String finalGID = computeGID(oldGID2New);
            setGroupId(finalGID);
            if (recordSize == originRow.size()) {
                int extSize = EXT_SIZE;
                originRow.set((originRow.size() - extSize), new DQAttribute<>(SwooshConstants.GID, originRow.size(), finalGID));
                extSize--;
                // group size
                originRow.set(originRow.size() - extSize, new DQAttribute<>(SwooshConstants.GROUP_SIZE, originRow.size(), 0));
                extSize--;
                // is master
                originRow.set(originRow.size() - extSize, new DQAttribute<>(SwooshConstants.IS_MASTER, originRow.size(), false));
                extSize--;
                // Score
                double score2 = getScore();
                if (score2 == 0.0) {
                    score2 = getOriginalValue(originRow.size() - extSize);
                }
                originRow.set(originRow.size() - extSize, new DQAttribute<>(SwooshConstants.SCORE2, originRow.size(), score2));
                extSize--;
                // group quality
                double groupQuality2 = getGroupQuality();
                if (groupQuality2 == 0.0) {
                    groupQuality2 = getOriginalValue(originalInputColumnSize + 4);
                }
                originRow.set(originRow.size() - extSize,
                        new DQAttribute<>(SwooshConstants.GROUP_QUALITY, originRow.size(), String.valueOf(groupQuality2)));
                // TDQ-11630: when multipass & output details, set the value when the detail is not empty.
                if (withDetails && StringUtils.isNotBlank(getLabeledAttributeScores())) {
                    extSize--;
                    originRow.set(originRow.size() - extSize,
                            new DQAttribute<>(SwooshConstants.ATTRIBUTE_SCORES, originRow.size(), getLabeledAttributeScores()));

                }

            }
        }
        return originRow;
    }

    /**
     * DOC yyin Comment method "getOriginalValue".
     * 
     * @param originalInputColumnSize
     * @param score2
     * @return
     */
    protected double getOriginalValue(int columnIndex) {
        String value = originRow.get(columnIndex).getValue();
        if (value == null || StringUtils.equalsIgnoreCase(SwooshConstants.NULL_STR, value)) {
            return 0.0;
        }
        return Double.parseDouble(value);
    }
}
