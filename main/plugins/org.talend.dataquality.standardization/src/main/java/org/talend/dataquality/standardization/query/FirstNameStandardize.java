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
package org.talend.dataquality.standardization.query;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.talend.dataquality.standardization.constant.PluginConstant;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class FirstNameStandardize {

    /**
     * According to levenshtein algorithm, the following value means a distance of 1 is allowed to match a first name
     * containing 4 to 7 letters, while for those with 8 to 12 letters, 2 erroneous letters are allowed. a first name
     * between 12 and 15 letters, allows a distance of 3, and so on ...
     * <p>
     * For the first names with minus sign inside, ex: Jean-Baptiste, the matching is done for Jean and Baptiste
     * separately, and the number of tokens is also considered by Lucene.
     */
    @Deprecated
    private static final float MATCHING_SIMILARITY = 0.74f;

    private int maxEdits = 1;

    private Analyzer analyzer;

    private IndexSearcher searcher;

    private int hitsPerPage;

    public FirstNameStandardize(IndexSearcher indexSearcher, Analyzer analyzer, int hitsPerPage) throws IOException {
        assert analyzer != null;
        assert indexSearcher != null;
        this.analyzer = analyzer;
        this.searcher = indexSearcher;
        this.hitsPerPage = hitsPerPage;
    }

    private ScoreDoc[] standardize(String input, boolean fuzzyQuery) throws ParseException, IOException {

        if (input == null || input.length() == 0) {
            return new ScoreDoc[0];
        }
        // MOD sizhaoliu 2012-7-4 TDQ-1576 tFirstnameMatch returns no firstname when several matches exist
        // Do not use doc collector which contains an inner sort.
        ScoreDoc[] matches = null;
        if (fuzzyQuery) {
            try {
                matches = getFuzzySearch(input).scoreDocs;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Query q = new QueryParser(PluginConstant.FIRST_NAME_STANDARDIZE_NAME, analyzer).parse(input);
            matches = searcher.search(q, 10).scoreDocs;
        }
        return matches;
    }

    public void getFuzzySearch(String input, TopDocsCollector<?> collector) throws Exception {
        Query q = new FuzzyQuery(new Term(PluginConstant.FIRST_NAME_STANDARDIZE_NAME, input));
        Query qalias = new FuzzyQuery(new Term(PluginConstant.FIRST_NAME_STANDARDIZE_ALIAS, input));
        BooleanQuery combinedQuery = new BooleanQuery();
        combinedQuery.add(q, BooleanClause.Occur.SHOULD);
        combinedQuery.add(qalias, BooleanClause.Occur.SHOULD);
        searcher.search(combinedQuery, collector);
    }

    public TopDocs getFuzzySearch(String input) throws Exception {
        // MOD sizhaoliu 2012-7-4 TDQ-1576 tFirstnameMatch returns no firstname when several matches exist
        // The 2 letter prefix requires exact match while the word to search may not be lowercased as in the index.
        // Extracted and documented MATCHING_SIMILARITY constant.
        Query q = new FuzzyQuery(new Term("name", input.toLowerCase()), maxEdits);//$NON-NLS-1$
        TopDocs matches = searcher.search(q, 10);
        return matches;
    }

    // FIXME this variable is only for tests
    public static final boolean SORT_WITH_COUNT = true;

    public ScoreDoc[] standardize(String inputName, Map<String, String> information2value, boolean fuzzySearch) throws Exception {
        if (inputName == null || inputName.length() == 0) {
            return new ScoreDoc[0];
        }
        // // DOC set get county and gender fields value
        String countryText = null;
        String genderText = null;
        Set<String> indexKinds = information2value.keySet();
        for (String indexKind : indexKinds) {
            if (indexKind.equals(PluginConstant.FIRST_NAME_STANDARDIZE_COUNTRY)) {
                countryText = information2value.get(indexKind);
            } else if (indexKind.equals(PluginConstant.FIRST_NAME_STANDARDIZE_GENDER)) {
                genderText = information2value.get(indexKind);
            }
        }
        // create all Term for query
        Term termName = new Term(PluginConstant.FIRST_NAME_STANDARDIZE_NAME, inputName);
        Term termCountry = (countryText == null) ? null : new Term(PluginConstant.FIRST_NAME_STANDARDIZE_COUNTRY, countryText);
        Term ternGender = (genderText == null) ? null : new Term(PluginConstant.FIRST_NAME_STANDARDIZE_GENDER, genderText);
        // many field to query for reposity
        BooleanQuery combinedQuery = new BooleanQuery();
        Query nameQuery = new QueryParser(PluginConstant.FIRST_NAME_STANDARDIZE_NAME, analyzer).parse(inputName);
        combinedQuery.add(nameQuery, BooleanClause.Occur.SHOULD);

        if (countryText != null && !countryText.equals("")) {//$NON-NLS-1$
            Query countryQuery = new QueryParser(PluginConstant.FIRST_NAME_STANDARDIZE_COUNTRY, analyzer).parse(countryText);
            combinedQuery.add(countryQuery, BooleanClause.Occur.SHOULD);
        }
        if (genderText != null && !genderText.equals("")) {//$NON-NLS-1$
            Query genderQuery = new QueryParser(PluginConstant.FIRST_NAME_STANDARDIZE_GENDER, analyzer).parse(genderText);
            combinedQuery.add(genderQuery, BooleanClause.Occur.SHOULD);
        }

        TopDocs matches = searcher.search(combinedQuery, 10);

        if (fuzzySearch) {
            BooleanQuery combinedFuzzyQuery = new BooleanQuery();
            Query name = new FuzzyQuery(termName);
            combinedQuery.add(name, BooleanClause.Occur.SHOULD);
            Query country = null;
            Query gender = null;
            if (countryText != null && !countryText.equals("")) {//$NON-NLS-1$
                country = new FuzzyQuery(termCountry);
                combinedQuery.add(country, BooleanClause.Occur.SHOULD);
                if (genderText != null && !genderText.equals("")) {//$NON-NLS-1$
                    gender = new FuzzyQuery(ternGender);
                    combinedQuery.add(gender, BooleanClause.Occur.SHOULD);
                }
            }

            matches = searcher.search(combinedFuzzyQuery, 10);
        }
        return matches.scoreDocs;
    }

    @SuppressWarnings("unused")
    private TopDocsCollector<?> createTopDocsCollector() throws IOException {
        // TODO the goal is to sort the result in descending order according to the "count" field
        if (SORT_WITH_COUNT) { // TODO enable this when it works correctly
            SortField sortfield = new SortField(PluginConstant.FIRST_NAME_STANDARDIZE_COUNT, SortField.Type.INT);
            Sort sort = new Sort(sortfield);
            // results are sorted according to a score and then to the count value
            return TopFieldCollector.create(sort, hitsPerPage, false, false, false, false);
        } else {
            return TopScoreDocCollector.create(hitsPerPage, false);
        }
    }

    /**
     * Method "replaceName".
     * 
     * @param input a first name
     * @return the standardized first name
     * @throws ParseException
     * @throws IOException
     */
    public String replaceName(String inputName, boolean fuzzyQuery) throws IOException {

        ScoreDoc[] results = null;
        try {
            results = standardize(inputName, fuzzyQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (results == null || results.length == 0) ? "" : searcher.doc(results[0].doc).get("name");//$NON-NLS-1$ //$NON-NLS-2$
    }

    public String replaceNameWithCountryGenderInfo(String inputName, String inputCountry, String inputGender, boolean fuzzyQuery)
            throws Exception {
        Map<String, String> indexFields = new HashMap<String, String>();
        indexFields.put("country", inputCountry);//$NON-NLS-1$
        indexFields.put("gender", inputGender);//$NON-NLS-1$
        ScoreDoc[] results = standardize(inputName, indexFields, fuzzyQuery);
        return results.length == 0 ? "" : searcher.doc(results[0].doc).get("name");//$NON-NLS-1$ //$NON-NLS-2$
    }

    public String replaceNameWithCountryInfo(String inputName, String inputCountry, boolean fuzzyQuery) throws Exception {
        Map<String, String> indexFields = new HashMap<String, String>();
        indexFields.put("country", inputCountry);//$NON-NLS-1$
        ScoreDoc[] results = standardize(inputName, indexFields, fuzzyQuery);
        return results.length == 0 ? "" : searcher.doc(results[0].doc).get("name");//$NON-NLS-1$ //$NON-NLS-2$
    }

    public String replaceNameWithGenderInfo(String inputName, String inputGender, boolean fuzzyQuery) throws Exception {
        Map<String, String> indexFields = new HashMap<String, String>();
        indexFields.put("gender", inputGender);//$NON-NLS-1$
        ScoreDoc[] results;

        results = standardize(inputName, indexFields, fuzzyQuery);

        return results.length == 0 ? "" : searcher.doc(results[0].doc).get("name");//$NON-NLS-1$ //$NON-NLS-2$
    }

    public void setMaxEdits(int maxEdits) {
        this.maxEdits = maxEdits;
    }

}
