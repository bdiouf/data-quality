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
package org.talend.dataquality.record.linkage.attribute;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.QGramTokenizer;

/**
 * @author scorreia
 * 
 * this matcher computes the Q-grams distances. By default q=3.
 */
// This code has been inspired by SimMetrics.
public class QGramsMatcher extends AbstractAttributeMatcher {

    private QGramTokenizer tokenizer = new QGramTokenizer();

    /**
     * q the length of the q-gram. By default, it is set to 3.
     */
    private int q = 3;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.Q_GRAMS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    public double getWeight(String string1, String string2) {
        final List<String> str1Tokens = tokenizer.tokenizeToArrayList(string1, q);
        final List<String> str2Tokens = tokenizer.tokenizeToArrayList(string2, q);

        final int maxQGramsMatching = str1Tokens.size() + str2Tokens.size();

        // return
        if (maxQGramsMatching == 0) {
            return 0.0f;
        } else {
            return (maxQGramsMatching - getUnNormalisedSimilarity(str1Tokens, str2Tokens)) / maxQGramsMatching;
        }
    }

    /**
     * gets the un-normalised similarity measure of the metric for the given strings.
     * 
     * @param str1Tokens
     * @param str2Tokens
     * 
     * @return returns the score of the similarity measure (un-normalised)
     */
    private float getUnNormalisedSimilarity(final List<String> str1Tokens, final List<String> str2Tokens) {

        final Set<String> allTokens = new HashSet<String>();
        allTokens.addAll(str1Tokens);
        allTokens.addAll(str2Tokens);

        int difference = 0;
        for (String token : allTokens) {
            int matchingQGrams1 = 0;
            for (String str1Token : str1Tokens) {
                if (str1Token.equals(token)) {
                    matchingQGrams1++;
                }
            }
            int matchingQGrams2 = 0;
            for (String str2Token : str2Tokens) {
                if (str2Token.equals(token)) {
                    matchingQGrams2++;
                }
            }
            difference += Math.abs(matchingQGrams1 - matchingQGrams2);
        }

        // return
        return difference;
    }

    /**
     * Sets the q.
     * 
     * @param q the q to set
     */
    public void setQ(int q) {
        this.q = q;
    }

}
