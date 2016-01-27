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
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @author scorreia A class to create an index with synonyms.
 */
public class SynonymIndexSearcher {

    private static final Logger LOGGER = Logger.getLogger(SynonymIndexSearcher.class);

    public enum SynonymSearchMode {

        MATCH_ANY("MATCH_ANY"),
        MATCH_PARTIAL("MATCH_PARTIAL"),
        MATCH_ALL("MATCH_ALL"),
        MATCH_EXACT("MATCH_EXACT"),
        MATCH_ANY_FUZZY("MATCH_ANY_FUZZY"),
        MATCH_ALL_FUZZY("MATCH_ALL_FUZZY"),

        MATCH_SEMANTIC_DICTIONARY("MATCH_SEMANTIC_DICTIONARY"), // Used only for searching semantic dictionary
        MATCH_SEMANTIC_KEYWORD("MATCH_SEMANTIC_KEYWORD");// Used only for searching semantic keyword

        private String label;

        SynonymSearchMode(String label) {
            this.label = label;
        }

        private String getLabel() {
            // TODO Auto-generated method stub
            return label;
        }

        /**
         * Method "get".
         *
         * @param label the label of the match mode
         * @return the match mode type given the label or null
         */
        public static SynonymSearchMode get(String label) {
            for (SynonymSearchMode type : SynonymSearchMode.values()) {
                if (type.getLabel().equalsIgnoreCase(label)) {
                    return type;
                }
            }
            return MATCH_ANY; // default value
        }
    }

    public static final String F_WORD = "word";//$NON-NLS-1$

    public static final String F_SYN = "syn";//$NON-NLS-1$

    public static final String F_WORDTERM = "wordterm";//$NON-NLS-1$

    public static final String F_SYNTERM = "synterm";//$NON-NLS-1$

    private IndexSearcher searcher;

    private int topDocLimit = 3;

    private float minimumSimilarity = 0.8f;

    private int maxEdits = 1; // Default value

    private static final float WORD_TERM_BOOST = 2F;

    private static final float WORD_BOOST = 1.5F;

    private static final int MAX_TOKEN_COUNT_FOR_SEMANTIC_MATCH = 20;

    private Analyzer analyzer;

    private SynonymSearchMode searchMode = SynonymSearchMode.MATCH_ANY;

    private float matchingThreshold = 0f;

    /**
     * The slop is only used for
     * {@link org.talend.dataquality.standardization.index.SynonymIndexSearcher.SynonymSearchMode#MATCH_PARTIAL}.
     * <p>
     * By default, the slop factor is one, meaning only one gap between the searched tokens is allowed.
     * <p>
     * For example: "the brown" can match "the quick brown fox", but "the fox" will not match it, except that we set the
     * slop value to 2 or greater.
     */
    private int slop = 1;

    /**
     * instantiate an index searcher. A call to the index initialization method such as {@link #openIndexInFS(URI)} is
     * required before using any other method.
     */
    public SynonymIndexSearcher() {
    }

    /**
     * SynonymIndexSearcher constructor creates this searcher and initializes the index.
     *
     * @param indexPath the path to the index.
     */
    public SynonymIndexSearcher(String indexPath) {
        try {
            openIndexInFS(indexPath);
        } catch (IOException e) {
            LOGGER.error("Unable to open synonym index.", e);
        }
    }

    /**
     * SynonymIndexSearcher constructor creates this searcher and initializes the index.
     *
     * @param indexPath the path to the index.
     */
    public SynonymIndexSearcher(URI indexPath) {
        try {
            openIndexInFS(indexPath);
        } catch (IOException e) {
            LOGGER.error("Unable to open synonym index.", e);
        }
    }

    SynonymIndexSearcher(IndexSearcher indexSearcher) {
        this.searcher = indexSearcher;
    }

    public void openIndexInFS(String path) throws IOException {
        FSDirectory indexDir = FSDirectory.open(new File(path));
        CheckIndex check = new CheckIndex(indexDir);
        Status status = check.checkIndex();
        if (status.missingSegments) {
            LOGGER.error("Failed to load index. Please make sure it's not empty."); //$NON-NLS-1$
        }
        IndexReader reader = DirectoryReader.open(indexDir);
        this.searcher = new IndexSearcher(reader);
    }

