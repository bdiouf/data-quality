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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 19 juin 2015. This function will look for a ’@’ and replace all characters before by ’X’ and
 * leave the rest unchanged. If there is no ’@’ in the input, the generated data will be a serie of ’X’.
 *
 */
public class MaskEmail extends GenerateFromFile<String> implements Serializable {

    private static final long serialVersionUID = 3520390903566492525L;

    List<String> keys = new ArrayList<>();

    private void addKeys(String[] para) {
        if (para.length > 0) {
            try {
                keys = KeysLoader.loadKeys(para[0]);
            } catch (IOException | NullPointerException e) {
                for (String element : para) {
                    keys.add(element.trim());
                }
            }
        }
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues, RandomWrapper rand) {
        super.parse(extraParameter, keepNullValues, rand);
        addKeys(parameters);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str != null && !EMPTY_STRING.equals(str)) {
            StringBuilder sb = new StringBuilder(str);
            int count = str.lastIndexOf('@');
            if (count == -1) {
                count = str.length();
            }
            if (keys.size() == 1 && keys.get(0).equals(EMPTY_STRING) || keys.size() == 0) {
                for (int i = 0; i < count; ++i) {
                    sb.setCharAt(i, 'X');
                }
            } else {
                sb.replace(0, count, keys.get(rnd.nextInt(keys.size())));
            }
            return sb.toString();
        } else {
            return EMPTY_STRING;
        }
    }
}
