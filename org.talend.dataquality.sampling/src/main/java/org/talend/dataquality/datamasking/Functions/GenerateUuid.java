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
import java.util.UUID;

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 22 juin 2015. This function will generate a UUID using Java's UUID.randomUUID() function.
 *
 */
public class GenerateUuid extends Function<String> implements Serializable {

    private static final long serialVersionUID = 7525227345231199052L;

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            return UUID.randomUUID().toString();
        }
    }
}
