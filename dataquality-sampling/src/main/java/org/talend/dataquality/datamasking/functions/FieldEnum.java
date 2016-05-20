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

import java.util.List;

/**
 * DOC jteuladedenantes class global comment. Detailled comment
 */
public class FieldEnum implements Field {

    /**
     * the exhaustive list of values
     */
    private List<String> enumValues;

    public FieldEnum(List<String> enumValues) {
        super();
        this.enumValues = enumValues;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Field#getWidth()
     */
    @Override
    public long getWidth() {
        return this.enumValues.size();
    }

    @Override
    public Long encode(String str) {
        return (long) this.enumValues.indexOf(str);
    }

    @Override
    public String decode(long number) {
        return this.enumValues.get((int) number);
    }

}
