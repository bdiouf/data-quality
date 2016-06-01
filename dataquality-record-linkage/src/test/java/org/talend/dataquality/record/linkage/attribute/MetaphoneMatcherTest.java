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

import junit.framework.Assert;

import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * created by zhao on Apr 16, 2013 Detailled comment
 * 
 */
@SuppressWarnings("nls")
public class MetaphoneMatcherTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.MetaphoneMatcher#getWeight(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetWeight() {
        MetaphoneMatcher metaphoneMatcher = new MetaphoneMatcher();
        String a = "John"; //$NON-NLS-1$
        String b = "Jon"; //$NON-NLS-1$
        double matchingWeight = metaphoneMatcher.getMatchingWeight(a, b);
        Assert.assertEquals(1.0d, matchingWeight);
        a = "23";
        matchingWeight = metaphoneMatcher.getMatchingWeight(a, a);
        Assert.assertEquals("input strings are the same => result should be 1.", 1.0d, matchingWeight);
        b = "64";
        matchingWeight = metaphoneMatcher.getMatchingWeight(a, b);
        Assert.assertNotSame("input strings are the same => result should NOT be 1.", 1.0d, matchingWeight);

        // test long strings
        a = "JohnFit";
        b = "JohnFitzgeraldKennedy";
        matchingWeight = metaphoneMatcher.getMatchingWeight(a, b);
        Assert.assertTrue(
                "input strings are not the same but Metaphone should not be able to distinguish between " + a + " and " + b,
                1.0d == matchingWeight);

        a = "\n";
        b = "Hulme";
        matchingWeight = metaphoneMatcher.getMatchingWeight(a, b);
        Assert.assertTrue("input strings are not the same " + a + " and " + b, 0.0d == matchingWeight);

    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.attribute.MetaphoneMatcher#getMatchType()}.
     */
    @Test
    public void testGetMatchType() {

        Assert.assertEquals(AttributeMatcherType.METAPHONE, new MetaphoneMatcher().getMatchType());
        Assert.assertEquals("METAPHONE", new MetaphoneMatcher().getMatchType().name()); //$NON-NLS-1$
        Assert.assertEquals("Metaphone", new MetaphoneMatcher().getMatchType().getLabel()); //$NON-NLS-1$
        Assert.assertEquals("METAPHONE", new MetaphoneMatcher().getMatchType().toString()); //$NON-NLS-1$
    }

}
