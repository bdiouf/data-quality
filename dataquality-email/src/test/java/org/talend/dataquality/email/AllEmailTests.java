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
package org.talend.dataquality.email;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.dataquality.email.checkerImpl.CallbackMailServerCheckerImplTest;
import org.talend.dataquality.email.checkerImpl.ListDomainsCheckerImplTest;
import org.talend.dataquality.email.checkerImpl.LocalPartRegexCheckerImplTest;
import org.talend.dataquality.email.checkerImpl.RegularRegexCheckerImplTest;
import org.talend.dataquality.email.checkerImpl.TLDsCheckerImplTest;

/**
 * created by qiongli on 2014年12月30日 Detailled comment
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ EmailVerifyTest.class, CallbackMailServerCheckerImplTest.class, ListDomainsCheckerImplTest.class,
        LocalPartRegexCheckerImplTest.class, RegularRegexCheckerImplTest.class, TLDsCheckerImplTest.class })
public class AllEmailTests {

}
