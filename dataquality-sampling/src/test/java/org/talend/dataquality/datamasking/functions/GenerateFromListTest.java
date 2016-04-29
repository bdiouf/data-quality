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
package org.talend.dataquality.datamasking.functions;

import org.junit.Assert;
import org.junit.Test;


/**
 * zshen class global comment. Detailled comment
 */
public class GenerateFromListTest {

    /**
     * Test method for {@link org.talend.dataquality.datamasking.functions.GenerateFromList#init()}.
     */
    @Test
    public void testInit() {
        GenerateFromList<String> gfls = new GenerateFromList<String>() {

            private static final long serialVersionUID = -1885973696653057702L;

            @Override
            protected String doGenerateMaskedField(String t) {
                return null;
            }

        };

        gfls.parse("commune.txt", true, null); //$NON-NLS-1$
        gfls.init();
        Assert.assertNotNull(gfls.StringTokens);
        Assert.assertEquals(592, gfls.StringTokens.size());
        Assert.assertEquals("Abbans-Dessous", gfls.StringTokens.get(0)); //$NON-NLS-1$
        Assert.assertEquals("Abbans-Dessus", gfls.StringTokens.get(1)); //$NON-NLS-1$
        Assert.assertEquals("Abbenans", gfls.StringTokens.get(2)); //$NON-NLS-1$

        gfls.parse("company.txt", true, null); //$NON-NLS-1$
        gfls.init();
        Assert.assertNotNull(gfls.StringTokens);
        Assert.assertEquals(100, gfls.StringTokens.size());
        Assert.assertEquals("Apple", gfls.StringTokens.get(0)); //$NON-NLS-1$
        Assert.assertEquals("Google", gfls.StringTokens.get(1)); //$NON-NLS-1$
        Assert.assertEquals("Samsung", gfls.StringTokens.get(2)); //$NON-NLS-1$

        gfls.parse("firstName.txt", true, null); //$NON-NLS-1$
        gfls.init();
        Assert.assertNotNull(gfls.StringTokens);
        Assert.assertEquals(200, gfls.StringTokens.size());
        Assert.assertEquals("Jacob", gfls.StringTokens.get(0)); //$NON-NLS-1$
        Assert.assertEquals("Emily", gfls.StringTokens.get(1)); //$NON-NLS-1$
        Assert.assertEquals("Michael", gfls.StringTokens.get(2)); //$NON-NLS-1$

        gfls.parse("jobTitle.txt", true, null); //$NON-NLS-1$
        gfls.init();
        Assert.assertNotNull(gfls.StringTokens);
        Assert.assertEquals(1186, gfls.StringTokens.size());
        Assert.assertEquals("Able Seamen Jobs", gfls.StringTokens.get(0)); //$NON-NLS-1$
        Assert.assertEquals("Account Manager Jobs", gfls.StringTokens.get(1)); //$NON-NLS-1$
        Assert.assertEquals("Accountant Jobs", gfls.StringTokens.get(2)); //$NON-NLS-1$

        gfls.parse("lastName.txt", true, null); //$NON-NLS-1$
        gfls.init();
        Assert.assertNotNull(gfls.StringTokens);
        Assert.assertEquals(65, gfls.StringTokens.size());
        Assert.assertEquals("Alexander", gfls.StringTokens.get(0)); //$NON-NLS-1$
        Assert.assertEquals("Ali", gfls.StringTokens.get(1)); //$NON-NLS-1$
        Assert.assertEquals("Alvarez", gfls.StringTokens.get(2)); //$NON-NLS-1$

        gfls.parse("organization.txt", true, null); //$NON-NLS-1$
        gfls.init();
        Assert.assertNotNull(gfls.StringTokens);
        Assert.assertEquals(100, gfls.StringTokens.size());
        Assert.assertEquals("Human Rights Watch (HRW)", gfls.StringTokens.get(0)); //$NON-NLS-1$
        Assert.assertEquals("Museum of Modern Art", gfls.StringTokens.get(1)); //$NON-NLS-1$
        Assert.assertEquals("United Nations Children's Fund (UNICEF)", gfls.StringTokens.get(2)); //$NON-NLS-1$
    }

}
