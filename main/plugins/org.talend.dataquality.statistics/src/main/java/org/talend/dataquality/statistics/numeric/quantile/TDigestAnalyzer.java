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
package org.talend.dataquality.statistics.numeric.quantile;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataquality.statistics.numeric.NumericalStatisticsAnalyzer;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.type.DataType.Type;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

/**
 * Analyze the quantiles given t-digest algorithm implemented by clearspring's "stream-lib" package. See more at <a
 * href=
 * "https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/stream/quantile/TDigest.java"
 * >TDigest</a>
 * 
 * @author zhao
 *
 */
public class TDigestAnalyzer extends NumericalStatisticsAnalyzer<TDigestQuantileStatistics> {

    private static final long serialVersionUID = -9176043422228459277L;

    private final ResizableList<TDigestQuantileStatistics> stats = new ResizableList<>(TDigestQuantileStatistics.class);

    private Integer[] compression = null;

    private boolean isDigestInited = false;

    public TDigestAnalyzer(Type[] types) {
        super(types);
    }

    @Override
    public void init() {
        super.init();
        stats.clear();
    }

    public void init(Integer[] compression) {
        this.compression = compression;
        isDigestInited = false;
    }

    @Override
    public boolean analyze(String... record) {
        Type[] types = this.getTypes();

        if (record.length != types.length)
            throw new IllegalArgumentException("Each column of the record should be declared a DataType.Type corresponding! \n"
                    + types.length + " type(s) declared in this T-Digest analyzer but " + record.length
                    + " column(s) was found in this record. \n"
                    + "Using method: setTypes(DataType.Type[] types) to set the types.");

        stats.resize(record.length);

        for (int id : this.getStatColIdx()) { // analysis each numerical column
                                              // in the record
            if (!TypeInferenceUtils.isValid(types[id], record[id])) {
                continue;
            }
            TDigestQuantileStatistics stat = stats.get(id);
            if (compression != null && !isDigestInited) {
                stat.initTDigest(compression[id]);
            }
            stat.add(Double.valueOf(record[id]));
        }

        if (!isDigestInited) {
            isDigestInited = true;
        }

        return true;
    }

    @Override
    public void end() {

    }

    @Override
    public List<TDigestQuantileStatistics> getResult() {
        return stats;
    }

    @Override
    public Analyzer<TDigestQuantileStatistics> merge(Analyzer<TDigestQuantileStatistics> another) {
        throw new NotImplementedException();
    }

}
