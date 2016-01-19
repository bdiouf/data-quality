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
package org.talend.dataquality.semantic.index;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

/**
 * Created by sizhaoliu on 03/04/15.
 */
public class LuceneIndex implements Index {

    private final SynonymIndexSearcher searcher;

    private LuceneLevenshteinDistance levenshtein = new LuceneLevenshteinDistance();

    private float minimumSimilarity = 0.75F;

    public LuceneIndex(URI indexPath, SynonymIndexSearcher.SynonymSearchMode searchMode) {
        searcher = new SynonymIndexSearcher(indexPath);
        searcher.setTopDocLimit(20);
        searcher.setSearchMode(searchMode);
        searcher.setMaxEdits(2);
    }

    @Override
    public void initIndex() {

    }

    @Override
    public void closeIndex() {
        searcher.close();
    }

    @Override
    public Set<String> findCategories(String data) {

        Set<String> foundCategorySet = new HashSet<String>();
        try {
            TopDocs docs = searcher.searchDocumentBySynonym(data);
            List<String> inputTokens = searcher.getTokensFromAnalyzer(data);// get tokenized input data

            String joinedTokens = StringUtils.join(inputTokens, ' ');
            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                int docNumber = scoreDoc.doc;
                Document document = searcher.getDocument(docNumber);
                String category = document.getValues(SynonymIndexSearcher.F_WORD)[0];
                if (foundCategorySet.contains(category)) {
                    continue;
                }
                String[] synonyms = document.getValues(SynonymIndexSearcher.F_SYNTERM);
                for (String syn : synonyms) {
                    // verify if the tokenized input data contains all tokens from the search result
                    if (SynonymIndexSearcher.SynonymSearchMode.MATCH_SEMANTIC_KEYWORD.equals(searcher.getSearchMode())) {
                        // for KW index
                        if (joinedTokens.contains(syn)) {
                            foundCategorySet.add(category);
                            break;
                        }
                    } else {
                        foundCategorySet.add(category);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foundCategorySet;
    }

    public static Map<String, Float> sortMapByValue(Map<String, Float> unsortedMap) {
        List<Map.Entry<String, Float>> list = new LinkedList<>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {

            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
        for (Iterator<Map.Entry<String, Float>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Float> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Map<String, Float> findSimilarFieldsInCategory(String input, String category) {
        return findSimilarFieldsInCategory(input, category, minimumSimilarity);
    }

    public Map<String, Float> findSimilarFieldsInCategory(String input, String category, Float similarity) {
        Map<String, Float> similarFieldMap = new HashMap<String, Float>();
        try {
            TopDocs docs = searcher.findSimilarValuesInCategory(input, category);
            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc.doc);
                IndexableField[] synFields = doc.getFields("syn");
                for (IndexableField synField : synFields) {
                    String synFieldValue = synField.stringValue();
                    if (!similarFieldMap.containsKey(synFieldValue)) {
                        float distance = calculateOverallSimilarity(input, synFieldValue);
                        if (distance >= similarity) {
                            similarFieldMap.put(synFieldValue, distance);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sortMapByValue(similarFieldMap);
    }

    private float calculateOverallSimilarity(String input, String field) throws IOException {
        final List<String> inputTokens = searcher.getTokensFromAnalyzer(input);
        final List<String> fieldTokens = searcher.getTokensFromAnalyzer(field);

        float bestTokenSimilarity = 0F;
        for (String inputToken : inputTokens) {
            for (String fieldToken : fieldTokens) {
                float similarity = levenshtein.getDistance(inputToken, fieldToken);
                if (similarity > bestTokenSimilarity) {
                    bestTokenSimilarity = similarity;
                }
            }
        }
        final float fullSimilarity = levenshtein.getDistance(input, field);
        final float overallSimilarity = Math.max((bestTokenSimilarity * 9 + fullSimilarity) / 10,
                (bestTokenSimilarity + fullSimilarity * 9) / 10);

        return overallSimilarity;
    }
}
