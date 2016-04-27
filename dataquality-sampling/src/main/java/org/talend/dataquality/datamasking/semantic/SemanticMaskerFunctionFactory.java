package org.talend.dataquality.datamasking.semantic;

import org.apache.log4j.Logger;
import org.talend.dataquality.datamasking.functions.DateVariance;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.functions.NumericVarianceString;
import org.talend.dataquality.datamasking.functions.ReplaceCharacters;

public class SemanticMaskerFunctionFactory {

    private static final Logger LOGGER = Logger.getLogger(SemanticMaskerFunctionFactory.class);

    public static Function<String> createMaskerFunctionForSemanticCategory(String semanticCategory, String dataType) {
        Function<String> function = null;
        final MaskableCategoryEnum cat = MaskableCategoryEnum.getCategoryById(semanticCategory);
        if (cat != null) {
            try {
                function = (Function<String>) cat.getFunctionType().getClazz().newInstance();
                function.parse("X", true, null);
            } catch (InstantiationException e) {
                LOGGER.debug(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                LOGGER.debug(e.getMessage(), e);
            }
        }
        if (function == null) {
            if ("string".equals(dataType)) {
                // string -> use ReplaceAll
                function = new ReplaceCharacters();
                function.parse("X", true, null);
            } else if ("numeric".equals(dataType)) {
                // numeric -> use NumericVariance
                function = new NumericVarianceString();
                function.parse("10", true, null);
            } else if ("date".equals(dataType)) {
                // date -> DateVariance with parameter 61 (meaning two months)
                DateVariance df = new DateVariance();
                df.parse("61", true, null);
                function = new DateFunctionAdapter(df, "yyyy-MM-dd");
            }
        }
        if (function == null) {
            throw new IllegalArgumentException("No masking function available for the current column! SemanticCategory: "
                    + semanticCategory + " DataType: " + dataType);
        }
        return function;
    }

}
