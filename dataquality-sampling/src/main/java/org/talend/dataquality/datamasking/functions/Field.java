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
package org.talend.dataquality.datamasking.functions;

/**
 * DOC jteuladedenantes class global comment. Detailled comment
 */
public interface Field {

    public long getWidth();

    /**
     * DOC jteuladedenantes Comment method "encode".
     * 
     * @param str
     * @return -1 if we can't encode the string
     */
    public Long encode(String str);

    public String decode(long number);
}
