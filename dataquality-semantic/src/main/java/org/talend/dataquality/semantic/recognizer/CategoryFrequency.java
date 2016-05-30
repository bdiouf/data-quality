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
package org.talend.dataquality.semantic.recognizer;

import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class CategoryFrequency implements Comparable<CategoryFrequency> {

    ISubCategory category;

    float frequency;

    long count;

    /**
     * CategoryFrequency constructor from a category.
     * 
     * @param categ the category
     */
    public CategoryFrequency(ISubCategory categ) {
        this.category = categ;
    }

    public String getCategoryId() {
        return category != null ? category.getId() : "";
    }

    public String getCategoryName() {
        return category != null ? category.getName() : "";
    }

    public float getFrequency() {
        return frequency;
    }

    public long getCount() {
        return count;
    }

    public ISubCategory getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CategoryFrequency))
            return false;

        CategoryFrequency that = (CategoryFrequency) o;

        return !(this.getCategoryId() != null ? !this.getCategoryId().equals(that.getCategoryId())
                : that.getCategoryId() != null);

    }

    @Override
    public int hashCode() {
        return getCategoryId() != null ? getCategoryId().hashCode() : 0;
    }

    @Override
    public int compareTo(CategoryFrequency o) {
        if (this.getCount() > o.getCount()) {
            return 1;
        } else if (this.getCount() < o.getCount()) {
            return -1;
        } else {
            final SemanticCategoryEnum cat1 = SemanticCategoryEnum.getCategoryById(this.getCategoryId());
            final SemanticCategoryEnum cat2 = SemanticCategoryEnum.getCategoryById(o.getCategoryId());
            if (cat1 != null && cat2 != null) {
                return cat2.ordinal() - cat1.ordinal();
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "[Category: " + category + " Count: " + count + " Frequency: " + frequency + "]";
    }
}
