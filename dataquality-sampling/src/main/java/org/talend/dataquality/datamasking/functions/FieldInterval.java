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
public class FieldInterval implements Field {

    private long minInterval;

    private long maxInterval;

    public FieldInterval(long minInterval, long maxInterval) {
        super();
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Field#getWidth()
     */
    @Override
    public long getWidth() {
        return maxInterval - minInterval + 1;
    }

    @Override
    public Long encode(String str) {
        Long longStr;
        try {
            longStr = Long.valueOf(str);
            if (longStr < this.minInterval || longStr > this.maxInterval)
                return -1L;
        } catch (NumberFormatException e) {
            return -1L;
        }
        return longStr - this.minInterval;

    }

    @Override
    public String decode(long number) {

        String res = String.valueOf(number + this.minInterval);
        int size = String.valueOf(this.maxInterval).length();
        while (res.length() < size)
            res = "0" + res;
        return res;
    }

}
