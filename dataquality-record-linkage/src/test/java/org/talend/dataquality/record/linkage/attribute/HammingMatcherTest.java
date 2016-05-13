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

import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

import junit.framework.Assert;

/**
 * DOC jteuladedenantes // Your Name class global comment. Detailled comment
 */
public class HammingMatcherTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.HammingMatcher#getMatchingWeight(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetMatchingWeight() {

        HammingMatcher hammingMatcher = new HammingMatcher();
        String a = "malequa"; //$NON-NLS-1$
        double matchingWeight1 = hammingMatcher.getMatchingWeight(a, a);
        Assert.assertEquals("input strings are the same => result should be 1.", 1.0d, matchingWeight1);
        String b = "malequz"; //$NON-NLS-1$
        matchingWeight1 = hammingMatcher.getMatchingWeight(a, b);
        Assert.assertTrue("input strings are different => result should be between 0 and 1.",
                matchingWeight1 < 1 && matchingWeight1 > 0);

        b = "molequz";
        double matchingWeight2 = hammingMatcher.getMatchingWeight(a, b);
        Assert.assertTrue("input strings are the same => result should be between 0 and 1.",
                matchingWeight2 < 1 && matchingWeight2 > 0);

        Assert.assertTrue(
                "from ('molequz', 'malequz'), 'malequz' is more similar to 'malequa'  => matchingWeight2 < matchingWeight1.",
                matchingWeight2 < matchingWeight1);

        b = "molequ";
        matchingWeight1 = hammingMatcher.getMatchingWeight(a, b);
        Assert.assertTrue("strings " + a + " and " + b + " have not the same length => result should be 0.",
                0.0d == matchingWeight1);

    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.attribute.HammingMatcher#getMatchType()}.
     */
    @Test
    public void testGetMatchType() {

        Assert.assertEquals(AttributeMatcherType.HAMMING, new HammingMatcher().getMatchType());
        Assert.assertEquals("HAMMING", new HammingMatcher().getMatchType().name()); //$NON-NLS-1$
        Assert.assertEquals("Hamming", new HammingMatcher().getMatchType().getLabel()); //$NON-NLS-1$
        Assert.assertEquals("HAMMING", new HammingMatcher().getMatchType().toString()); //$NON-NLS-1$
    }

}
