package org.talend.dataquality.datamasking.semantic;

import org.apache.log4j.Logger;
import org.talend.dataquality.datamasking.functions.DateVariance;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.functions.NumericVarianceInteger;
import org.talend.dataquality.datamasking.functions.NumericVarianceString;

public class SemanticMaskerFunctionFactory {

    private static final Logger LOGGER = Logger.getLogger(SemanticMaskerFunctionFactory.class);

    @SuppressWarnings("unchecked")
    public static Function<String> createMaskerFunctionForSemanticCategory(String semanticCategory, String dataType) {
        Function<String> function = null;
        final MaskableCategoryEnum cat = MaskableCategoryEnum.getCategoryById(semanticCategory);
        if (cat != null) {
            try {
                function = (Function<String>) cat.getFunctionType().getClazz().newInstance();
                if (cat.getParameter() == null) {
                    function.parse("X", true, null); //$NON-NLS-1$
                } else {
                    function.parse(cat.getParameter(), true, null);
                }
            } catch (InstantiationException e) {
                LOGGER.debug(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                LOGGER.debug(e.getMessage(), e);
            }
        }
        if (function == null) {
            switch (dataType) {
            case "numeric":
            case "integer":
                NumericVarianceInteger nvi = new NumericVarianceInteger();
                nvi.parse("10", true, null);
                function = new IntegerFunctionAdapter(nvi);
                break;
            case "decimal":
                function = new NumericVarianceString();
                function.parse("10", true, null);
                break;
            case "date":
                DateVariance df = new DateVariance();
                df.parse("61", true, null);
                function = new DateFunctionAdapter(df, "M/d/yyyy");
                break;
            case "string":
                function = new ReplaceCharactersWithGeneration();
                function.parse("X", true, null);
                break;
            default:
                break;

            }
        }
        if (function == null) {
            throw new IllegalArgumentException("No masking function available for the current column! SemanticCategory: "
                    + semanticCategory + " DataType: " + dataType);
        }
        return function;
    }

}
