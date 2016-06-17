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

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

public class DatePatternHelperTest {

    @Test
    public void testGuessPattern() throws ParseException {
        assertEquals("M/d/yy", DatePatternHelper.guessDatePattern("3/22/99"));
        assertEquals("EEEE, MMMM d, yyyy", DatePatternHelper.guessDatePattern("Sunday, May 16, 1999"));
        assertEquals("yyyy-MM-ddXXX", DatePatternHelper.guessDatePattern("1999-03-22+01:00"));
        assertEquals("yyyy-DDDXXX", DatePatternHelper.guessDatePattern("1999-81+01:00"));
    }

}
