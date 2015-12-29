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
package org.talend.dataquality.statistics.frequency;

import java.util.ArrayList;
import java.util.List;

import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.type.DatetimePatternUtils;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

/**
 * Compute the pattern frequency tables.<br>
 * 
 * @author mzhao
 *
 */
public class PatternFrequencyAnalyzer extends FrequencyAnalyzer<PatternFrequencyStatistics> {

    private static final long serialVersionUID = -4658709249927616622L;

    @Override
    protected String getValuePattern(String originalValue) {
        if (TypeInferenceUtils.isDate(originalValue)) {
            return DatetimePatternUtils.getInstance().datePatternReplace(originalValue);
        } else if(TypeInferenceUtils.isTime(originalValue)){
            return DatetimePatternUtils.getInstance().timePatternReplace(originalValue);
        }else{
            return DatetimePatternUtils.getInstance().patternReplace(originalValue);
        }
    }

    protected void initFreqTableList(int size) {
        List<PatternFrequencyStatistics> freqTableList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PatternFrequencyStatistics freqTable = new PatternFrequencyStatistics();
            freqTable.setAlgorithm(algorithm);
            freqTable.setParameter(parameters);
            freqTableList.add(freqTable);
        }
        freqTableStatistics = new ResizableList<>(freqTableList);
    }

}
