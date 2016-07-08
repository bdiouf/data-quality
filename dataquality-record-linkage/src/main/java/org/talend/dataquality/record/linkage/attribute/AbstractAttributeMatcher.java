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

import java.io.Serializable;

import org.apache.commons.lang.NotImplementedException;

/**
 * Abstract matcher class for shared operations like blank string checking.
 */
public abstract class AbstractAttributeMatcher implements IAttributeMatcher, Serializable {

    private static final long serialVersionUID = -21096755142812677L;

    private NullOption nullOption = NullOption.nullMatchNull;

    private String attributeName = null;

    @Override
    public float getThreshold() {
        // TODO Default implementations
        throw new NotImplementedException();
    }

    @Override
    public double getWeight() {
        // TODO Default implementations
        throw new NotImplementedException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchingWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    public double getMatchingWeight(String str1, String str2) {
        switch (nullOption) {
        case nullMatchAll:
            if (isNullOrEmpty(str1) || isNullOrEmpty(str2)) {
                return 1.0;
            }
            break;
        case nullMatchNone:
            if (isNullOrEmpty(str1) || isNullOrEmpty(str2)) {
                return 0.0;
            }
            break;
        case nullMatchNull:
            boolean str1IsNull = isNullOrEmpty(str1);
            boolean str2IsNull = isNullOrEmpty(str2);
            if (str1IsNull && str2IsNull) { // both null => match
                return 1.0;
            } else if (str1IsNull || str2IsNull) { // only one null => non-match
                return 0.0;
            }
            break;
        default:
            break;
        }

        assert !isNullOrEmpty(str1) : "string should not be null or empty here"; //$NON-NLS-1$
        assert !isNullOrEmpty(str2) : "string should not be null or empty here"; //$NON-NLS-1$
        // TDQ-10366 qiongli,catch the Exception.
        double weight = 0;
        try {
            weight = getWeight(str1, str2);
        } catch (Exception exc) {
            // return 0 if it has exception.
            return 0;
        }
        return weight;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str); //$NON-NLS-1$
    }

    /**
     * Calculate matching weight using specified matcher.
     * 
     * @param record1 the first string
     * @param record2 the secord string
     * @return result between 0 and 1
     */
    protected abstract double getWeight(String record1, String record2);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#setNullOption(org.talend.dataquality.record
     * .linkage.attribute.IAttributeMatcher.NullOption)
     */
    @Override
    public void setNullOption(NullOption option) {
        this.nullOption = option;
    }

    @Override
    public NullOption getNullOption() {
        return nullOption;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getAttributeName()
     */
    @Override
    public String getAttributeName() {
        return attributeName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#setAttributeName(java.lang.String)
     */
    @Override
    public void setAttributeName(String name) {
        this.attributeName = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#setNullOption(java.lang.String)
     */
    @Override
    public void setNullOption(String option) {
        if (IAttributeMatcher.NullOption.nullMatchAll.name().equalsIgnoreCase(option)) {
            this.nullOption = IAttributeMatcher.NullOption.nullMatchAll;
        } else if (IAttributeMatcher.NullOption.nullMatchNone.name().equalsIgnoreCase(option)) {
            this.nullOption = IAttributeMatcher.NullOption.nullMatchNone;
        } else if (IAttributeMatcher.NullOption.nullMatchNull.name().equalsIgnoreCase(option)) {
            this.nullOption = IAttributeMatcher.NullOption.nullMatchNull;
        } else {
            this.nullOption = IAttributeMatcher.NullOption.nullMatchNull;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#isDummyMatcher()
     */
    @Deprecated
    @Override
    public boolean isDummyMatcher() {
        return false;
    }

}
