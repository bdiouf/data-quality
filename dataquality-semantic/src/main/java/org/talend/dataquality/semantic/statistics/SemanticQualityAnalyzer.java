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
package org.talend.dataquality.semantic.statistics;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum.RecognizerType;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.classifier.impl.DataDictFieldClassifier;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.QualityAnalyzer;
import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.ValueQualityStatistics;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class SemanticQualityAnalyzer extends QualityAnalyzer<ValueQualityStatistics, String[]> {

    private static final long serialVersionUID = -5951511723860660263L;

    private final ResizableList<ValueQualityStatistics> results = new ResizableList<>(ValueQualityStatistics.class);

    private UserDefinedClassifier regexClassifier;

    private DataDictFieldClassifier dataDictClassifier;

    private final CategoryRecognizerBuilder builder;

    public SemanticQualityAnalyzer(CategoryRecognizerBuilder builder, String[] types, boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
        this.builder = builder;
        this.types = types;
    }

    public SemanticQualityAnalyzer(CategoryRecognizerBuilder builder, String... types) {
        this.types = types;
        this.builder = builder;
    }

    @Override
    public void init() {
        try {
            regexClassifier = new UDCategorySerDeser().readJsonFile();
            if (Mode.LUCENE.equals(builder.getMode())) {
                LuceneIndex dict = new LuceneIndex(builder.getDDPath(),
                        SynonymIndexSearcher.SynonymSearchMode.MATCH_SEMANTIC_DICTIONARY);
                LuceneIndex keyword = new LuceneIndex(builder.getKWPath(),
                        SynonymIndexSearcher.SynonymSearchMode.MATCH_SEMANTIC_KEYWORD);
                dataDictClassifier = new DataDictFieldClassifier(dict, keyword);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setStoreInvalidValues(boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
    }

    /**
     * @deprecated use {@link #analyze(String...)}
     * <p>
     * TODO remove this method later
     * 
     * Analyze record of Array of string type, this method is used in scala library which not support parameterized
     * array type.
     * 
     * @param record
     * @return
     */
    @Deprecated
    public boolean analyzeArray(String[] record) {
        return analyze(record);
    }

    /**
     * TODO use String[] as parameter for this method.
     */
    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            record = new String[] { StringUtils.EMPTY };
        }
        results.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            String semanticType = types[i];
            if (SemanticCategoryEnum.UNKNOWN.name().equals(semanticType)) {
                continue;
            }

            final String value = record[i];
            final ValueQualityStatistics valueQuality = results.get(i);
            if (value == null || value.trim().length() == 0) {
                valueQuality.incrementEmpty();
            } else {
                analyzeValue(semanticType, value, valueQuality);
            }
        }
        return true;
    }

    private void analyzeValue(String semanticType, String value, ValueQualityStatistics valueQuality) {
        SemanticCategoryEnum cat = SemanticCategoryEnum.valueOf(semanticType);
        RecognizerType recognizerType = cat.getRecognizerType();
        Set<String> catIds = null;
        switch (recognizerType) {
        case OTHER:
            break;
        case REGEX:
            catIds = regexClassifier.classify(value);
            if (catIds.contains(semanticType)) {
                valueQuality.incrementValid();
            } else {
                valueQuality.incrementInvalid();
                processInvalidValue(valueQuality, value);
            }
            break;
        case CLOSED_INDEX:
            catIds = dataDictClassifier.classify(value);
            if (catIds.contains(semanticType)) {
                valueQuality.incrementValid();
            } else {
                valueQuality.incrementInvalid();
                processInvalidValue(valueQuality, value);
            }
            break;
        case OPEN_INDEX:
            catIds = dataDictClassifier.classify(value);
            if (catIds.contains(semanticType)) {
                valueQuality.incrementValid();
            } else {
                valueQuality.incrementUnknown();
                processUnknownValue(valueQuality, value);
            }
            break;
        default:
            break;
        }

    }

    private boolean isSemanticValid(String semanticType, String value) {

        SemanticCategoryEnum cat = SemanticCategoryEnum.valueOf(semanticType);
        RecognizerType recognizerType = cat.getRecognizerType();

        switch (recognizerType) {
        case OTHER:
            break;
        case REGEX:
            Set<String> regexCatIds = regexClassifier.classify(value);
            return regexCatIds.contains(semanticType);
        case OPEN_INDEX:
            break;
        case CLOSED_INDEX:
            Set<String> dictCatIds = dataDictClassifier.classify(value);
            return dictCatIds.contains(semanticType);
        default:
            break;
        }

        return true;
    }

    private boolean isSemanticUnknown(String semanticType, String value) {
        SemanticCategoryEnum cat = SemanticCategoryEnum.valueOf(semanticType);
        RecognizerType recognizerType = cat.getRecognizerType();

        switch (recognizerType) {
        case OTHER:
            break;
        case REGEX:
            break;
        case OPEN_INDEX:
            Set<String> dictCatIds = dataDictClassifier.classify(value);
            return !dictCatIds.contains(semanticType);
        case CLOSED_INDEX:
            break;
        default:
            break;
        }

        return false;
    }

    private void processInvalidValue(ValueQualityStatistics valueQuality, String invalidValue) {
        if (isStoreInvalidValues) {
            valueQuality.appendInvalidValue(invalidValue);
        }
    }

    private void processUnknownValue(ValueQualityStatistics valueQuality, String unknownValue) {
        if (isStoreInvalidValues) {
            valueQuality.appendUnknownValue(unknownValue);
        }
    }

    @Override
    public void end() {
    }

    @Override
    public List<ValueQualityStatistics> getResult() {
        return results;
    }

    @Override
    public Analyzer<ValueQualityStatistics> merge(Analyzer<ValueQualityStatistics> analyzer) {
        int idx = 0;
        SemanticQualityAnalyzer mergedValueQualityAnalyze = new SemanticQualityAnalyzer(this.builder, this.types);
        ((ResizableList<ValueQualityStatistics>) mergedValueQualityAnalyze.getResult()).resize(results.size());
        for (ValueQualityStatistics qs : results) {
            ValueQualityStatistics mergedStats = mergedValueQualityAnalyze.getResult().get(idx);
            ValueQualityStatistics anotherStats = analyzer.getResult().get(idx);
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
        dataDictClassifier.closeIndex();
    }
}
