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
package org.talend.dataquality.semantic.index.utils;

import org.talend.dataquality.semantic.index.utils.optimizer.AirportOptimizer;
import org.talend.dataquality.semantic.index.utils.optimizer.CategoryOptimizer;
import org.talend.dataquality.semantic.index.utils.optimizer.FrCommuneOptimizer;
import org.talend.dataquality.semantic.index.utils.optimizer.UsCountyOptimizer;

public enum DictionaryGenerationSpec {

    /**
     * the categories defined in Keyword index
     */
    // ADDRESS_LINE("street_type_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0, 1, 2, 3, 4, 5 }),

    // FULL_NAME("civility_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0, 1, 2, 3, 4, 5 }),

    /**
     * the categories defined in Data Dictionary index
     */
    ANIMAL("animal_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0, 1, 2, 3, 4 }),

    ANSWER("answer.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0, 1 }),

    AIRPORT("airport-name-wiki.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0 }, new AirportOptimizer()),

    AIRPORT_CODE("airport-code-wiki.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0 }),

    CITY("city_cleaned_without_pinyin.csv", new CsvReaderConfig(CsvConstants.COMMA, false), new int[] {}),

    CIVILITY("civility_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0, 1, 2, 3, 4, 5 }),

    CONTINENT("continent_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0, 1, 2, 3, 4, 5 }),

    CONTINENT_CODE("continent_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 6 }),

    COUNTRY("country-codes.csv", new CsvReaderConfig(CsvConstants.COMMA, true), new int[] { 0, 1 }),

    COUNTRY_CODE_ISO2("country-codes.csv", new CsvReaderConfig(CsvConstants.COMMA, true), new int[] { 2 }),

    COUNTRY_CODE_ISO3("country-codes.csv", new CsvReaderConfig(CsvConstants.COMMA, true), new int[] { 3 }),

    CURRENCY_NAME("country-codes.csv", new CsvReaderConfig(CsvConstants.COMMA, true), new int[] { 17 }),

    CURRENCY_CODE("country-codes.csv", new CsvReaderConfig(CsvConstants.COMMA, true), new int[] { 14 }),

    HR_DEPARTMENT("hr_department_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0 }),

    FIRST_NAME("firstname_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0 }),

    // LAST_NAME("lastNames.txt", new CsvReaderConfig(CsvConstants.TAB, true), new int[] { 0 }),

    GENDER("gender_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0, 1, 2, 3, 4, 5 }),

    JOB_TITLE("job_title_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0 }),

    MONTH("months_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0, 1, 2, 3, 4, 5 }),

    STREET_TYPE("street_type_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0, 1, 2, 3, 4, 5 }),

    WEEKDAY("weekdays_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0, 1, 2, 3, 4, 5 }),

    MUSEUM("wordnet_museums_yago2.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0 }),

    US_COUNTY("us_counties.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0 }, new UsCountyOptimizer()),

    ORGANIZATION("wordnet_organizations_yago2.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0 }),

    COMPANY("wordnet_companies_yago2_optimized.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0 }),

    BEVERAGE("wordnet_beverages_yago2.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0 }),

    MEASURE_UNIT("units_of_measurement_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0 }),

    INDUSTRY("industry_GICS_simplified.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 1 }),

    INDUSTRY_GROUP("industry_group_GICS_simplified.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 1 }),

    SECTOR("industry_sector_GICS_simplified.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 1 }),

    FR_COMMUNE(
            "fr_comsimp2015.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 10, 11 },
            new FrCommuneOptimizer()),

    FR_DEPARTEMENT("fr_depts2015.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 5 }),

    FR_REGION("fr_reg2015.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 4 }),

    LANGUAGE("languages_code_name.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 2, 3, 4, 5 }),

    LANGUAGE_CODE_ISO2("languages_code_name.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0 }),

    LANGUAGE_CODE_ISO3("languages_code_name.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 1 }),

    CA_PROVINCE_TERRITORY("ca_province_territory.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0 }),

    CA_PROVINCE_TERRITORY_CODE("ca_province_territory.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 2 }),

    MX_ESTADO("mx_estado.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0 }),

    MX_ESTADO_CODE("mx_estado.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 2 });

    private String sourceFile;

    private CsvReaderConfig csvConfig;

    private int[] columnsToIndex;

    private CategoryOptimizer optimizer;

    /**
     * SemanticCategoryEnum constructor.
     * 
     * @param displayName the category shown in Semantic Discovery wizard
     * @param description the description of the category
     */
    private DictionaryGenerationSpec(String sourceFile, CsvReaderConfig csvConfig, int[] columnsToIndex) {
        this(sourceFile, csvConfig, columnsToIndex, null);
    }

    /**
     * SemanticCategoryEnum constructor.
     * 
     * @param displayName the category shown in Semantic Discovery wizard
     * @param description the description of the category
     */
    private DictionaryGenerationSpec(String sourceFile, CsvReaderConfig csvConfig, int[] columnsToIndex,
            CategoryOptimizer optimizer) {
        this.sourceFile = sourceFile;
        this.csvConfig = csvConfig;
        this.columnsToIndex = columnsToIndex;
        this.optimizer = optimizer;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public CsvReaderConfig getCsvConfig() {
        return csvConfig;
    }

    public int[] getColumnsToIndex() {
        return columnsToIndex;
    }

    public void setColumnsToIndex(int[] columnsToIndex) {
        this.columnsToIndex = columnsToIndex;
    }

    public CategoryOptimizer getOptimizer() {
        return optimizer;
    }

}
