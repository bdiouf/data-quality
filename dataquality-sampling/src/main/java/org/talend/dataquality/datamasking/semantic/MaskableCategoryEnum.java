package org.talend.dataquality.datamasking.semantic;

import org.talend.dataquality.datamasking.FunctionType;

/**
 * Enumeration of all maskable semantic categories.
 */
public enum MaskableCategoryEnum {

    ADDRESS_LINE("Address Line", FunctionType.MASK_ADDRESS),
    CITY("City", FunctionType.MASK_EMAIL),
    COMPANY("Company", FunctionType.GENERATE_FROM_LIST_STRING),
    EMAIL("Email", FunctionType.MASK_EMAIL),
    FIRST_NAME("First Name", FunctionType.GENERATE_FROM_LIST_STRING),
    FR_COMMUNE("FR Commune", FunctionType.GENERATE_FROM_LIST_STRING),
    FULL_NAME("Full Name", FunctionType.REPLACE_CHARACTERS),
    IPv4_ADDRESS("IPv4 Address", FunctionType.REPLACE_NUMERIC_STRING),
    IPv6_ADDRESS("IPv6 Address", FunctionType.REPLACE_CHARACTERS),
    JOB_TITLE("Job Title", FunctionType.GENERATE_FROM_LIST_STRING),
    LOCALIZATION("Localization", FunctionType.REPLACE_NUMERIC_STRING),
    LOCATION_COORDINATE("Location Coordinate", FunctionType.REPLACE_NUMERIC_STRING),
    MAC_ADDRESS("MAC Address", FunctionType.REPLACE_CHARACTERS),
    ORGANIZATION("Organization", FunctionType.GENERATE_FROM_LIST_STRING),
    PASSPORT("Passport", FunctionType.REPLACE_CHARACTERS),

    US_PHONE("US Phone", FunctionType.GENERATE_PHONE_NUMBER_US),
    FR_PHONE("FR Phone", FunctionType.GENERATE_PHONE_NUMBER_FRENCH),
    UK_PHONE("UK Phone", FunctionType.GENERATE_PHONE_NUMBER_UK),
    DE_PHONE("DE Phone", FunctionType.GENERATE_PHONE_NUMBER_GERMANY),

    US_POSTAL_CODE("US Postal Code", FunctionType.REPLACE_NUMERIC_STRING),
    FR_POSTAL_CODE("FR Postal Code", FunctionType.REPLACE_NUMERIC_STRING),
    DE_POSTAL_CODE("DE Postal Code", FunctionType.REPLACE_NUMERIC_STRING),
    UK_POSTAL_CODE("UK Postal Code", FunctionType.REPLACE_NUMERIC_STRING),
    BE_POSTAL_CODE("BE Postal Code", FunctionType.REPLACE_NUMERIC_STRING),
    FR_CODE_COMMUNE_INSEE("FR Insee Code", FunctionType.REPLACE_NUMERIC_STRING),

    US_SSN("US Social Security Number", FunctionType.GENERATE_SSN_US),
    FR_SSN("FR Social Security Number", FunctionType.GENERATE_SSN_FRENCH),
    UK_SSN("UK Social Security Number", FunctionType.GENERATE_SSN_UK),

    MASTERCARD("Mastercard Credit Card", FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING),
    US_CREDIT_CARD("AmEx Credit Card", FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING),
    VISACARD("Visa Credit Card", FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING);

    private String displayName;

    private FunctionType functionType;

    /**
     * SemanticCategoryEnum constructor.
     * 
     * @param displayName the category shown in Semantic Discovery wizard
     * @param description the description of the category
     */
    private MaskableCategoryEnum(String displayName, FunctionType functionType) {
        this.displayName = displayName;
        this.functionType = functionType;
    }

    public String getId() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public FunctionType getFunctionType() {
        return functionType;
    }

    public static MaskableCategoryEnum getCategoryById(String catId) {
        try {
            return valueOf(catId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
