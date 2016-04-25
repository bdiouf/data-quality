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

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 19 juin 2015. This class is called when the required function is GenerateBetween. It will
 * parse and set the parameters.
 *
 */
public abstract class GenerateBetween<T2> extends Function<T2> {

    protected int min = 0;

    protected int max = 0;

    protected void setBounds() {
        if (parameters.length == 2) {
            try {
                min = Integer.parseInt(parameters[0].trim());
                max = Integer.parseInt(parameters[1].trim());
            } catch (NumberFormatException e) {
                // Do nothing
            }
        }
        if (min > max) {
            int tmp = min;
            min = max;
            max = tmp;
        }
    }

    @Override
    protected abstract T2 doGenerateMaskedField(T2 t);
}
