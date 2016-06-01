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
package org.talend.dataquality.statistics.frequency.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Frequency statistics evaluator with keep a internal map to track with the frequency table.<br>
 * This is a "naive" way to compute the frequency table by maintaining a Map tracking the frequency table.</br>
 * Note
 * that in case of large size data being analyzed, this analyzer may bring memory issue, use
 * {@link SSFrequencyEvaluator} instead.
 * 
 * @author zhao
 *
 */
public class NaiveFrequencyEvaluator extends AbstractFrequencyEvaluator {

    private Map<String, Long> value2freq = new HashMap<String, Long>();

    @Override
    public void add(String value) {
        Long freq = value2freq.get(value);
        if (freq == null) {
            value2freq.put(value, 1l);
        } else {
            value2freq.put(value, freq + 1);
        }
    }

    @Override
    public Map<String, Long> getTopK(int topk) {
        Comparator<Entry<String, Long>> byValue = (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue());
        List<Map.Entry<String, Long>> list = value2freq.entrySet().stream().sorted(byValue.reversed())
                .collect(Collectors.toList());
        Map<String, Long> ftMap = new LinkedHashMap<String, Long>();
        int count = 1;
        for (Entry<String, Long> entry : list) {
            ftMap.put(entry.getKey(), entry.getValue());
            if (count == topk) {
                break;
            }
            count++;
        }
        return ftMap;
    }

    @Override
    public long getFrequency(String item) {
        return value2freq.get(item);
    }

    @Override
    public void setParameters(Map<String, String> params) {
        // No parameter needed.
    }
}
