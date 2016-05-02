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
package org.talend.dataquality.datamasking.semantic;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.duplicating.AllDataqualitySamplingTests;

/**
 * zshen class global comment. Detailled comment
 */
public class GenerateFromFileStringProvidedTest {

    /**
     * Test method for {@link org.talend.dataquality.datamasking.functions.GenerateFromList#init()}.
     */
    @Test
    public void testInit() {
        GenerateFromFileStringProvided gfls = new GenerateFromFileStringProvided();

        gfls.parse(MaskableCategoryEnum.FR_COMMUNE.getParameter(), true, new Random(AllDataqualitySamplingTests.RANDOM_SEED)); // $NON-NLS-1$
        gfls.init();
        Assert.assertEquals("Rognon", gfls.generateMaskedRow("A")); //$NON-NLS-1$
        Assert.assertEquals("Colombier-Fontaine", gfls.generateMaskedRow("A")); //$NON-NLS-1$
        Assert.assertEquals("Chatillon-le-Duc", gfls.generateMaskedRow("A")); //$NON-NLS-1$

        gfls.parse(MaskableCategoryEnum.COMPANY.getParameter(), true, null); // $NON-NLS-1$
        gfls.init();

        gfls.parse(MaskableCategoryEnum.FIRST_NAME.getParameter(), true, null); // $NON-NLS-1$
        gfls.init();

        gfls.parse(MaskableCategoryEnum.LAST_NAME.getParameter(), true, null); // $NON-NLS-1$
        gfls.init();

        gfls.parse(MaskableCategoryEnum.JOB_TITLE.getParameter(), true, null); // $NON-NLS-1$
        gfls.init();

        gfls.parse(MaskableCategoryEnum.ORGANIZATION.getParameter(), true, null); // $NON-NLS-1$
        gfls.init();
    }

}
