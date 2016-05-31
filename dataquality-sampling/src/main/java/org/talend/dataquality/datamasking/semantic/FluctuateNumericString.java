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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

import org.talend.daikon.number.BigDecimalParser;
import org.talend.dataquality.datamasking.functions.NumericVariance;

public class FluctuateNumericString extends NumericVariance<String> {

    private static final long serialVersionUID = -8029563336814263376L;

    private static final Pattern patternInteger = Pattern.compile("^(\\+|-)?\\d+$");

    @Override
    protected String doGenerateMaskedField(String input) {
        if (input == null || EMPTY_STRING.equals(input.trim())) {
            return input;
        } else {
            init();
            double rateToApply = rnd.nextDouble() * rate;
            if (patternInteger.matcher(input).matches()) {
                final long result = Math.round(Integer.valueOf(input) * (rateToApply + 100) / 100);
                return String.valueOf(result);
            } else {
                try {
                    final double doubleValue = BigDecimalParser.toBigDecimal(input).doubleValue();
                    final String doubleStr = String.valueOf(doubleValue);
                    final int decimalLength = doubleStr.substring(doubleStr.lastIndexOf(".")).length() - 1;
                    final Double result = doubleValue * (rateToApply + 100) / 100;
                    final BigDecimal bigDecimal = new BigDecimal(result).setScale(decimalLength, RoundingMode.HALF_UP);
                    return bigDecimal.toString();
                } catch (NumberFormatException e) {
                    return EMPTY_STRING;
                }
            }
        }
    }
}
