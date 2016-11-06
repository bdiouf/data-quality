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
package org.talend.dataquality.semantic.classifier;

import org.talend.dataquality.semantic.model.CategoryType;

/**
 * Enumeration of all supported semantic categories.
 * <p/>
 * In most cases, the keys of the categories should not be changed.
 */
public enum SemanticCategoryEnum {

    /**
     * the categories defined in Data Dictionary index
     */
    ANIMAL("Animal", "Animal (multilingual)", CategoryType.DD, false),
    ANSWER("Answer", "Yes/No (in EN, FR, DE and ES)", CategoryType.DD, false),
    AIRPORT("Airport", "Airport name", CategoryType.DD, false),
    AIRPORT_CODE("Airport Code", "Airport name", CategoryType.DD, true),
    CIVILITY("Civility", "Civility (multilingual)", CategoryType.DD, true),
    CONTINENT("Continent", "Continent name (multilingual)", CategoryType.DD, true),
    CONTINENT_CODE("Continent Code", "Continent code", CategoryType.DD, true),
    COUNTRY("Country", "Country Name (EN+FR)", CategoryType.DD, true),
    COUNTRY_CODE_ISO2("Country Code ISO2", "Country code of ISO3166-1 Alpha-2", CategoryType.DD, true),
    COUNTRY_CODE_ISO3("Country Code ISO3", "Country code of ISO3166-1 Alpha-3", CategoryType.DD, true),
    CURRENCY_NAME("Currency Name", "Currency name (EN)", CategoryType.DD, true),
    CURRENCY_CODE("Currency Code", "Currency alphabetic code", CategoryType.DD, true),
    HR_DEPARTMENT("HR Department", "Department or service name in company", CategoryType.DD, false),

    FIRST_NAME("First Name", "First name", CategoryType.DD, false),
    LAST_NAME("Last Name", "Last name", CategoryType.DD, false),
    CITY("City", "City name (multilingual)", CategoryType.DD, false),

    GENDER("Gender", "Gender (Multilingual)", CategoryType.DD, true),
    JOB_TITLE("Job Title", "Job Title (EN)", CategoryType.DD, false),
    MONTH("Month", "Month (Multilingual)", CategoryType.DD, true),
    STREET_TYPE("Street Type", "Street type (multilingual)", CategoryType.DD, true),
    WEEKDAY("Weekday", "Weekday (multilingual)", CategoryType.DD, true),

    MUSEUM("Museum", "Museum Names", CategoryType.DD, false),
    US_COUNTY("US County", "US County Names", CategoryType.DD, true),
    ORGANIZATION("Organization", "Organization Names", CategoryType.DD, false),
    COMPANY("Company", "Company Names", CategoryType.DD, false),
    BEVERAGE("Beverage", "Beverage Names", CategoryType.DD, false),
    MEASURE_UNIT("Measure Unit", "Units of Measurement", CategoryType.DD, false),
    INDUSTRY("Industry", "Industry Names", CategoryType.DD, false),
    INDUSTRY_GROUP("Industry Group", "Industry Group Names", CategoryType.DD, false),
    SECTOR("Sector", "Economic Sector Names", CategoryType.DD, false),

    FR_COMMUNE("FR Commune", "French Commune names", CategoryType.DD, true),
    FR_DEPARTEMENT("FR Departement", "French Departement names", CategoryType.DD, true),
    FR_REGION("FR Region", "French Region names", CategoryType.DD, true),
    FR_REGION_LEGACY("FR Region Legacy", "Legacy French Region names", CategoryType.DD, true),
    LANGUAGE("Language", "Language Name (EN+FR+DE+NATIVE)", CategoryType.DD, true),
    LANGUAGE_CODE_ISO2("Language Code ISO2", "Language Code of ISO639-1 Alpha-2", CategoryType.DD, true),
    LANGUAGE_CODE_ISO3("Language Code ISO3", "Language Code of ISO639-2 Alpha-3 (B/T)", CategoryType.DD, true),
    CA_PROVINCE_TERRITORY("CA Province Territory", "Provinces and Territories of Canada", CategoryType.DD, true),
    CA_PROVINCE_TERRITORY_CODE("CA Province Territory Code", "Canada Provinces and Territories Code", CategoryType.DD, true),
    MX_ESTADO("MX Estado", "Federated States and Federal District of Mexico", CategoryType.DD, true),
    MX_ESTADO_CODE("MX Estado Code", "Mexico States Code", CategoryType.DD, true),

    /**
     * the categories defined in Keyword index
     */
    ADDRESS_LINE("Address Line", "Address line which contains STREET_TYPE keyword", CategoryType.DD, false),
    FULL_NAME("Full Name", "Full name which contains CIVILITY keyword", CategoryType.DD, false),

    /**
     * the categories defined in categorizer.json
     */
    AT_VAT_NUMBER("AT VAT Number", "Austria VAT number", CategoryType.RE, true),
    BANK_ROUTING_TRANSIT_NUMBER("Bank Routing Transit Number", "Bank routing transit Number", CategoryType.RE, true),
    BE_POSTAL_CODE("BE Postal Code", "Belgium postal code", CategoryType.RE, true),
    BG_VAT_NUMBER("BG VAT Number", "Bulgaria VAT number", CategoryType.RE, true),
    COLOR_HEX_CODE("Color Hex Code", "Color hexadecimal code", CategoryType.RE, true),
    EMAIL("Email", "email", CategoryType.RE, true),

