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
package org.talend.dataquality.common.regex;

import org.junit.Assert;
import org.junit.Test;

/**
 * DOC talend class global comment. Detailled comment
 */
public class HiraganaSmallTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}. test1
     * other case
     */
    @Test
    public void testHandleRequestCase1() {
        HiraganaSmall hiraganaSmall = new HiraganaSmall();
        String handleRequest = hiraganaSmall.handleRequest("もしtalendは第二の話、そして誰が最強だ？"); //$NON-NLS-1$
        Assert.assertEquals("もしtalendは第二の話、そして誰が最強だ？", handleRequest); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.statistics.frequency.regex.ChainResponsibilityHandler#handleRequest(java.lang.String)}. test2
     * small case
     */
    @Test
    public void testHandleRequestCase2() {
        HiraganaSmall hiraganaSmall = new HiraganaSmall();
        String handleRequest = hiraganaSmall.handleRequest(
                "........ぁ,...... ぃ ,.... ぅ ,.....ぇ,..... ぉ,.... っ,.... ゃ ,..... ゅ,.... ょ,.... ゎ ,..... ゕ ,..... ゖ"); //$NON-NLS-1$
        Assert.assertEquals("........h,...... h ,.... h ,.....h,..... h,.... h,.... h ,..... h,.... h,.... h ,..... h ,..... h", //$NON-NLS-1$
                handleRequest);
    }

}
