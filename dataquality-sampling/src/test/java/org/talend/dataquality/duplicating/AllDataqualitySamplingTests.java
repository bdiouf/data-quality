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
package org.talend.dataquality.duplicating;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.dataquality.sampling.ReservoirSamplerTest;

@RunWith(Suite.class)
@SuiteClasses({ DateChangerTest.class, FieldModifierTest.class, AbstractDuplicatorTest.class, ReservoirSamplerTest.class })
public class AllDataqualitySamplingTests {

    public static final long RANDOM_SEED = 12345678;

}
