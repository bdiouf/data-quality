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
package org.talend.dataquality.record.linkage.grouping.swoosh;

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
            super.outputRow(row);
        }
    }

}
