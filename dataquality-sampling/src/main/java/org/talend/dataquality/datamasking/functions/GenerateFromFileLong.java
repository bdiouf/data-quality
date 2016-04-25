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

import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 19 juin 2015. See GenerateFromFile.
 *
 */
public class GenerateFromFileLong extends GenerateFromFile<Long> {

    private static final long serialVersionUID = -2510960686417569211L;

    private List<Long> LongTokens = new ArrayList<>();

    @Override
    public void parse(String extraParameter, boolean keepNullValues, RandomWrapper rand) {
        super.parse(extraParameter, keepNullValues, rand);
        super.init();
    }

    @Override
    protected Long doGenerateMaskedField(Long l) {
        for (int i = 0; i < StringTokens.size(); ++i) {
            long tmp = 0L;
            try {
                tmp = Long.parseLong(StringTokens.get(i));
                LongTokens.add(tmp);
            } catch (NumberFormatException e) {
                LongTokens.add(0L);
            }
        }
        if (LongTokens.size() > 0) {
            return LongTokens.get(rnd.nextInt(LongTokens.size()));
        } else {
            return 0L;
        }
    }
}
