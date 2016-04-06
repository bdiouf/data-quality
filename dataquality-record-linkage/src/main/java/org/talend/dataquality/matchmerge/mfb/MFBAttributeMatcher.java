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
package org.talend.dataquality.matchmerge.mfb;

import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

public class MFBAttributeMatcher implements IAttributeMatcher {

    private final double threshold;

    private final SubString subString;

    private final IAttributeMatcher delegate;

    private final double weight;

    private MFBAttributeMatcher(IAttributeMatcher delegate, double weight, double threshold, SubString subString) {
        this.delegate = delegate;
        this.threshold = threshold;
        this.subString = subString;
        this.weight = weight;
    }

    public static MFBAttributeMatcher wrap(IAttributeMatcher matcher, double weight, double threshold, SubString subString) {
        return new MFBAttributeMatcher(matcher, weight, threshold, subString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#isDummyMatcher()
     */
    @Override
    public boolean isDummyMatcher() {
        return delegate.isDummyMatcher();
    }

    @Override
    public double getMatchingWeight(String str1, String str2) {
        if (subString.needSubStringOperation()) {
            str1 = str1.substring(subString.getBeginIndex(), subString.getEndIndex());
            str2 = str2.substring(subString.getBeginIndex(), subString.getEndIndex());
        }
        double matchingWeight = delegate.getMatchingWeight(str1, str2);
        if (matchingWeight < threshold) {
            return 0;
        }
        return matchingWeight;
    }

    @Override
    public AttributeMatcherType getMatchType() {
        return delegate.getMatchType();
    }

    @Override
    public void setNullOption(NullOption option) {
        delegate.setNullOption(option);
    }

    @Override
    public NullOption getNullOption() {
        return delegate.getNullOption();
    }

    @Override
    public void setNullOption(String option) {
        delegate.setNullOption(option);
    }

    @Override
    public String getAttributeName() {
        return delegate.getAttributeName();
    }

    @Override
    public void setAttributeName(String name) {
        delegate.setAttributeName(name);
    }

    @Override
    public float getThreshold() {
        return (float) threshold;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}
