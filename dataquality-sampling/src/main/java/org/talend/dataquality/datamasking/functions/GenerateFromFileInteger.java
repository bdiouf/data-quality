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

import java.util.ArrayList;
import java.util.List;

/**
 * created by jgonzalez on 19 juin 2015. See GenerateFromFile.
 *
 */
public class GenerateFromFileInteger extends GenerateFromFile<Integer> {

    private static final long serialVersionUID = 1896675901231975008L;

    private List<Integer> IntegerTokens = new ArrayList<>();

    @Override
    protected Integer doGenerateMaskedField(Integer j) {
        for (int i = 0; i < substituteList.size(); ++i) {
            int tmp = 0;
            try {
                tmp = Integer.parseInt(substituteList.get(i));
                IntegerTokens.add(tmp);
            } catch (NumberFormatException e) {
                IntegerTokens.add(0);
            }
        }
        if (IntegerTokens.size() > 0) {
            return IntegerTokens.get(rnd.nextInt(IntegerTokens.size()));
        } else {
            return 0;
        }
    }
}
