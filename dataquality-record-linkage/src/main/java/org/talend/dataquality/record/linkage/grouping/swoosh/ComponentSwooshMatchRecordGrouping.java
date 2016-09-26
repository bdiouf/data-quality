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

import org.apache.commons.lang.StringUtils;

/**
 * created by yyin on 2016年3月2日 Detailled comment
 *
 */
public class ComponentSwooshMatchRecordGrouping extends AnalysisSwooshMatchRecordGrouping {

    private boolean matchFinished = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping#initialize()
     */
    // @Override
    // public void initialize() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    // masterRecords.clear();
    // getKeyAttributes();
    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping#end()
     */
    @Override
    public void end() {
        if (isOutputDistDetails() && getIsDisplayAttLabels()) {
            combinedRecordMatcher.setDisplayLabels(true);
        }

        matchFinished = false;
        if (isLinkToPrevious) {// use multipass
            swooshGrouping.swooshMatchWithMultipass(combinedRecordMatcher, survivorShipAlgorithmParams, originalInputColumnSize);
        } else {
            // during the match, the output in processing will not output really
            swooshGrouping.swooshMatch(combinedRecordMatcher, survivorShipAlgorithmParams);
        }
        swooshGrouping.afterAllRecordFinished();
        matchFinished = true;

        for (RichRecord row : tmpMatchResult) {
            // For swoosh algorithm, the GID can only be know after all of the records are computed.
            outputRow(row);
        }

        // Clear the GID map , no use anymore.
        clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.grouping.swoosh.AnalysisSwooshMatchRecordGrouping#outputRow(org.talend.
     * dataquality.record.linkage.grouping.swoosh.RichRecord)
     */
    @Override
    protected void outputRow(RichRecord row) {
        if (!matchFinished) {
            tmpMatchResult.add(row);
        } else {
            if (this.isPassOriginalValue) {//TDQ-12057 when the first tmatchgroup select to pass the original value, just pass this record 
                String[] strRow = getValuesFromOriginRow(row);
                Object[] withOrigin = new Object[strRow.length + 1];
                int index = 0;
                for (String str : strRow) {
                    withOrigin[index++] = str;
                }
                //Added TDQ-12057 : put the whole attributes of the current record into the output(last position).
                withOrigin[index] = row.getAttributes();
                outputRow(withOrigin);
            } else {
                super.outputRow(row);
            }
        }
    }

    // TODO: need to add the info: multipass + output DD
    @Override
    protected List<DQAttribute<?>> getOutputRow(RichRecord row) {
        if (this.isLinkToPrevious) {
            if (this.swooshGrouping.isHasPassedOriginal()) {//Added TDQ-12057
                int ext = 7;
                if (isOutputDistDetails()) {
                    ext = 8;
                }
                List<DQAttribute<?>> outputRow = row.getoutputRow(swooshGrouping.getOldGID2New(), isOutputDistDetails(), ext);
                outputRow.remove(outputRow.size() - 2);
                outputRow.add(originalInputColumnSize - 1,
                        new DQAttribute<>(SwooshConstants.ORIGINAL_RECORD, originalInputColumnSize, ""));
                return outputRow;
            } //~
            return row.getOutputRow(swooshGrouping.getOldGID2New(), isOutputDistDetails());
        } else {
            List<DQAttribute<?>> outputRow = super.getOutputRow(row);
            if (!isOutputDistDetails()
                    && StringUtils.equals(SwooshConstants.ATTRIBUTE_SCORES, outputRow.get(outputRow.size() - 1).getLabel())) {//if not output details, need to remove the last : Attribute scores
                outputRow.remove(outputRow.size() - 1);
            }
            return outputRow;
        }
    }

}
