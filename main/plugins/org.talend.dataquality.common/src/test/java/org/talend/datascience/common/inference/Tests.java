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
package org.talend.datascience.common.inference;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.datascience.common.inference.type.DataTypeAnalyzerTest;
import org.talend.datascience.common.inference.type.TypeInferenceUtilsTest;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ DataTypeAnalyzerTest.class, TypeInferenceUtilsTest.class })
public class Tests {
}
