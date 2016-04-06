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
package org.talend.dataquality.semantic.classifier.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.ISubCategoryClassifier;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.index.Index;

/**
 * Created by sizhaoliu on 27/03/15.
 */
public class DataDictFieldClassifier implements ISubCategoryClassifier {

    private Index dictionary;

    private Index keyword;

    public DataDictFieldClassifier(Index dictionary, Index keyword) {
        this.dictionary = dictionary;
        this.keyword = keyword;

    }

    @Override
    public Set<String> classify(String data) {
        StringTokenizer t = new StringTokenizer(data, " ");
        final int tokenCount = t.countTokens();

        HashSet<String> result = new HashSet<String>();
        // if it's a valid syntactic data --> search in DD
        if (tokenCount < 3) {
            result.addAll(dictionary.findCategories(data));
        } else {
            result.addAll(dictionary.findCategories(data));
            result.addAll(keyword.findCategories(data));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.semantic.classifier.ISubCategoryClassifier#classifyIntoCategories(java.lang.String)
     */
    @Deprecated
    @Override
    public Set<ISubCategory> classifyIntoCategories(String data) {
        Set<ISubCategory> categories = new HashSet<>();
        for (String id : classify(data)) {
            categories.add(new UserDefinedCategory(id, SemanticCategoryEnum.valueOf(id)));
        }

        return categories;
    }

}
