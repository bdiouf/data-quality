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
package org.talend.dataquality.semantic.recognizer;

import java.util.Collection;

/**
 * created by talend on 2015-07-28 Detailled comment.
 * 
 */
public interface CategoryRecognizer {

    void prepare();

    void reset();

    String[] process(String data);

    Collection<CategoryFrequency> getResult();

    void end();
}
