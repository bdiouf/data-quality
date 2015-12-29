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
package org.talend.dataquality.standardization.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class WordNetSynonymEngine implements SynonymEngine {

    private Directory directory;

    private IndexSearcher searcher;

    /**
     * Reuse this term for optimized creation of terms.
     */
    private Term term = new Term("word", "");//$NON-NLS-1$ //$NON-NLS-2$

    public WordNetSynonymEngine(File index) throws IOException {
        directory = FSDirectory.open(index);
        searcher = new IndexSearcher(directory);
    }

    public void close() throws IOException {
        this.searcher.close();
        this.directory.close();
    }

    @Override
    public String[] getSynonyms(String word) throws IOException {

        List<String> synList = new ArrayList<String>();

        // collect every matching document
        AllDocCollector collector = new AllDocCollector();
        PhraseQuery pq = new PhraseQuery();
        pq.add(term.createTerm(word));
        searcher.search(pq, collector);

        // iterate over matching documents
        for (ScoreDoc hits : collector.getHits()) {
            Document doc = searcher.doc(hits.doc);
            String[] values = doc.getValues("syn"); //$NON-NLS-1$ // FIXME hard coded string

            // record synonyms
            for (String value : values) {
                synList.add(value);
            }
        }

        return synList.toArray(new String[0]);
    }
}
