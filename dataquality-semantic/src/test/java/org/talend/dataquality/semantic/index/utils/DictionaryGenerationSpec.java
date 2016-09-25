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
    ADDRESS_LINE(GenerationType.KEYWORD, "street_type_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0, 1, 2, 3, 4, 5 }),

    FULL_NAME(
            GenerationType.KEYWORD,
            "civility_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0, 1, 2, 3, 4, 5 }),

    /**
     * the categories defined in Data Dictionary index
     */
    ANIMAL(GenerationType.DICTIONARY, "animal_cleaned.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0, 1, 2, 3, 4 }),

    ANSWER(GenerationType.DICTIONARY, "answer.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 0, 1 }),

    AIRPORT(
            GenerationType.DICTIONARY,
            "airport-name-wiki.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 0 },
            new AirportOptimizer()),

    AIRPORT_CODE(
            GenerationType.DICTIONARY,
            "airport-code-wiki.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0 }),

    CITY(
            GenerationType.DICTIONARY,
            "city_cleaned_without_pinyin.csv",
            new CsvReaderConfig(CsvConstants.COMMA, false),
            new int[] {}),

    // CITY_COMPLEMENTED("city_complemented.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, false), new int[] { 2 }, null,
    // "CITY"),

    CIVILITY(
            GenerationType.DICTIONARY,
            "civility_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0, 1, 2, 3, 4, 5 }),

    CONTINENT(
            GenerationType.DICTIONARY,
            "continent_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 0, 1, 2, 3, 4, 5 }),

    CONTINENT_CODE(
            GenerationType.DICTIONARY,
            "continent_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 6 }),

    COUNTRY(GenerationType.DICTIONARY, "country-codes.csv", new CsvReaderConfig(CsvConstants.COMMA, true), new int[] { 0, 1 }),

    COUNTRY_CODE_ISO2(
            GenerationType.DICTIONARY,
            "country-codes.csv",
            new CsvReaderConfig(CsvConstants.COMMA, true),
            new int[] { 2 }),

    COUNTRY_CODE_ISO3(
            GenerationType.DICTIONARY,
            "country-codes.csv",
            new CsvReaderConfig(CsvConstants.COMMA, true),
            new int[] { 3 }),

    CURRENCY_NAME(
            GenerationType.DICTIONARY,
            "country-codes.csv",
            new CsvReaderConfig(CsvConstants.COMMA, true),
            new int[] { 17 }),

    CURRENCY_CODE(
            GenerationType.DICTIONARY,
            "country-codes.csv",
            new CsvReaderConfig(CsvConstants.COMMA, true),
            new int[] { 14 }),

    HR_DEPARTMENT(
            GenerationType.DICTIONARY,
            "hr_department_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 0 }),

    FIRST_NAME(
            GenerationType.DICTIONARY,
            "firstname_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0 }),

    LAST_NAME(GenerationType.DICTIONARY, "lastname12k.csv", new CsvReaderConfig(CsvConstants.COMMA, true), new int[] { 0 }),

    GENDER(
            GenerationType.DICTIONARY,
            "gender_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0, 1, 2, 3, 4, 5 }),

    JOB_TITLE(
            GenerationType.DICTIONARY,
            "job_title_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 0 }),

    MONTH(
            GenerationType.DICTIONARY,
            "months_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0, 1, 2, 3, 4, 5 }),

    STREET_TYPE(
            GenerationType.DICTIONARY,
            "street_type_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0, 1, 2, 3, 4, 5 }),

    WEEKDAY(
            GenerationType.DICTIONARY,
            "weekdays_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0, 1, 2, 3, 4, 5 }),

    MUSEUM(
            GenerationType.DICTIONARY,
            "wordnet_museums_yago2.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 0 }),

    US_COUNTY(
            GenerationType.DICTIONARY,
            "us_counties.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 0 },
            new UsCountyOptimizer()),

    ORGANIZATION(
            GenerationType.DICTIONARY,
            "wordnet_organizations_yago2.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 0 }),

    COMPANY(
            GenerationType.DICTIONARY,
            "wordnet_companies_yago2_optimized.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0 }),

    BEVERAGE(
            GenerationType.DICTIONARY,
            "wordnet_beverages_yago2.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 0 }),

    MEASURE_UNIT(
            GenerationType.DICTIONARY,
            "units_of_measurement_cleaned.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 0 }),

    INDUSTRY(
            GenerationType.DICTIONARY,
            "industry_GICS_simplified.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 1 }),

    INDUSTRY_GROUP(
            GenerationType.DICTIONARY,
            "industry_group_GICS_simplified.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 1 }),

    SECTOR(
            GenerationType.DICTIONARY,
            "industry_sector_GICS_simplified.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 1 }),

    FR_COMMUNE(
            GenerationType.DICTIONARY,
            "fr_comsimp2015.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 10, 11 },
            new FrCommuneOptimizer()),

    FR_DEPARTEMENT(
            GenerationType.DICTIONARY,
            "fr_depts2015.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 5 }),

    FR_REGION(GenerationType.DICTIONARY, "fr_reg2016.txt", new CsvReaderConfig(CsvConstants.TAB, true), new int[] { 4 }),

    FR_REGION_LEGACY(
            GenerationType.DICTIONARY,
            "fr_reg2015.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, false),
            new int[] { 4 }),

    LANGUAGE(
            GenerationType.DICTIONARY,
            "languages_code_name.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 2, 3, 4, 5 }),

    LANGUAGE_CODE_ISO2(
            GenerationType.DICTIONARY,
            "languages_code_name.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0 }),

    LANGUAGE_CODE_ISO3(
            GenerationType.DICTIONARY,
            "languages_code_name.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 1 }),

    CA_PROVINCE_TERRITORY(
            GenerationType.DICTIONARY,
            "ca_province_territory.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 0 }),

    CA_PROVINCE_TERRITORY_CODE(
            GenerationType.DICTIONARY,
            "ca_province_territory.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 2 }),

    MX_ESTADO(GenerationType.DICTIONARY, "mx_estado.csv", new CsvReaderConfig(CsvConstants.SEMICOLON, true), new int[] { 0 }),

    MX_ESTADO_CODE(
            GenerationType.DICTIONARY,
            "mx_estado.csv",
            new CsvReaderConfig(CsvConstants.SEMICOLON, true),
            new int[] { 2 });

    private GenerationType generationType;

    private String sourceFile;

    private CsvReaderConfig csvConfig;

    private int[] columnsToIndex;

    private CategoryOptimizer optimizer;

    private String categoryName;

    private DictionaryGenerationSpec(GenerationType generationType, String sourceFile, CsvReaderConfig csvConfig,
            int[] columnsToIndex) {
        this(generationType, sourceFile, csvConfig, columnsToIndex, null, null);
    }

    private DictionaryGenerationSpec(GenerationType generationType, String sourceFile, CsvReaderConfig csvConfig,
            int[] columnsToIndex, CategoryOptimizer optimizer) {
        this(generationType, sourceFile, csvConfig, columnsToIndex, optimizer, null);
    }

    private DictionaryGenerationSpec(GenerationType generationType, String sourceFile, CsvReaderConfig csvConfig,
            int[] columnsToIndex, CategoryOptimizer optimizer, String categoryName) {
        this.generationType = generationType;
        this.sourceFile = sourceFile;
        this.csvConfig = csvConfig;
        this.columnsToIndex = columnsToIndex;
        this.optimizer = optimizer;
        if (categoryName == null) {
            this.categoryName = this.name();
        } else {
            this.categoryName = categoryName;
        }
    }

    public GenerationType getGenerationType() {
        return generationType;
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

    public String getCategoryName() {
        return categoryName;
    }

}

enum GenerationType {
    DICTIONARY,
    KEYWORD
}
