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
package org.talend.dataquality.statistics.numeric.summary;

/**
 * Summary statistics bean <br>
 * Note that variance computation is based on apache common match library. See more at <a href=
 * "http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/stat/descriptive/SummaryStatistics.html"
 * >SummaryStatistics</a>
 * 
 * @author mzhao
 *
 */
public class SummaryStatistics {

    private org.apache.commons.math3.stat.descriptive.SummaryStatistics summaryStatistics = null;

    /**
     * Add the data to memory so that the variance can be computed given this list.
     * 
     * @param value field valued added to the list.<br>
     * See more about add value mehtod
     */
    public void addData(double value) {
        if (summaryStatistics == null) {
            summaryStatistics = new org.apache.commons.math3.stat.descriptive.SummaryStatistics();
        }
        summaryStatistics.addValue(value);
    }

    public double getMin() {
        if (summaryStatistics != null) {
            return summaryStatistics.getMin();
        }
        return Double.NaN;
    }

    public double getMax() {
        if (summaryStatistics != null) {
            return summaryStatistics.getMax();
        }
        return Double.NaN;
    }

    public double getMean() {
        if (summaryStatistics != null) {
            return summaryStatistics.getMean();
        }
        return Double.NaN;
    }

    public double getVariance() {
        if (summaryStatistics != null) {
            return summaryStatistics.getVariance();
        }
        return Double.NaN;
    }

    public double getSum() {
        if (summaryStatistics != null) {
            return summaryStatistics.getSum();
        }
        return Double.NaN;
    }

}
