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
package org.talend.dataquality.datamasking.Functions;

import java.io.Serializable;

import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 18 juin 2015. This function will return a string between the two given as parameters.
 *
 */
public class GenerateBetweenString extends GenerateBetween<String> implements Serializable {

    private static final long serialVersionUID = 5649899736691218870L;

    @Override
    public void parse(String extraParameter, boolean keepNullValues, RandomWrapper rand) {
        super.parse(extraParameter, keepNullValues, rand);
        super.setBounds();
    }

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            int result = rnd.nextInt((max - min) + 1) + min;
            return result == 0 ? EMPTY_STRING : String.valueOf(result);
        }
    }
}