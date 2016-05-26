package org.talend.dataquality.semantic.index.utils.optimizer;

import java.util.HashSet;
import java.util.Set;

public class UsCountyOptimizer implements CategoryOptimizer {

    @Override
    public Set<String> optimize(String[] values) {
        Set<String> output = new HashSet<String>();
        for (String input : values) {
            output.add(input);
            if (input.endsWith(" County")) {
                output.add(input.substring(0, input.length() - 7));
            } else if (input.endsWith(" Parish")) {
                output.add(input.substring(0, input.length() - 7));
            } else if (input.endsWith(" Area")) {
                output.add(input.substring(0, input.length() - 5));
            } else if (input.endsWith(" Borough")) {
                output.add(input.substring(0, input.length() - 8));
            } else if (input.startsWith("Municipality of ")) {
                output.add(input.substring(16, input.length()));
            } else if (input.startsWith("City and County of ")) {
                output.add(input.substring(19, input.length()));
            } else if (input.startsWith("Town and County of ")) {
                output.add(input.substring(19, input.length()));
            }
        }
        return output;
    }

}
