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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.clearspring.analytics.stream.Counter;
import com.clearspring.analytics.stream.StreamSummary;

/**
 * Statatistics bean implement by StreamSummary library in "stream-lib" package. <br>
 * Using SpaceSaver algorithm to compute the frequency tables.<br>
 * the parameter <code>capacity</code> is used to tune precision trading off spaces.<br>
 * See more details about this parameter please refer to <a href=
 * "https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/stream/StreamSummary.java#L64"
 * >StreamSummary</a>
 * 
 * @author mzhao
 *
 */
public class SSFrequencyEvaluator extends AbstractFrequencyEvaluator {

    private StreamSummary<String> streamSummary;

    public SSFrequencyEvaluator() {
        streamSummary = new StreamSummary<String>(2000);
    }

    public StreamSummary<String> getStreamSummary() {
        return streamSummary;
    }

    @Override
    public Map<String, Long> getTopK(int topK) {
        Map<String, Long> freqTable = new LinkedHashMap<String, Long>();
        List<Counter<String>> counters = streamSummary.topK(topK);
        for (Counter<String> counter : counters) {
            freqTable.put(counter.getItem(), counter.getCount());
        }
        return freqTable;
    }

    @Override
    public void setParameters(Map<String, String> params) throws IllegalArgumentException {
        if (params.get(CAPACITY) != null) {
            try {
                int capacity = Integer.valueOf(params.get(CAPACITY));
                streamSummary = new StreamSummary<String>(capacity);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * Not available getting a specific item's frequency.
     */
    @Override
    public long getFrequency(String item) {
        return 0;
    }

    @Override
    public void add(String value) {
        streamSummary.offer(value);

    }

    public static final String CAPACITY = "capacity";
}
