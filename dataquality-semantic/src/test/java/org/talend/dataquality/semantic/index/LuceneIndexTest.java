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
package org.talend.dataquality.semantic.index;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

public class LuceneIndexTest {

    private static final Logger LOGGER = Logger.getLogger(LuceneIndexTest.class);

    private static Map<String[], String[]> EXPECTED_SIMILAR_VALUES = new LinkedHashMap<String[], String[]>() {

        private static final long serialVersionUID = 1L;

        {
            put(new String[] { "talend", SemanticCategoryEnum.COMPANY.getId() },//
                    new String[] { "Talend", "Vox Talent" });
            put(new String[] { "Russian Federatio", SemanticCategoryEnum.COUNTRY.getId() }, //
                    new String[] { "Russian Federation", "Fédération de Russie" });
            put(new String[] { "Federation de Russie", SemanticCategoryEnum.COUNTRY.getId() }, //
                    new String[] { "Russian Federation", "Fédération de Russie" });
            put(new String[] { "Russie", SemanticCategoryEnum.COUNTRY.getId() }, //
                    new String[] { "Russie", "Russia" });

            put(new String[] { "viet nam", SemanticCategoryEnum.COUNTRY.getId() }, //
                    new String[] { "Viet Nam", "Viêt-nam", "Viêt Nam" });
            put(new String[] { "vietnam", SemanticCategoryEnum.COUNTRY.getId() }, //
                    new String[] { "Vietnam", "Viêtnam" });

            put(new String[] { "Oil Gas Consumable Fuels", SemanticCategoryEnum.INDUSTRY.getId() }, //
                    new String[] { "Oil, Gas & Consumable Fuels", "Gas Utilities" });

            put(new String[] { "Clermont Ferrand", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont-Ferrand", "Clermont" });
            put(new String[] { "clermont ferrand", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont-Ferrand", "Clermont" });
            put(new String[] { "Clermont Fd", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont-Ferrand", "Clermont" });
            put(new String[] { "clermont fd", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont-Ferrand", "Clermont" });
            put(new String[] { "Clermont", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont-Ferrand", "Clermont" });
            put(new String[] { "clermont", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont-Ferrand", "Clermont" });
            put(new String[] { "Ferrand", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont-Ferrand", "Ferran" });
            put(new String[] { "ferrand", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Clermont-Ferrand", "Ferran" });

            put(new String[] { "carrières", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Carrières-sur-Seine" });
            put(new String[] { "carrieres", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Carrières-sur-Seine" });
            put(new String[] { "carrière", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Carrère" });
            put(new String[] { "carriere", SemanticCategoryEnum.FR_COMMUNE.getId() }, //
                    new String[] { "Carrère" });
        }
    };

    @Test
    public void testFindSimilarFieldsInCategory() throws URISyntaxException {

        final URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI();
        final LuceneIndex dataDictIndex = new LuceneIndex(ddPath, SynonymIndexSearcher.SynonymSearchMode.MATCH_ANY_FUZZY);

        for (String[] input : EXPECTED_SIMILAR_VALUES.keySet()) {
            final List<String> expectedMatches = Arrays.asList(EXPECTED_SIMILAR_VALUES.get(input));
            LOGGER.debug("-----------search [" + input[0] + "] in category " + input[1] + "----------");
            Map<String, Float> resultMap = dataDictIndex.findSimilarFieldsInCategory(input[0], input[1]);
            for (String key : resultMap.keySet()) {
                LOGGER.debug(key + " \t " + resultMap.get(key));
            }
            for (String match : expectedMatches) {
                assertTrue("The value [" + match + "] is expected to be found for the search of [" + input[0] + "].", resultMap
                        .keySet().contains(match));
            }
        }

    }
}
