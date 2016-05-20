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
 * @author jteuladedenantes
 * 
 * A Field is a set of values. According to a field, we can encode a value in a number or decode a number in a value of
 * the field
 */
public interface Field {

    /**
     * @return the number of different possible values in this field
     */
    public long getWidth();

    /**
     * @param str, the string to encode
     * @return the position number related to this string, -1 if str doesn't exist in this field
     */
    public Long encode(String str);

    /**
     * @param number, the number to decode
     * @return the string related to this number, "" if number is longer than the width
     */
    public String decode(long number);
}
