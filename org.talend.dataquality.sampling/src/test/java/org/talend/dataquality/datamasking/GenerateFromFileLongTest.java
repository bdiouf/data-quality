// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileLong;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateFromFileLongTest {

    private String path = GenerateFromFileHashIntegerTest.path;

    private GenerateFromFileLong gffl = new GenerateFromFileLong();

    @Test
    public void testGood() {
        gffl.parse(path, false, new RandomWrapper(42));
        assertEquals(9, gffl.generateMaskedRow(0L).longValue());
    }

    @Test
    public void testNull() {
        gffl.parse(gffl.EMPTY_STRING, false, new RandomWrapper(42));
        gffl.setKeepNull(true);
        assertEquals(0, gffl.generateMaskedRow(0L).longValue());
        assertEquals(null, gffl.generateMaskedRow(null));

    }

}
