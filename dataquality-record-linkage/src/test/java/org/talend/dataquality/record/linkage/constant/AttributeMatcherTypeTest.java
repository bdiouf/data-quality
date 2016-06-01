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
package org.talend.dataquality.record.linkage.constant;

import junit.framework.Assert;

import org.junit.Test;

/**
 * created by zshen on Nov 26, 2013 Detailled comment
 * 
 */
public class AttributeMatcherTypeTest {

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.constant.AttributeMatcherType#get(java.lang.String)}
     * . case1 all of normal case
     */
    @Test
    public void testGetCase1() {
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            AttributeMatcherType attributeMatcherTypeByName = AttributeMatcherType.get(type.name());
            AttributeMatcherType attributeMatcherTypeByComponentValue = AttributeMatcherType.get(type.getComponentValue());
            AttributeMatcherType attributeMatcherTypeByLName = AttributeMatcherType.get(type.name().toLowerCase());
            AttributeMatcherType attributeMatcherTypeByLComponentValue = AttributeMatcherType
                    .get(type.getComponentValue().toLowerCase());
            AttributeMatcherType attributeMatcherTypeByUName = AttributeMatcherType.get(type.name().toUpperCase());
            AttributeMatcherType attributeMatcherTypeByUComponentValue = AttributeMatcherType
                    .get(type.getComponentValue().toUpperCase());
            // Assert no one is null
            Assert.assertNotNull(attributeMatcherTypeByName);
            Assert.assertNotNull(attributeMatcherTypeByComponentValue);
            Assert.assertNotNull(attributeMatcherTypeByLName);
            Assert.assertNotNull(attributeMatcherTypeByLComponentValue);
            Assert.assertNotNull(attributeMatcherTypeByUName);
            Assert.assertNotNull(attributeMatcherTypeByUComponentValue);
            // all of return type is same
            Assert.assertTrue(attributeMatcherTypeByName == attributeMatcherTypeByComponentValue);
            Assert.assertTrue(attributeMatcherTypeByLName == attributeMatcherTypeByLComponentValue);
            Assert.assertTrue(attributeMatcherTypeByUName == attributeMatcherTypeByUComponentValue);
            Assert.assertTrue(attributeMatcherTypeByUName == attributeMatcherTypeByName);
            Assert.assertTrue(attributeMatcherTypeByUName == attributeMatcherTypeByLName);
        }
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.constant.AttributeMatcherType#get(java.lang.String)}
     * . case2 input is null or empty
     */
    @Test
    public void testGetCase2() {
        AttributeMatcherType attributeMatcherTypeByNull = AttributeMatcherType.get(null);
        Assert.assertNull(attributeMatcherTypeByNull);
        AttributeMatcherType attributeMatcherTypeByEmpty = AttributeMatcherType.get(""); //$NON-NLS-1$
        Assert.assertNull(attributeMatcherTypeByEmpty);

    }

}