    // EN
    EN_MONEY_AMOUNT("Money Amount (EN)", "Amount of money in English format", CategoryType.RE, true),
    EN_MONTH("EN Month", "Month in English", CategoryType.RE, true),
    EN_MONTH_ABBREV("EN Month Abbrev", "Month English abbreviation", CategoryType.RE, true),
    EN_WEEKDAY("EN Weekday", "Weekday or their abbreviation", CategoryType.RE, true),

    // FR
    FR_MONEY_AMOUNT("Money Amount (FR)", "Amount of money in French format", CategoryType.RE, true),
    FR_PHONE("FR Phone", "French Phone Number", CategoryType.RE, true),
    FR_POSTAL_CODE("FR Postal Code", "French postal code", CategoryType.RE, true),
    FR_CODE_COMMUNE_INSEE("FR Insee Code", "French Insee code of cities with Corsica and colonies", CategoryType.RE, true),
    FR_SSN("FR Social Security Number", "French Social Security Number", CategoryType.RE, true),
    FR_VAT_NUMBER("FR VAT Number", "French VAT number", CategoryType.RE, true),

    // US
    US_PHONE("US Phone", "US Phone Number", CategoryType.RE, true),
    US_POSTAL_CODE("US Postal Code", "US postal code", CategoryType.RE, true),
    US_SSN("US Social Security Number", "US Social Security number", CategoryType.RE, true),
    US_STATE("US State", "US states", CategoryType.RE, true),
    US_STATE_CODE("US State Code", "US State code", CategoryType.RE, true),

    // DE
    DE_PHONE("DE Phone", "German phone number", CategoryType.RE, true),
    DE_POSTAL_CODE("DE Postal Code", "German postal code", CategoryType.RE, true),

    // UK
    UK_PHONE("UK Phone", "UK phone number", CategoryType.RE, true),
    UK_POSTAL_CODE("UK Postal Code", "UK postal code", CategoryType.RE, true),
    UK_SSN(
            "UK Social Security Number",
            "national identification number, national identity number, or national insurance number generally called an NI Number (NINO)",
            CategoryType.RE,
            true),

    GPS_COORDINATE("GPS Coordinate", "Google Maps style GPS Decimal format", CategoryType.RE, true),
    IPv4_ADDRESS("IPv4 Address", "IPv4 address", CategoryType.RE, true),
    IPv6_ADDRESS("IPv6 Address", "IPv6 address", CategoryType.RE, true),
    ISBN_10("ISBN-10", "International Standard Book Number 10 digits. Such as ISBN 2-711-79141-6", CategoryType.RE, true),
    ISBN_13("ISBN-13", "International Standard Book Number 13 digits.", CategoryType.RE, true),
    LOCALIZATION("Localization", "Longitude or latitude coordinates with at least meter precision", CategoryType.RE, true),
    LOCATION_COORDINATE(
            "Location Coordinate",
            "Latitude and longitude coordinates separated by a comma in the form: N 0:59:59.99,E 0:59:59.99",
            CategoryType.RE,
            true),
    MAC_ADDRESS("MAC Address", "MAC Address.", CategoryType.RE, true),

    AMEX_CARD("Amex Card", "American Express card", CategoryType.RE, true),
    MASTERCARD("MasterCard", "MasterCard", CategoryType.RE, true),
    VISA_CARD("Visa Card", "Visa card", CategoryType.RE, true),

    PASSPORT("Passport", "Passport number", CategoryType.RE, true),
    SEDOL("SEDOL", "Stock Exchange Daily Official List", CategoryType.RE, true),
    SE_SSN("SE Social Security Number", "Swedish person number", CategoryType.RE, true),
    URL("Web URL", "Web site URL", CategoryType.RE, true),
    WEB_DOMAIN("Web Domain", "Web site domain", CategoryType.RE, true),

    HDFS_URL("HDFS URL", "HDFS URL", CategoryType.RE, true),
    FILE_URL("File URL", "File URL", CategoryType.RE, true),
    MAILTO_URL("MailTo URL", "MailTo URL", CategoryType.RE, true),
    DATA_URL("Data URL", "Data URL", CategoryType.RE, true),
    IBAN("IBAN", "IBAN", CategoryType.RE, true),

    UNKNOWN("", "Blank, Null and those who have no other semantic category", CategoryType.OT, false),

    /**
     * the categories with specific implementations
     */
    DATE("Date", "Date", CategoryType.OT, false);

    private String displayName;

    private String description;

    private CategoryType categoryType;

    private boolean completeness;

    /**
     * SemanticCategoryEnum constructor.
     * 
     * @param displayName the category shown in Semantic Discovery wizard
     * @param description the description of the category
     */
    private SemanticCategoryEnum(String displayName, String description, CategoryType categoryType, boolean completeness) {
        this.displayName = displayName;
        this.description = description;
        this.categoryType = categoryType;
        this.completeness = completeness;
    }

    public String getId() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public boolean getCompleteness() {
        return completeness;
    }

    /**
     * @deprecated use {@link CategoryRegistryManager.getInstance(contextName).getCategoryLabel()} instead
     */
    public static SemanticCategoryEnum getCategoryById(String catId) {
        if ("".equals(catId)) {
            return UNKNOWN;
        }
        try {
            return valueOf(catId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
