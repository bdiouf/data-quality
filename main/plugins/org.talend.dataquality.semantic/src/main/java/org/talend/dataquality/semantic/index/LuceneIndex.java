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
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.search.TopDocs;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

/**
 * Created by sizhaoliu on 03/04/15.
 */
public class LuceneIndex implements Index {

    private final SynonymIndexSearcher searcher;

    public LuceneIndex(URI indexPath, SynonymIndexSearcher.SynonymSearchMode searchMode) {
        searcher = new SynonymIndexSearcher(indexPath);
        searcher.setTopDocLimit(5);
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
            for (int i = 0; i < docs.scoreDocs.length; i++) {
                int docNumber = docs.scoreDocs[i].doc;
                String category = searcher.getWordByDocNumber(docNumber);
                foundCategorySet.add(category);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foundCategorySet;
    }

}
