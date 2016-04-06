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
 * created by scorreia on Jan 7, 2013 Detailled comment
 * 
 */
@SuppressWarnings("nls")
public class SoundexMatcherTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.SoundexMatcher#getWeight(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetWeight() {
        SoundexMatcher soundexMatcher = new SoundexMatcher();
        String a = "John"; //$NON-NLS-1$
        String b = "Jon"; //$NON-NLS-1$
        double matchingWeight = soundexMatcher.getMatchingWeight(a, b);
        Assert.assertEquals(1.0d, matchingWeight);

        a = "\n";
        b = "Hulme";
        matchingWeight = soundexMatcher.getMatchingWeight(a, b);
        Assert.assertTrue("input strings are not the same " + a + " and " + b, 0.0d == matchingWeight);

    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.attribute.SoundexMatcher#getMatchType()}.
     */
    @Test
    public void testGetMatchType() {
        Assert.assertEquals(AttributeMatcherType.SOUNDEX, new SoundexMatcher().getMatchType());
        Assert.assertEquals("SOUNDEX", new SoundexMatcher().getMatchType().name()); //$NON-NLS-1$
        Assert.assertEquals("Soundex", new SoundexMatcher().getMatchType().getLabel()); //$NON-NLS-1$
        Assert.assertEquals("SOUNDEX", new SoundexMatcher().getMatchType().toString()); //$NON-NLS-1$
    }

}
