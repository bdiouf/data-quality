package org.talend.dataquality.record.linkage;

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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcherTest;
import org.talend.dataquality.record.linkage.attribute.DoubleMetaphoneMatcherTest;
import org.talend.dataquality.record.linkage.attribute.LevenshteinMatcherTest;
import org.talend.dataquality.record.linkage.attribute.MetaphoneMatcherTest;
import org.talend.dataquality.record.linkage.attribute.SoundexMatcherTest;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherTypeTest;
import org.talend.dataquality.record.linkage.grouping.AbstractRecordGroupingTest;
import org.talend.dataquality.record.linkage.grouping.SwooshRecordGroupingTest;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcherTest;
import org.talend.dataquality.record.linkage.record.SimpleVSRRecordMatcherTest;
import org.talend.dataquality.record.linkage.utils.AlgorithmSwitchTest;
import org.talend.dataquality.record.linkage.utils.QGramTokenizerTest;
import org.talend.windowkey.AlgoBoxTest;
import org.talend.windowkey.FingerprintKeyerTest;
import org.talend.windowkey.NGramFingerprintKeyerTest;

/**
 * DOC yyin class global comment. Detailled comment
 */
@RunWith(Suite.class)
@SuiteClasses({ AbstractAttributeMatcherTest.class, DoubleMetaphoneMatcherTest.class, LevenshteinMatcherTest.class,
        MetaphoneMatcherTest.class, SoundexMatcherTest.class, AttributeMatcherTypeTest.class, AbstractRecordGroupingTest.class,
        CombinedRecordMatcherTest.class, SimpleVSRRecordMatcherTest.class, AlgorithmSwitchTest.class, QGramTokenizerTest.class,
        AlgoBoxTest.class, FingerprintKeyerTest.class, NGramFingerprintKeyerTest.class, SwooshRecordGroupingTest.class })
public class AllRecordLinkageTests {

}
