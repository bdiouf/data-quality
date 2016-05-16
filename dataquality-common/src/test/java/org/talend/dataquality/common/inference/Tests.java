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
package org.talend.dataquality.common.inference;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.dataquality.common.regex.FullwidthLatinLowercasedLettersTest;
import org.talend.dataquality.common.regex.FullwidthLatinNumbersTest;
import org.talend.dataquality.common.regex.FullwidthLatinUppercasedLettersTest;
import org.talend.dataquality.common.regex.HangulTest;
import org.talend.dataquality.common.regex.HiraganaSmallTest;
import org.talend.dataquality.common.regex.HiraganaTest;
import org.talend.dataquality.common.regex.KanjiTest;
import org.talend.dataquality.common.regex.KatakanaSmallTest;
import org.talend.dataquality.common.regex.KatakanaTest;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ ResizableListTest.class, KatakanaTest.class, KatakanaSmallTest.class, KanjiTest.class, HiraganaTest.class,
        HiraganaSmallTest.class, HangulTest.class, FullwidthLatinUppercasedLettersTest.class, FullwidthLatinNumbersTest.class,
        FullwidthLatinLowercasedLettersTest.class })
public class Tests {
}