    /**
     * Method "openIndexInFS" opens a FS folder index.
     *
     * @param path the path of the index folder
     * @throws java.io.IOException if file does not exist, or any other problem
     */
    public void openIndexInFS(URI path) throws IOException {
        Directory indexDir = ClassPathDirectory.open(path);
        IndexReader reader = DirectoryReader.open(indexDir);
        this.searcher = new IndexSearcher(reader);
    }

    /**
     * search a document by the word.
     *
     * @param word
     * @return
     * @throws java.io.IOException
     */
    public TopDocs searchDocumentByWord(String word) {
        if (word == null) {
            return null;
        }
        String tempWord = word.trim();
        if (tempWord.equals("")) { //$NON-NLS-1$
            return null;
        }
        TopDocs docs = null;
        try {
            Query query = createWordQueryFor(tempWord);
            docs = this.searcher.search(query, topDocLimit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docs;
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
        case MATCH_ANY:
            query = createCombinedQueryFor(stringToSearch, false, false);
            break;
        case MATCH_PARTIAL:
            query = createCombinedQueryForPartialMatch(stringToSearch);
            break;
        case MATCH_ALL:
            query = createCombinedQueryFor(stringToSearch, false, true);
            break;
        case MATCH_EXACT:
            query = createCombinedQueryForExactMatch(stringToSearch);
            break;
        case MATCH_ANY_FUZZY:
            query = createCombinedQueryFor(stringToSearch, true, false);
            break;
        case MATCH_ALL_FUZZY:
            query = createCombinedQueryFor(stringToSearch, true, true);
            break;
        case MATCH_SEMANTIC_DICTIONARY:
            query = createQueryForSemanticDictionaryMatch(stringToSearch);
            break;
        case MATCH_SEMANTIC_KEYWORD:
            query = createQueryForSemanticKeywordMatch(stringToSearch);
            break;
        default: // do the same as MATCH_ANY mode
            query = createCombinedQueryFor(stringToSearch, false, false);
            break;
        }
        topDocs = this.searcher.search(query, topDocLimit);
        return topDocs;
    }

    /**
     * Count the synonyms of the first document found by a query on word.
     *
     * @param word
     * @return the number of synonyms
     */
    public int getSynonymCount(String word) {
        try {
            Query query = createWordQueryFor(word);
            TopDocs docs;
            docs = this.searcher.search(query, topDocLimit);
            if (docs.totalHits > 0) {
                Document doc = this.searcher.doc(docs.scoreDocs[0].doc);
                String[] synonyms = doc.getValues(F_SYN);
                return synonyms.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
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
            doc = this.searcher.doc(docNum);
        } catch (IOException e) {
            e.printStackTrace();
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
        return this.searcher.getIndexReader().numDocs();
    }

    /**
     * Getter for topDocLimit.
     *
     * @return the topDocLimit
     */
    public int getTopDocLimit() {
        return this.topDocLimit;
    }

    /**
     * Method "setTopDocLimit" set the maximum number of documents to return after a search.
     *
     * @param topDocLimit the limit
     */
    public void setTopDocLimit(int topDocLimit) {
        this.topDocLimit = topDocLimit;
    }

    /**
     * Getter for slop. The slop is the maximum number of moves allowed to put the terms in order.
     *
     * @return the slop
     */
    public int getSlop() {
        return this.slop;
    }

    /**
     * Sets the slop.
     *
     * @param slop the slop to set
     */
    public void setSlop(int slop) {
        this.slop = slop;
    }

    /**
     * Method "setAnalyzer".
     *
     * @param analyzer the analyzer to use in searches.
     */
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    /**
     *
     * @return the analyzer used in searches (StandardAnalyzer by default)
     */
    public Analyzer getAnalyzer() {
        if (analyzer == null) {
            analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
        }
        return this.analyzer;
    }

    private Query createWordQueryFor(String stringToSearch) {
        TermQuery query = new TermQuery(new Term(F_WORDTERM, stringToSearch.toLowerCase()));
        return query;
    }

    private Query getTermQuery(String field, String text, boolean fuzzy) {
        Term term = new Term(field, text);
        return fuzzy ? new FuzzyQuery(term, maxEdits) : new TermQuery(term);
    }

    public TopDocs findSimilarValuesInCategory(String input, String category) throws IOException {
        BooleanQuery combinedQuery = new BooleanQuery();
        if (category != null && !StringUtils.EMPTY.equals(category)) {
            Term catTerm = new Term(F_WORDTERM, category);
            Query catQuery = new TermQuery(catTerm);
            combinedQuery.add(catQuery, BooleanClause.Occur.MUST);
        }

        BooleanQuery valueQuery = new BooleanQuery();
        List<String> tokens = getTokensFromAnalyzer(input);
        Query inputTermQuery = getTermQuery(F_SYNTERM, StringUtils.join(tokens, ' '), true);
        valueQuery.add(inputTermQuery, BooleanClause.Occur.SHOULD);

        BooleanQuery inputTokenQuery = new BooleanQuery();
        for (String token : tokens) {
            inputTokenQuery.add(getTermQuery(F_SYN, token, true), BooleanClause.Occur.SHOULD);
        }
        valueQuery.add(inputTokenQuery, BooleanClause.Occur.SHOULD);

        combinedQuery.add(valueQuery, BooleanClause.Occur.MUST);

        TopDocs topDocs = this.searcher.search(combinedQuery, 50);
        return topDocs;
    }

    /**
     * create a combined query who searches for the input tokens separately (with QueryParser) and also the entire input
     * string (with TermQuery or FuzzyQuery).
     *
     * @param input
     * @param fuzzy this options decides whether output the fuzzy matches
     * @param allMatch this options means the result should be returned only if all tokens are found in the index
     * @return
     * @throws java.io.IOException
     */
    private Query createCombinedQueryFor(String input, boolean fuzzy, boolean allMatch) throws IOException {
        BooleanQuery combinedQuery = new BooleanQuery();
        Query wordTermQuery, synTermQuery, wordQuery, synQuery;
        wordTermQuery = getTermQuery(F_WORDTERM, input.toLowerCase(), fuzzy);
        synTermQuery = getTermQuery(F_SYNTERM, input.toLowerCase(), fuzzy);

        List<String> tokens = getTokensFromAnalyzer(input);
        wordQuery = new BooleanQuery();
        synQuery = new BooleanQuery();
        for (String token : tokens) {
            ((BooleanQuery) wordQuery).add(getTermQuery(F_WORD, token, fuzzy), allMatch ? BooleanClause.Occur.MUST
                    : BooleanClause.Occur.SHOULD);
            ((BooleanQuery) synQuery).add(getTermQuery(F_SYN, token, fuzzy), allMatch ? BooleanClause.Occur.MUST
                    : BooleanClause.Occur.SHOULD);
        }

        // increase importance of the reference word
        wordTermQuery.setBoost(WORD_TERM_BOOST);
        wordQuery.setBoost(WORD_BOOST);

        combinedQuery.add(wordTermQuery, BooleanClause.Occur.SHOULD);
        combinedQuery.add(synTermQuery, BooleanClause.Occur.SHOULD);
        combinedQuery.add(wordQuery, BooleanClause.Occur.SHOULD);
        combinedQuery.add(synQuery, BooleanClause.Occur.SHOULD);
        return combinedQuery;
    }

    /**
     * create a combined query who searches for the input tokens in order (with double quotes around the input) and also
     * the entire input string (with TermQuery).
     *
     * @param input
     * @return
     * @throws java.io.IOException
     */
    private Query createCombinedQueryForPartialMatch(String input) throws IOException {
        BooleanQuery combinedQuery = new BooleanQuery();
        Query wordTermQuery, synTermQuery, wordQuery, synQuery;
        wordTermQuery = getTermQuery(F_WORDTERM, input.toLowerCase(), false);
        synTermQuery = getTermQuery(F_SYNTERM, input.toLowerCase(), false);

        List<String> tokens = getTokensFromAnalyzer(input);
        wordQuery = new PhraseQuery();
        ((PhraseQuery) wordQuery).setSlop(slop);
        synQuery = new PhraseQuery();
        ((PhraseQuery) synQuery).setSlop(slop);
        for (String token : tokens) {
            token = token.toLowerCase();
            ((PhraseQuery) wordQuery).add(new Term(F_WORD, token));
            ((PhraseQuery) synQuery).add(new Term(F_SYN, token));
        }
        // increase importance of the reference word
        wordTermQuery.setBoost(WORD_TERM_BOOST);
        wordQuery.setBoost(WORD_BOOST);

        combinedQuery.add(wordTermQuery, BooleanClause.Occur.SHOULD);
        combinedQuery.add(synTermQuery, BooleanClause.Occur.SHOULD);
        combinedQuery.add(wordQuery, BooleanClause.Occur.SHOULD);
        combinedQuery.add(synQuery, BooleanClause.Occur.SHOULD);
        return combinedQuery;
    }

    /**
     * @param input
     * @return
     * @throws IOException
     */
    private Query createQueryForSemanticDictionaryMatch(String input) throws IOException {
        List<String> tokens = getTokensFromAnalyzer(input);
        // for dictionary search, ignore searching for input containing too many tokens
        if (tokens.size() > MAX_TOKEN_COUNT_FOR_SEMANTIC_MATCH) {
            return new TermQuery(new Term(F_SYNTERM, StringUtils.EMPTY));
        }
        Query synTermQuery = getTermQuery(F_SYNTERM, StringUtils.join(tokens, ' '), false);

        return synTermQuery;
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
                booleanQuery.add(getTermQuery(F_SYN, tokens.get(i), false), BooleanClause.Occur.SHOULD);
            }
        } else {
            for (String token : tokens) {
                booleanQuery.add(getTermQuery(F_SYN, token, false), BooleanClause.Occur.SHOULD);
            }
        }
        return booleanQuery;
    }

    /**
     * create a combined query who searches for the input tokens in order (with double quotes around the input) and also
     * the entire input string (with TermQuery).
     *
     * @param input
     * @return
     * @throws java.io.IOException
     */
    private Query createCombinedQueryForExactMatch(String input) throws IOException {
        BooleanQuery combinedQuery = new BooleanQuery();
        Query wordTermQuery, synTermQuery;
        wordTermQuery = getTermQuery(F_WORDTERM, input.toLowerCase(), false);
        synTermQuery = getTermQuery(F_SYNTERM, input.toLowerCase(), false);
        // increase importance of the reference word
        wordTermQuery.setBoost(WORD_TERM_BOOST);

        combinedQuery.add(wordTermQuery, BooleanClause.Occur.SHOULD);
        combinedQuery.add(synTermQuery, BooleanClause.Occur.SHOULD);
        return combinedQuery;
    }

    public void close() {
        try {
            searcher.getIndexReader().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOC root Comment method "getIndexSearcher".
     *
     * @return
     */
    public IndexSearcher getIndexSearcher() {
        return searcher;
    }

    public SynonymSearchMode getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(SynonymSearchMode searchMode) {
        this.searchMode = searchMode;
    }

    /**
     * @deprecated with new Lucene API, we should use maxEdits instead of minimumSimilarity for fuzzy matching
     * @param minimumSimilarity
     */
    @Deprecated
    public void setMinimumSimilarity(float minimumSimilarity) {
        this.minimumSimilarity = minimumSimilarity;
    }

    /**
     * @deprecated with new Lucene API, we should use maxEdits instead of minimumSimilarity for fuzzy matching
     * @param minimumSimilarity
     */
    @Deprecated
    public void setMinimumSimilarity(double minimumSimilarity) {
        this.minimumSimilarity = (float) minimumSimilarity;
    }

    public void setMaxEdits(int maxEdits) {
        this.maxEdits = maxEdits;
    }

    public float getMatchingThreshold() {
        return matchingThreshold;
    }

    public void setMatchingThreshold(float matchingThreshold) {
        this.matchingThreshold = matchingThreshold;
    }

    public void setMatchingThreshold(double matchingThreshold) {
        this.matchingThreshold = (float) matchingThreshold;
    }

    /**
     * 
     * @param input
     * @return a list of lower-case tokens which strips accents & punctuation
     * @throws IOException
     */
    public List<String> getTokensFromAnalyzer(String input) throws IOException {
        StandardTokenizer tokenStream = new StandardTokenizer(new StringReader(input));
        TokenStream result = new StandardFilter(tokenStream);
        result = new LowerCaseFilter(result);
        result = new ASCIIFoldingFilter(result);
        CharTermAttribute charTermAttribute = result.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        List<String> termList = new ArrayList<String>();
        while (result.incrementToken()) {
            String term = charTermAttribute.toString();
            termList.add(term);
        }
        result.close();
        return termList;
    }
}
