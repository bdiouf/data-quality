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
package org.talend.dataquality.semantic.index.utils.optimizer;

import java.util.HashSet;
import java.util.Set;

public class AirportOptimizer implements CategoryOptimizer {

    @Override
    public Set<String> optimize(String[] inputValues) {
        Set<String> synAirportNames = new HashSet<String>();
        for (String input : inputValues) {
            if (input == null || input.trim().length() == 0) {
                continue;
            }

            synAirportNames.add(input);

            // 1. create syn without the word in parenthesis
            if (input.contains("(")) {
                String subStrWithOutParenthesis = input.substring(0, input.indexOf("(")).trim()
                        + input.substring(input.indexOf(")") + 1);
                synAirportNames.add(subStrWithOutParenthesis);
            }

            // 2. create syn for the items with the keyword words, e.g. "Airport", "Heliport", "Field"
            if (input.contains(" Airport")) {// 2.1 create syn for the items with the word "Airport"
                if (input.contains("International Airport")) {
                    synAirportNames.add(input.replaceAll("(?i)" + java.util.regex.Pattern.quote(" International Airport"), ""));
                } else if (input.contains("Municipal Airport")) {
                    synAirportNames.add(input.replaceAll("(?i)" + java.util.regex.Pattern.quote(" Municipal Airport"), ""));
                } else if (input.contains("Regional Airport")) {
                    synAirportNames.add(input.replaceAll("(?i)" + java.util.regex.Pattern.quote(" Regional Airport"), ""));
                } else if (input.contains("County Airport")) {
                    synAirportNames.add(input.replaceAll("(?i)" + java.util.regex.Pattern.quote(" County Airport"), ""));
                }
                synAirportNames.add(input.replaceAll("(?i)" + java.util.regex.Pattern.quote(" Airport"), ""));

            }

            if (input.contains(" Heliport")) {// 2.2 create syn for the items with the word "Heliport"
                synAirportNames.add(input.replaceAll("(?i)" + java.util.regex.Pattern.quote(" Heliport"), ""));
            }

            if (input.contains(" Field")) {// 2.3 create syn for the items with the word "Field"
                synAirportNames.add(input.replaceAll("(?i)" + java.util.regex.Pattern.quote(" Field"), ""));
            }
        }
        return synAirportNames;
    }

}
