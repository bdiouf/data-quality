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
package org.talend.dataquality.semantic.classifier;

/**
 * Enumeration of all supported semantic categories.
 * <p/>
 * In most cases, the keys of the categories should not be changed.
 */
public enum SemanticCategoryEnum {
    UNKNOWN("", "Blank, Null and those who have no other semantic category"),

    /**
     * the categories defined in Data Dictionary index
     */
    ANIMAL("Animal", "Animal (multilingual)"),
    AIRPORT("Airport", "Airport name"),
    AIRPORT_CODE("Airport Code", "Airport name"),
    CITY("City", "City name (multilingual)"),
    CIVILITY("Civility", "Civility (multilingual)"),
    CONTINENT("Continent", "Continent name (multilingual)"),
    CONTINENT_CODE("Continent Code", "Continent code"),
    COUNTRY("Country", "Country Name (EN+FR)"),
    COUNTRY_CODE_ISO2("Country Code ISO2", "Country code of ISO3166-1 Alpha-2"),
    COUNTRY_CODE_ISO3("Country Code ISO3", "Country code of ISO3166-1 Alpha-3"),
    CURRENCY_NAME("Currency Name", "Currency name (EN)"),
    CURRENCY_CODE("Currency Code", "Currency alphabetic code"),
    HR_DEPARTMENT("HR Department", "Department or service name in company"),
    FIRST_NAME("First Name", "First name"),
    GENDER("Gender", "Gender (Multilingual)"),
    JOB_TITLE("Job Title", "Airport name"),
    MONTH("Month", "Month (Multilingual)"),
    STREET_TYPE("Street Type", "Street type (multilingual)"),
    WEEKDAY("Weekday", "Weekday (multilingual)"),

    /**
     * the categories defined in Keyword index
     */
    ADDRESS_LINE("Address Line", "Address line which contains STREET_TYPE keyword"),
    FULL_NAME("Full Name", "Full name which contains CIVILITY keyword"),

    /**
     * the categories defined in categorizer.json
     */
    AT_VAT_NUMBER("AT VAT Number", "Austria VAT number"),
    BANK_ROUTING_TRANSIT_NUMBER("Bank Routing Transit Number", "Bank routing transit Number"),
    BE_POSTAL_CODE("BE Postal Code", "Belgium postal code"),
    BG_VAT_NUMBER("BG VAT Number", "Bulgaria VAT number"),
    COLOR_HEX_CODE("Color Hex Code", "Color hexadecimal code"),

    // DE
    DE_PHONE("DE Phone", "German phone number"),
    DE_POSTAL_CODE("DE Postal Code", "German postal code"),

    EMAIL("Email", "email"),

    // EN
    EN_MONEY_AMOUNT("Money Amount (EN)", "Amount of money in English format"),
    EN_MONTH("EN Month", "Month in English"),
    EN_MONTH_ABBREV("EN Month Abbrev", "Month English abbreviation"),
    EN_WEEK_DAY("EN Week Day", "Week Day or their abbreviation"),

    // FR
    FR_MONEY_AMOUNT("Money Amount (FR)", "Amount of money in French format"),
    FR_CODE_COMMUNE_INSEE("FR Insee Code", "French Insee code of cities with Corsica and colonies"),
    FR_PHONE("FR Phone", "French Phone Number"),
    FR_POSTAL_CODE("FR Postal Code", "French postal code"),
    FR_SSN("FR Social Security Number", "French Social Security Number"),
    FR_VAT_NUMBER("FR VAT Number", "French VAT number"),

    GPS_COORDINATE("GPS Coordinate", "Google Maps style GPS Decimal format"),
    IPv4_ADDRESS("IPv4 Address", "IPv4 address"),
    IPv6_ADDRESS("IPv6 Address", "IPv6 address"),
    ISBN_10("ISBN-10", "International Standard Book Number 10 digits. Such as ISBN 2-711-79141-6"),
    ISBN_13("ISBN-13", "International Standard Book Number 13 digits."),
    LOCALIZATION("Localization", "Longitude or latitude coordinates with at least meter precision"),
    LOCATION_COORDINATE(
                        "Location Coordinate",
                        "Latitude and longitude coordinates separated by a comma in the form: N 0:59:59.99,E 0:59:59.99"),
    MAC_ADDRESS("MAC Address", "MAC Address."),
    MASTERCARD("Mastercard Credit Card", "MasterCard Credit card"),
    PASSPORT("Passport", "Passport number"),
    SEDOL("SEDOL", "Stock Exchange Daily Official List"),
    SE_SSN("SE Social Security Number", "Swedish person number"),
    TIME("Time", "Day time in 24h format HH:mm"),
    URL("URL", "Web site URL"),

    // UK
    UK_PHONE("UK Phone", "UK phone number"),
    UK_POSTAL_CODE("UK Postal Code", "UK postal code"),
    UK_SSN(
           "UK Social Security Number",
           "national identification number, national identity number, or national insurance number generally called an NI Number (NINO)"),

    // US
    US_CREDIT_CARD("AmEx Credit Card", "US American Express Credit card"),
    US_PHONE("US Phone", "US Phone Number"),
    US_POSTAL_CODE("US Postal Code", "US postal code"),
    US_SSN("US Social Security Number", "US Social Security number"),
    US_STATE("US State", "US states"),
    US_STATE_CODE("US State Code", "US State code"),

    WEB_DOMAIN("Web Domain", "Web site domain"),

    /**
     * the categories with specific implementations
     */
    DATE("Date", "Date");

    private String displayName;

    private String description;

    /**
     * SemanticCategoryEnum constructor.
     * 
     * @param displayName the category shown in Semantic Discovery wizard
     * @param description the description of the category
     */
    private SemanticCategoryEnum(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
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

    public static SemanticCategoryEnum getCategoryById(String catId) {
        try {
            return valueOf(catId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
