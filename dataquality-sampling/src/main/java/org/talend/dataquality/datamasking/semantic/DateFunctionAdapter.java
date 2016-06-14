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
package org.talend.dataquality.datamasking.semantic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.talend.dataquality.datamasking.functions.Function;

public class DateFunctionAdapter extends Function<String> {

    private static final long serialVersionUID = -2845447810365033162L;

    private Function<Date> function;

    private List<SimpleDateFormat> dataFormatList = new ArrayList<>();

    public DateFunctionAdapter(Function<Date> functionToAdapt, List<String> datePatternList) {
        function = functionToAdapt;
        if (datePatternList != null) {
            for (String pattern : datePatternList) {
                try {
                    dataFormatList.add(new SimpleDateFormat(pattern));
                } catch (IllegalArgumentException e) {
                    // do nothing for invalid date pattern;
                }
            }
        }
    }

    @Override
    protected String doGenerateMaskedField(String input) {
        if (input == null || EMPTY_STRING.equals(input.trim())) {
            return input;
        }
        for (SimpleDateFormat sdf : dataFormatList) {
            try {
                if (!sdf.toPattern().contains("H") && input.contains(":")) {
                    continue;
                }
                final Date inputDate = sdf.parse(input);
                final Date result = function.generateMaskedRow(inputDate);
                return sdf.format(result);
            } catch (ParseException e) {
                // do nothing, continue to try other patterns;
            }
        }
        return EMPTY_STRING;
    }

}
