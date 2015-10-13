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
package org.talend.dataquality.statistics.quality;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.QualityAnalyzer;
import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.ValueQualityStatistics;
import org.talend.datascience.common.inference.type.DataType;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class DataTypeQualityAnalyzer extends QualityAnalyzer<ValueQualityStatistics, DataType.Type[]> {

    private static final long serialVersionUID = -5951511723860660263L;

    private final ResizableList<ValueQualityStatistics> results = new ResizableList<>(ValueQualityStatistics.class);

    private boolean isStoreInvalidValues = true;

    private static Logger log = Logger.getLogger(DataTypeQualityAnalyzer.class);

    public DataTypeQualityAnalyzer(DataType.Type[] types, boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
        this.types = types;
    }

    public DataTypeQualityAnalyzer(DataType.Type... types) {
        this.types = types;
    }

    @Override
    public void init() {
        // Nothing to do.
    }

    @Override
    public void setStoreInvalidValues(boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
    }

    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            record = new String[] { StringUtils.EMPTY };
        }
        results.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final String value = record[i];
            final ValueQualityStatistics valueQuality = results.get(i);
            if (TypeInferenceUtils.isEmpty(value)) {
                valueQuality.incrementEmpty();
            } else if (TypeInferenceUtils.isValid(types[i], value)) {
                valueQuality.incrementValid();
            } else {
                valueQuality.incrementInvalid();
                processInvalidValue(valueQuality, value);
            }
        }
        return true;
    }

    private void processInvalidValue(ValueQualityStatistics valueQuality, String invalidValue) {
        if (isStoreInvalidValues) {
            valueQuality.appendInvalidValue(invalidValue);
        }
    }

    @Override
    public void end() {
        // Nothing to do.
    }

    @Override
    public List<ValueQualityStatistics> getResult() {
        return results;
    }

    @Override
    public Analyzer<ValueQualityStatistics> merge(Analyzer<ValueQualityStatistics> another) {

        if (another == null) {
            log.warn("Another analyzer is null, have nothing to merge!");
            return this;
        }

        int idx = 0;
        DataTypeQualityAnalyzer mergedValueQualityAnalyze = new DataTypeQualityAnalyzer();
        ((ResizableList<ValueQualityStatistics>) mergedValueQualityAnalyze.getResult()).resize(results.size());
        for (ValueQualityStatistics qs : results) {
            ValueQualityStatistics mergedStats = mergedValueQualityAnalyze.getResult().get(idx);
            ValueQualityStatistics anotherStats = another.getResult().get(idx);
            mergedStats.setValidCount(qs.getValidCount() + anotherStats.getValidCount());
            mergedStats.setInvalidCount(qs.getInvalidCount() + anotherStats.getInvalidCount());
            mergedStats.setEmptyCount(qs.getEmptyCount() + anotherStats.getEmptyCount());
            if (!qs.getInvalidValues().isEmpty()) {
                mergedStats.getInvalidValues().addAll(qs.getInvalidValues());
            }
            if (!anotherStats.getInvalidValues().isEmpty()) {
                mergedStats.getInvalidValues().addAll(anotherStats.getInvalidValues());
            }
            idx++;
        }
        return mergedValueQualityAnalyze;
    }

    @Override
    public void close() throws Exception {
    }
}
