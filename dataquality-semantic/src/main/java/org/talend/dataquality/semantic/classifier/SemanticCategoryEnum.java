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

/**
 * Enumeration of all supported semantic categories.
 * <p/>
 * In most cases, the keys of the categories should not be changed.
 */
public enum SemanticCategoryEnum {
    UNKNOWN("", "Blank, Null and those who have no other semantic category", RecognizerType.OTHER),

    /**
     * the categories defined in Data Dictionary index
     */
    ANIMAL("Animal", "Animal (multilingual)", RecognizerType.OPEN_INDEX),
    ANSWER("Answer", "Yes/No (in EN, FR, DE and ES)", RecognizerType.OPEN_INDEX),
    AIRPORT("Airport", "Airport name", RecognizerType.OPEN_INDEX),
    AIRPORT_CODE("Airport Code", "Airport name", RecognizerType.CLOSED_INDEX),
    CITY("City", "City name (multilingual)", RecognizerType.OPEN_INDEX),
    CIVILITY("Civility", "Civility (multilingual)", RecognizerType.CLOSED_INDEX),
    CONTINENT("Continent", "Continent name (multilingual)", RecognizerType.CLOSED_INDEX),
    CONTINENT_CODE("Continent Code", "Continent code", RecognizerType.CLOSED_INDEX),
    COUNTRY("Country", "Country Name (EN+FR)", RecognizerType.CLOSED_INDEX),
    COUNTRY_CODE_ISO2("Country Code ISO2", "Country code of ISO3166-1 Alpha-2", RecognizerType.CLOSED_INDEX),
    COUNTRY_CODE_ISO3("Country Code ISO3", "Country code of ISO3166-1 Alpha-3", RecognizerType.CLOSED_INDEX),
    CURRENCY_NAME("Currency Name", "Currency name (EN)", RecognizerType.CLOSED_INDEX),
    CURRENCY_CODE("Currency Code", "Currency alphabetic code", RecognizerType.CLOSED_INDEX),
    HR_DEPARTMENT("HR Department", "Department or service name in company", RecognizerType.OPEN_INDEX),
    FIRST_NAME("First Name", "First name", RecognizerType.OPEN_INDEX),
    LAST_NAME("Last Name", "Last name", RecognizerType.OPEN_INDEX),
    GENDER("Gender", "Gender (Multilingual)", RecognizerType.CLOSED_INDEX),
    JOB_TITLE("Job Title", "Job Title (EN)", RecognizerType.OPEN_INDEX),
    MONTH("Month", "Month (Multilingual)", RecognizerType.CLOSED_INDEX),
    STREET_TYPE("Street Type", "Street type (multilingual)", RecognizerType.CLOSED_INDEX),
    WEEKDAY("Weekday", "Weekday (multilingual)", RecognizerType.CLOSED_INDEX),

    MUSEUM("Museum", "Museum Names", RecognizerType.OPEN_INDEX),
    US_COUNTY("US County", "US County Names", RecognizerType.CLOSED_INDEX),
    ORGANIZATION("Organization", "Organization Names", RecognizerType.OPEN_INDEX),
    COMPANY("Company", "Company Names", RecognizerType.OPEN_INDEX),
    BEVERAGE("Beverage", "Beverage Names", RecognizerType.OPEN_INDEX),
    MEASURE_UNIT("Measure Unit", "Units of Measurement", RecognizerType.OPEN_INDEX),
    INDUSTRY("Industry", "Industry Names", RecognizerType.OPEN_INDEX),
    INDUSTRY_GROUP("Industry Group", "Industry Group Names", RecognizerType.OPEN_INDEX),
    SECTOR("Sector", "Economic Sector Names", RecognizerType.OPEN_INDEX),

    FR_COMMUNE("FR Commune", "French Commune names", RecognizerType.CLOSED_INDEX),
    FR_DEPARTEMENT("FR Departement", "French Departement names", RecognizerType.CLOSED_INDEX),
    FR_REGION("FR Region", "French Region names", RecognizerType.CLOSED_INDEX),
    LANGUAGE("Language", "Language Name (EN+FR+DE+NATIVE)", RecognizerType.CLOSED_INDEX),
    LANGUAGE_CODE_ISO2("Language Code ISO2", "Language Code of ISO639-1 Alpha-2", RecognizerType.CLOSED_INDEX),
    LANGUAGE_CODE_ISO3("Language Code ISO3", "Language Code of ISO639-2 Alpha-3 (B/T)", RecognizerType.CLOSED_INDEX),
    CA_PROVINCE_TERRITORY("CA Province Territory", "Provinces and Territories of Canada", RecognizerType.CLOSED_INDEX),
    CA_PROVINCE_TERRITORY_CODE("CA Province Territory Code", "Canada Provinces and Territories Code", RecognizerType.CLOSED_INDEX),
    MX_ESTADO("MX Estado", "Federated States and Federal District of Mexico", RecognizerType.CLOSED_INDEX),
    MX_ESTADO_CODE("MX Estado Code", "Mexico States Code", RecognizerType.CLOSED_INDEX),

    /**
     * the categories defined in Keyword index
     */
    ADDRESS_LINE("Address Line", "Address line which contains STREET_TYPE keyword", RecognizerType.OPEN_INDEX),
    FULL_NAME("Full Name", "Full name which contains CIVILITY keyword", RecognizerType.OPEN_INDEX),

