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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class TokenizedResolutionMethodTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod#getTypeByValue(java.lang.String)} . case1
     * all of normal case
     */
    @Test
    public void testGetTypeByValueStringCase1() {
        for (TokenizedResolutionMethod type : TokenizedResolutionMethod.values()) {
            TokenizedResolutionMethod TokenizedResolutionMethodByName = TokenizedResolutionMethod.getTypeByValue(type.name());
            TokenizedResolutionMethod TokenizedResolutionMethodByComponentValue = TokenizedResolutionMethod
                    .getTypeByValue(type.getComponentValue());
            TokenizedResolutionMethod TokenizedResolutionMethodByLName = TokenizedResolutionMethod
                    .getTypeByValue(type.name().toLowerCase());
            TokenizedResolutionMethod TokenizedResolutionMethodByLComponentValue = TokenizedResolutionMethod
                    .getTypeByValue(type.getComponentValue().toLowerCase());
            TokenizedResolutionMethod TokenizedResolutionMethodByUName = TokenizedResolutionMethod
                    .getTypeByValue(type.name().toUpperCase());
            TokenizedResolutionMethod TokenizedResolutionMethodByUComponentValue = TokenizedResolutionMethod
                    .getTypeByValue(type.getComponentValue().toUpperCase());
            if (TokenizedResolutionMethod.NO == type) {
                assertNotNull(TokenizedResolutionMethodByName);
                assertNotNull(TokenizedResolutionMethodByLName);
                assertNotNull(TokenizedResolutionMethodByUName);
            } else {
                assertNull(TokenizedResolutionMethodByName);
                assertNull(TokenizedResolutionMethodByLName);
                assertNull(TokenizedResolutionMethodByUName);
            }
            assertNotNull(TokenizedResolutionMethodByComponentValue);
            assertNotNull(TokenizedResolutionMethodByLComponentValue);
            assertNotNull(TokenizedResolutionMethodByUComponentValue);

        }
    }

    /**
     * Test method for
     * {@org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod#getTypeByValue(java.lang.String)
     * 
     * 
     * 
     * 
     * 
     * } . case2
     * input is null empty or some other word
     */
    @Test
    public void testGetCase2() {
        TokenizedResolutionMethod TokenizedResolutionMethodByNull = TokenizedResolutionMethod.getTypeByValue(null);
        assertNull(TokenizedResolutionMethodByNull);
        TokenizedResolutionMethod TokenizedResolutionMethodByEmpty = TokenizedResolutionMethod.getTypeByValue(""); //$NON-NLS-1$
        assertNull(TokenizedResolutionMethodByEmpty);
        TokenizedResolutionMethod TokenizedResolutionMethodByOtherWord = TokenizedResolutionMethod.getTypeByValue("111111"); //$NON-NLS-1$
        assertNull(TokenizedResolutionMethodByOtherWord);
    }

}
