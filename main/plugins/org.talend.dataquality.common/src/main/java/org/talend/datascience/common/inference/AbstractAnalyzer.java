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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * Abstract analyzer handling generic functions of all sort of analyzers.
 * 
 * @since 1.3.3
 * @author zhao
 * @param <T>
 *
 */
public abstract class AbstractAnalyzer<T> implements Analyzer<T> {

    private static final long serialVersionUID = -6035118696501272997L;

    protected Map<String, String> parameters = new ConcurrentHashMap<String, String>();

    @Override
    public void addParameters(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }

    @Override
    public void setParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    @Override
    public void removeParameter(String key) {
        parameters.remove(key);
    }


    @Override
    public void removeParameters(Set<String> keys) {
        for (String key : keys) {
            parameters.remove(key);
        }
    }
}
