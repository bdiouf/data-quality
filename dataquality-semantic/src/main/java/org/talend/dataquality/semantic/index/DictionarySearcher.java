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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class DictionarySearcher {

    private static final Logger LOGGER = Logger.getLogger(DictionarySearcher.class);

    public enum DictionarySearchMode {

        MATCH_SEMANTIC_DICTIONARY("MATCH_SEMANTIC_DICTIONARY"), // Used only for searching semantic dictionary
        MATCH_SEMANTIC_KEYWORD("MATCH_SEMANTIC_KEYWORD");// Used only for searching semantic keyword

        private String label;

        DictionarySearchMode(String label) {
            this.label = label;
        }

        private String getLabel() {
            return label;
        }

        /**
         * Method "get".
         *
         * @param label the label of the match mode
         * @return the match mode type given the label or null
         */
        public static DictionarySearchMode get(String label) {
            for (DictionarySearchMode type : DictionarySearchMode.values()) {
                if (type.getLabel().equalsIgnoreCase(label)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid search mode: " + label);
        }
    }

    public static final String F_ID = "id";//$NON-NLS-1$

    public static final String F_WORD = "word";//$NON-NLS-1$

    public static final String F_SYN = "syn";//$NON-NLS-1$

    public static final String F_SYNTERM = "synterm";//$NON-NLS-1$

    public static final String F_RAW = "raw";

    private SearcherManager mgr;

    private int topDocLimit = 3;

    private int maxEdits = 2; // Default value

    private static final int MAX_TOKEN_COUNT_FOR_SEMANTIC_MATCH = 20;

    private static final int MAX_CHAR_COUNT_FOR_SEMANTIC_MATCH = 100;

    private DictionarySearchMode searchMode = DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY;

    /**
     * SynonymIndexSearcher constructor creates this searcher and initializes the index.
     *
     * @param indexPath the path to the index.
     */
    public DictionarySearcher(String indexPath) {
        try {
            FSDirectory indexDir = FSDirectory.open(new File(indexPath));
            mgr = new SearcherManager(indexDir, null);
        } catch (IOException e) {
            LOGGER.error("Unable to open synonym index.", e);
        }
    }

    /**
     * SynonymIndexSearcher constructor creates this searcher and initializes the index.
     *
     * @param indexPath the path to the index.
     */
    public DictionarySearcher(URI indexPathURI) {
        try {
            Directory indexDir = ClassPathDirectory.open(indexPathURI);
            mgr = new SearcherManager(indexDir, null);
        } catch (IOException e) {
            LOGGER.error("Unable to open synonym index.", e);
        }
    }

    DictionarySearcher(Directory indexDir) throws IOException {
        mgr = new SearcherManager(indexDir, null);
    }

    /**
     * search for documents by one of the synonym (which may be the word).
     *
     * @param stringToSearch
     * @return
     * @throws java.io.IOException
     */
    public TopDocs searchDocumentBySynonym(String stringToSearch) throws IOException {
        TopDocs topDocs = null;
        Query query;
        switch (searchMode) {
        case MATCH_SEMANTIC_DICTIONARY:
            query = createQueryForSemanticDictionaryMatch(stringToSearch);
            break;
        case MATCH_SEMANTIC_KEYWORD:
            query = createQueryForSemanticKeywordMatch(stringToSearch);
            break;
        default: // do the same as MATCH_SEMANTIC_DICTIONARY mode
            query = createQueryForSemanticDictionaryMatch(stringToSearch);
            break;
        }
        final IndexSearcher searcher = mgr.acquire();
        topDocs = searcher.search(query, topDocLimit);
        mgr.release(searcher);
        return topDocs;
    }

    /**
     * Get a document from search result by its document number.
     *
     * @param docNum the doc number
     * @return the document (can be null if any problem)
     */
    public Document getDocument(int docNum) {
        Document doc = null;
        try {
            final IndexSearcher searcher = mgr.acquire();
            doc = searcher.doc(docNum);
            mgr.release(searcher);
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return doc;
    }

    /**
     * Method "getWordByDocNumber".
     *
     * @param docNo the document number
     * @return the document or null
     */
    public String getWordByDocNumber(int docNo) {
        Document document = getDocument(docNo);
        return document != null ? document.getValues(F_WORD)[0] : null;
    }

    /**
     * Method "getSynonymsByDocNumber".
     *
     * @param docNo the doc number
     * @return the synonyms or null if no document is found
     */
    public String[] getSynonymsByDocNumber(int docNo) {
        Document document = getDocument(docNo);
        return document != null ? document.getValues(F_SYN) : null;
    }

    /**
     * Method "getNumDocs".
     *
     * @return the number of documents in the index
     */
    public int getNumDocs() {
        try {
            final IndexSearcher searcher = mgr.acquire();
            final int numDocs = searcher.getIndexReader().numDocs();
            mgr.release(searcher);
            return numDocs;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Method "setTopDocLimit" set the maximum number of documents to return after a search.
     *
     * @param topDocLimit the limit
     */
    public void setTopDocLimit(int topDocLimit) {
        this.topDocLimit = topDocLimit;
    }

    private Query getTermQuery(String field, String text, boolean fuzzy) {
        Term term = new Term(field, text);
        return fuzzy ? new FuzzyQuery(term, maxEdits) : new TermQuery(term);
    }

    /**
     * @param input
     * @return
     * @throws IOException
     */
    private Query createQueryForSemanticDictionaryMatch(String input) throws IOException {
        // for dictionary search, ignore searching for input containing too many tokens
        if (input.length() > MAX_CHAR_COUNT_FOR_SEMANTIC_MATCH) {
            return new TermQuery(new Term(F_SYNTERM, StringUtils.EMPTY));
        }

        return getTermQuery(F_SYNTERM, StringUtils.join(getTokensFromAnalyzer(input), ' '), false);
    }

    /**
     * 
     * 
     * @param input
     * @return
     * @throws IOException
     */
    private Query createQueryForSemanticKeywordMatch(String input) throws IOException {
        BooleanQuery booleanQuery = new BooleanQuery();
        List<String> tokens = getTokensFromAnalyzer(input);
        // for keyword search, only search the beginning tokens from input
        if (tokens.size() > MAX_TOKEN_COUNT_FOR_SEMANTIC_MATCH) {
            for (int i = 0; i < MAX_TOKEN_COUNT_FOR_SEMANTIC_MATCH; i++) {
                booleanQuery.add(getTermQuery(F_SYNTERM, tokens.get(i), false), BooleanClause.Occur.SHOULD);
            }
        } else {
            for (String token : tokens) {
                booleanQuery.add(getTermQuery(F_SYNTERM, token, false), BooleanClause.Occur.SHOULD);
            }
        }
        return booleanQuery;
    }

    public void close() {
        try {
            mgr.acquire().getIndexReader().close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public DictionarySearchMode getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(DictionarySearchMode searchMode) {
        this.searchMode = searchMode;
    }

    public void setMaxEdits(int maxEdits) {
        this.maxEdits = maxEdits;
    }

    public static String getJointTokens(String input) {
        return StringUtils.join(getTokensFromAnalyzer(input), ' ');
    }

    /**
     * 
     * @param input
     * @return a list of lower-case tokens which strips accents & punctuation
     * @throws IOException
     */
    public static List<String> getTokensFromAnalyzer(String input) {
        StandardTokenizer tokenStream = new StandardTokenizer(new StringReader(input));
        TokenStream result = new StandardFilter(tokenStream);
        result = new LowerCaseFilter(result);
        result = new ASCIIFoldingFilter(result);
        CharTermAttribute charTermAttribute = result.addAttribute(CharTermAttribute.class);
        List<String> termList = new ArrayList<String>();
        try {
            tokenStream.reset();
            while (result.incrementToken()) {
                String term = charTermAttribute.toString();
                termList.add(term);
            }
            result.close();
        } catch (IOException e) {
            // do nothing
        }
        if (termList.size() == 1) { // require exact match when the input has only one token
            termList.clear();
            termList.add(StringUtils.stripAccents(input.toLowerCase()));
        }
        return termList;
    }

    public void maybeRefreshIndex() {
        try {
            mgr.maybeRefresh();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
