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

public abstract class QualityAnalyzer<T, QT> implements Analyzer<T> {

    private static final long serialVersionUID = 6214486020243062215L;

    protected boolean isStoreInvalidValues = true;

    protected QT types;

    /**
     * @deprecated use {@link #addParameters(java.util.HashMap)} instead with
     * {@link Parameters.QualityParam#STORE_VALUE}
     * @param isStoreInvalidValues
     */
    public void setStoreInvalidValues(boolean isStoreInvalidValues) {
        this.isStoreInvalidValues = isStoreInvalidValues;
    }

    public QT getTypes() {
        return types;
    }
}
