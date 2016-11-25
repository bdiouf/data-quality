package org.talend.dataquality.semantic.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.index.ClassPathDirectory;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.DQDocument;

public class LocalDictionaryCache {

    private static final Logger LOGGER = Logger.getLogger(LocalDictionaryCache.class);

    private DirectoryReader reader;

    private IndexSearcher searcher;

    LocalDictionaryCache(String contextName) {
        try {
            URI ddPath = CategoryRegistryManager.getInstance(contextName).getDictionaryURI();
            Directory dir = ClassPathDirectory.open(ddPath);
            reader = DirectoryReader.open(dir);
            searcher = new IndexSearcher(reader);
        } catch (IOException e) {
            LOGGER.error("Failed to read local dictionary cache! ", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to parse index URI! ", e);
        }
    }

    private List<DQDocument> dqDocListFromTopDocs(String categoryName, TopDocs docs) throws IOException {
        List<DQDocument> dqDocList = new ArrayList<>();
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document luceneDoc = reader.document(scoreDoc.doc);
            DQDocument dqDoc = DictionaryUtils.dictionaryEntryFromDocument(luceneDoc, categoryName);
            dqDocList.add(dqDoc);
        }
        return dqDocList;
    }

    public List<DQDocument> listDocuments(String categoryName, int offset, int n) {
        try {
            TopDocs docs = sendListDocumentsQuery(categoryName, offset, n);
            return dqDocListFromTopDocs(categoryName, docs);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private Query getListDocumentsQuery(String categoryName) throws IOException {
        Query q = new TermQuery(new Term(DictionarySearcher.F_WORD, categoryName));
        return q;
    }

    private TopDocs sendListDocumentsQuery(String categoryName, int offset, int n) throws IOException {
        if (offset <= 0) {
            return searcher.search(getListDocumentsQuery(categoryName), n);
        } else {
            TopDocs topDocs = searcher.search(getListDocumentsQuery(categoryName), offset + n);
            Query q = new TermQuery(new Term(DictionarySearcher.F_WORD, categoryName));
            return searcher.searchAfter(topDocs.scoreDocs[Math.min(topDocs.totalHits, offset) - 1], q, n);
        }
    }

    public Set<String> suggestValues(String categoryName, String prefix) {
        return suggestValues(categoryName, prefix, 100);
    }

    public Set<String> suggestValues(String categoryName, String input, int num) {
        if (input != null) {
            final String trimmedInput = input.trim();
            if (trimmedInput.length() >= 2) {
                Set<String> values = doSuggestValues(categoryName, trimmedInput, num, true);
                if (values.isEmpty()) {
                    return doSuggestValues(categoryName, trimmedInput, num, false);
                } else {
                    return values;
                }
            }
        }
        return Collections.emptySet();
    }

    private Set<String> doSuggestValues(String categoryName, String input, int num, boolean isPrefixSearch) {
        String jointInput = DictionarySearcher.getJointTokens(input);
        String queryString = isPrefixSearch ? jointInput + "*" : "*" + jointInput + "*";

        final BooleanQuery booleanQuery = new BooleanQuery();
        final Query catQuery = new TermQuery(new Term(DictionarySearcher.F_WORD, categoryName));
        booleanQuery.add(catQuery, BooleanClause.Occur.MUST);
        final Query wildcardQuery = new WildcardQuery(new Term(DictionarySearcher.F_SYNTERM, queryString));
        booleanQuery.add(wildcardQuery, BooleanClause.Occur.MUST);

        Set<String> results = new TreeSet<String>();

        try {
            TopDocs topDocs = searcher.search(booleanQuery, num);
            for (int i = 0; i < topDocs.scoreDocs.length; i++) {
                Document doc = reader.document(topDocs.scoreDocs[i].doc);
                IndexableField[] fields = doc.getFields(DictionarySearcher.F_RAW);
                for (IndexableField f : fields) {
                    final String str = f.stringValue();
                    if (isPrefixSearch) {
                        if (StringUtils.startsWithIgnoreCase(str, input)
                                || StringUtils.startsWithIgnoreCase(DictionarySearcher.getJointTokens(str), jointInput)) {
                            results.add(str);
                        }
                    } else {// infix search
                        if (StringUtils.containsIgnoreCase(str, input)
                                || StringUtils.containsIgnoreCase(DictionarySearcher.getJointTokens(str), jointInput)) {
                            results.add(str);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.trace(e.getMessage(), e);
        }
        return results;
    }
}
