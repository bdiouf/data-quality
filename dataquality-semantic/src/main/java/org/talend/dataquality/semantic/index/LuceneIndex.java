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
package org.talend.dataquality.semantic.index;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

/**
 * Created by sizhaoliu on 03/04/15.
 */
public class LuceneIndex implements Index {

    private final SynonymIndexSearcher searcher;

    public LuceneIndex(URI indexPath, SynonymIndexSearcher.SynonymSearchMode searchMode) {
        searcher = new SynonymIndexSearcher(indexPath);
        searcher.setTopDocLimit(20);
        searcher.setSearchMode(searchMode);
    }

    @Override
    public void initIndex() {

    }

    @Override
    public void closeIndex() {

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
}