    /**
     * the categories defined in categorizer.json
     */
    AT_VAT_NUMBER("AT VAT Number", "Austria VAT number", RecognizerType.REGEX),
    BANK_ROUTING_TRANSIT_NUMBER("Bank Routing Transit Number", "Bank routing transit Number", RecognizerType.REGEX),
    BE_POSTAL_CODE("BE Postal Code", "Belgium postal code", RecognizerType.REGEX),
    BG_VAT_NUMBER("BG VAT Number", "Bulgaria VAT number", RecognizerType.REGEX),
    COLOR_HEX_CODE("Color Hex Code", "Color hexadecimal code", RecognizerType.REGEX),

    // DE
    DE_PHONE("DE Phone", "German phone number", RecognizerType.REGEX),
    DE_POSTAL_CODE("DE Postal Code", "German postal code", RecognizerType.REGEX),

    EMAIL("Email", "email", RecognizerType.REGEX),

    // EN
    EN_MONEY_AMOUNT("Money Amount (EN)", "Amount of money in English format", RecognizerType.REGEX),
    EN_MONTH("EN Month", "Month in English", RecognizerType.REGEX),
    EN_MONTH_ABBREV("EN Month Abbrev", "Month English abbreviation", RecognizerType.REGEX),
    EN_WEEK_DAY("EN Week Day", "Week Day or their abbreviation", RecognizerType.REGEX),

    // FR
    FR_MONEY_AMOUNT("Money Amount (FR)", "Amount of money in French format", RecognizerType.REGEX),
    FR_CODE_COMMUNE_INSEE("FR Insee Code", "French Insee code of cities with Corsica and colonies", RecognizerType.REGEX),
    FR_PHONE("FR Phone", "French Phone Number", RecognizerType.REGEX),
    FR_POSTAL_CODE("FR Postal Code", "French postal code", RecognizerType.REGEX),
    FR_SSN("FR Social Security Number", "French Social Security Number", RecognizerType.REGEX),
    FR_VAT_NUMBER("FR VAT Number", "French VAT number", RecognizerType.REGEX),

    GPS_COORDINATE("GPS Coordinate", "Google Maps style GPS Decimal format", RecognizerType.REGEX),
    IPv4_ADDRESS("IPv4 Address", "IPv4 address", RecognizerType.REGEX),
    IPv6_ADDRESS("IPv6 Address", "IPv6 address", RecognizerType.REGEX),
    ISBN_10("ISBN-10", "International Standard Book Number 10 digits. Such as ISBN 2-711-79141-6", RecognizerType.REGEX),
    ISBN_13("ISBN-13", "International Standard Book Number 13 digits.", RecognizerType.REGEX),
    LOCALIZATION("Localization", "Longitude or latitude coordinates with at least meter precision", RecognizerType.REGEX),
    LOCATION_COORDINATE(
                        "Location Coordinate",
                        "Latitude and longitude coordinates separated by a comma in the form: N 0:59:59.99,E 0:59:59.99",
                        RecognizerType.REGEX),
    MAC_ADDRESS("MAC Address", "MAC Address.", RecognizerType.REGEX),
    MASTERCARD("Mastercard", "MasterCard Credit card", RecognizerType.REGEX),
    VISACARD("Visa Card", "Visa Credit card", RecognizerType.REGEX),
    PASSPORT("Passport", "Passport number", RecognizerType.REGEX),
    SEDOL("SEDOL", "Stock Exchange Daily Official List", RecognizerType.REGEX),
    SE_SSN("SE Social Security Number", "Swedish person number", RecognizerType.REGEX),
    URL("Web URL", "Web site URL", RecognizerType.REGEX),

    // UK
    UK_PHONE("UK Phone", "UK phone number", RecognizerType.REGEX),
    UK_POSTAL_CODE("UK Postal Code", "UK postal code", RecognizerType.REGEX),
    UK_SSN(
           "UK Social Security Number",
           "national identification number, national identity number, or national insurance number generally called an NI Number (NINO)",
           RecognizerType.REGEX),

    // US
    US_CREDIT_CARD("AmEx Credit Card", "US American Express Credit card", RecognizerType.REGEX),
    US_PHONE("US Phone", "US Phone Number", RecognizerType.REGEX),
    US_POSTAL_CODE("US Postal Code", "US postal code", RecognizerType.REGEX),
    US_SSN("US Social Security Number", "US Social Security number", RecognizerType.REGEX),
    US_STATE("US State", "US states", RecognizerType.REGEX),
    US_STATE_CODE("US State Code", "US State code", RecognizerType.REGEX),

    WEB_DOMAIN("Web Domain", "Web site domain", RecognizerType.REGEX),

    /**
     * the categories with specific implementations
     */
    DATE("Date", "Date", RecognizerType.OTHER);

    private String displayName;

    private String description;

    private RecognizerType recognizerType;

    public enum RecognizerType {
        REGEX,
        OPEN_INDEX,
        CLOSED_INDEX,
        OTHER
    }

    /**
     * SemanticCategoryEnum constructor.
     * 
     * @param displayName the category shown in Semantic Discovery wizard
     * @param description the description of the category
     */
    private SemanticCategoryEnum(String displayName, String description, RecognizerType type) {
        this.displayName = displayName;
        this.description = description;
        this.recognizerType = type;
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

    public RecognizerType getRecognizerType() {
        return recognizerType;
    }

    public static SemanticCategoryEnum getCategoryById(String catId) {
        try {
            return valueOf(catId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
