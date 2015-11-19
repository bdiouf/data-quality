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
package org.talend.datascience.common.parameter;

/**
 * Parameter keys which is used from API client passing parameters.
 * 
 * @since 1.3.3
 * @author mzhao
 *
 */
public class Parameters {

    /**
     * Parameters regarding date type.<br>
     * LOCALE is value from {@link java.util.LOCALE} language and country like "zh_CN,fr_FR,en_US, ja_JP"
     * 
     * @author mzhao
     *
     */
    public static enum DateParam {
        DATE_PATTERN,
        LOCALE
    }

    /**
     * Parameters regarding field qualities.
     * 
     * @author mzhao
     *
     */
    public static enum QualityParam {
        STORE_VALUE
    }
}
