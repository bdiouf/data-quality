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
package org.talend.datascience.common.inference.type;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.type.DataType.Type;

/**
 * Type inference executor which provide several methods computing the types.<br>
 * The suggested usage of this class is the following call sequence:<br>
 * 1. {{@link #init()}, called once.<br>
 * 2. {{@link Analyzer#analyze(String...)} , called as many iterations as required.<br>
 * 3. {{@link #getResult()} , called once.<br>
 * 
 * <b>Important note:</b> This class is <b>NOT</b> thread safe.
 *
 */

public class DataTypeAnalyzer implements Analyzer<DataType> {

    private static final long serialVersionUID = 373694310453353502L;

    private final ResizableList<DataType> dataTypes = new ResizableList<>(DataType.class);

    private DataType.Type execute(String value) {
        if (TypeInferenceUtils.isEmpty(value)) {
            // 1. detect empty
            return DataType.Type.EMPTY;
        } else if (TypeInferenceUtils.isBoolean(value)) {
            // 2. detect boolean
            return DataType.Type.BOOLEAN;
        } else if (TypeInferenceUtils.isInteger(value)) {
            // 3. detect integer
            return DataType.Type.INTEGER;
        } else if (TypeInferenceUtils.isDouble(value)) {
            // 4. detect double
            return DataType.Type.DOUBLE;
        } else if (isDate(value)) {
            // 5. detect date
            return DataType.Type.DATE;
        } else if (isTime(value)) {
            // 6. detect date
            return DataType.Type.TIME;
        }
        // will return string when no matching
        return DataType.Type.STRING;
    }

    protected boolean isDate(String value) {
        return TypeInferenceUtils.isDate(value);
    }

    protected boolean isTime(String value) {
        return TypeInferenceUtils.isTime(value);
    }

    public void init() {
        dataTypes.clear();
    }

    /**
     * Analyze record of Array of string type, this method is used in scala library which not support parameterized
     * array type.
     * 
     * @param record
     * @return
     */
    public boolean analyzeArray(String[] record) {
        return analyze(record);
    }

    /**
     * Inferring types record by record.
     *
     * @param record for which the data type is guessed.
     * @return true if inferred successfully, false otherwise.
     */
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        dataTypes.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final DataType dataType = dataTypes.get(i);
            dataType.increment(execute(record[i]));
        }
        return true;
    }

    public void end() {
        // Nothing to do.
    }

    /**
     * Get the inferring result, this method should be called once and only once after {
     * {@link Analyzer#analyze(String...)} method.
     * 
     * @return A map for <b>each</b> column. Each map contains the type occurrence count.
     */
    public List<DataType> getResult() {
        return dataTypes;
    }

    @Override
    public Analyzer<DataType> merge(Analyzer<DataType> another) {
        int idx = 0;
        DataTypeAnalyzer mergedAnalyzer = new DataTypeAnalyzer();
        for (DataType dt : dataTypes) {
            mergedAnalyzer.getResult().add(idx, dt);
            if (!another.getResult().isEmpty()) {
                Map<DataType.Type, Long> typeFreqTable = dt.getTypeFrequencies();
                Map<DataType.Type, Long> anotherTypeFreqTable = another.getResult().get(idx).getTypeFrequencies();
                Iterator<Type> anotherDTIt = anotherTypeFreqTable.keySet().iterator();
                while (anotherDTIt.hasNext()) {
                    Type anotherDT = anotherDTIt.next();
                    // Update the current map
                    if (typeFreqTable.containsKey(anotherDT)) {
                        mergedAnalyzer.getResult().get(idx).getTypeFrequencies()
                                .put(anotherDT, typeFreqTable.get(anotherDT) + anotherTypeFreqTable.get(anotherDT));
                    } else {
                        mergedAnalyzer.getResult().get(idx).getTypeFrequencies()
                                .put(anotherDT, anotherTypeFreqTable.get(anotherDT));
                    }
                }
            }
            idx++;
        }
        return mergedAnalyzer;

    }

    @Override
    public void close() throws Exception {

    }

}
