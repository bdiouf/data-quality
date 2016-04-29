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
package org.talend.dataquality.datamasking.semantic;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Test;

public class ValueDataMaskerTest {

    private static final Map<String[], String> EXPECTED_MASKED_VALUES = new LinkedHashMap<String[], String>() {

        private static final long serialVersionUID = 1L;

        {

            // 0. UNKNOWN
            put(new String[] { "92000", "UNKNOWN", "numeric" }, ".*");
            put(new String[] { "2023-06-07", "UNKNOWN", "date" }, ".*");
            put(new String[] { "sdkjs@talend.com", "UNKNOWN", "string" }, ".*");

            // 1. FIRST_NAME
            put(new String[] { "John", "FIRST_NAME", "string" }, ".*");

            // 2. LAST_NAME
            put(new String[] { "Dupont", "LAST_NAME", "string" }, ".*");

            // 3. EMAIL
            put(new String[] { "sdkjs@talend.com", MaskableCategoryEnum.EMAIL.name(), "String" }, ".*@talend.com");

            // 4. PHONE
            put(new String[] { "0123456789", MaskableCategoryEnum.US_PHONE.name(), "String" }, ".*");
            put(new String[] { "321938", MaskableCategoryEnum.FR_PHONE.name(), "String" }, ".*");
            put(new String[] { "4444444", MaskableCategoryEnum.DE_PHONE.name(), "String" }, ".*");
            put(new String[] { "666666666", MaskableCategoryEnum.UK_PHONE.name(), "String" }, ".*");

            // 5. JOB_TITLE
            put(new String[] { "CEO", MaskableCategoryEnum.JOB_TITLE.name(), "String" }, ".*");

            // 6. ADDRESS_LINE
            put(new String[] { "9 Rue Pagès", MaskableCategoryEnum.ADDRESS_LINE.name(), "String" }, ".*");

            // 7 POSTAL_CODE
            put(new String[] { "37218-1324", MaskableCategoryEnum.US_POSTAL_CODE.name(), "String" }, ".*");
            put(new String[] { "92150", MaskableCategoryEnum.FR_POSTAL_CODE.name(), "String" }, ".*");
            put(new String[] { "63274", MaskableCategoryEnum.DE_POSTAL_CODE.name(), "String" }, ".*");
            put(new String[] { "AT1 3BW", MaskableCategoryEnum.UK_POSTAL_CODE.name(), "String" }, ".*");


            // 8 ORGANIZATION

            // 9 COMPANY

            // 10 CREDIT_CARD
            put(new String[] { "5300 1232 8732 8318", MaskableCategoryEnum.US_CREDIT_CARD.name(), "String" }, ".*");
            put(new String[] { "5300 1232 8732 8318", MaskableCategoryEnum.MASTERCARD.name(), "String" }, ".*");
            put(new String[] { "4300 1232 8732 8318", MaskableCategoryEnum.VISACARD.name(), "String" }, ".*");

            // 11 SSN
            put(new String[] { "728931789", MaskableCategoryEnum.US_SSN.name(), "String" }, ".*");
            put(new String[] { "17612 38293 28232", MaskableCategoryEnum.FR_SSN.name(), "String" }, ".*");
            put(new String[] { "634217823", MaskableCategoryEnum.UK_SSN.name(), "String" }, ".*");

            // Company
            put(new String[] { "Talend", MaskableCategoryEnum.COMPANY.name(), "String" }, ".*");
            // First Name
            put(new String[] { "John", MaskableCategoryEnum.FIRST_NAME.name(), "String" }, ".*");
            // Last Name
            put(new String[] { "Dupont", MaskableCategoryEnum.LAST_NAME.name(), "String" }, ".*");
            // FR Commune
            put(new String[] { "Amancey", MaskableCategoryEnum.FR_COMMUNE.name(), "String" }, ".*");
            // Job Title
            put(new String[] { "developer", MaskableCategoryEnum.JOB_TITLE.name(), "String" }, ".*");
            // Organization
            put(new String[] { "Kiva", MaskableCategoryEnum.ORGANIZATION.name(), "String" }, ".*");
        }
    };

    /**
     * Test method for {@link org.talend.dataquality.datamasking.DataMasker#process(java.lang.Object, boolean)}.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test
    public void testProcess() throws InstantiationException, IllegalAccessException {

        for (String[] input : EXPECTED_MASKED_VALUES.keySet()) {
            String inputValue = input[0];
            String semanticCategory = input[1];
            String dataType = input[2];

            System.out.print("[" + semanticCategory + "]\n\t" + inputValue + " => ");
            final ValueDataMasker masker = new ValueDataMasker(semanticCategory, dataType);

            Pattern pattern = Pattern.compile(EXPECTED_MASKED_VALUES.get(input));
            System.out.println(masker.maskValue(inputValue));
            assertTrue("Test faild on [" + inputValue + "]", pattern.matcher(masker.maskValue(inputValue)).matches());

        }

        // Assert.assertNotEquals(city, masker.process(city));
        // masker should generate a city name
        // Assert the masked value is in a list of city names

        // categories to mask
        // First names, last names, email, IP address (v4, v6), localization, GPS coordinates, phone
        // Job title , street, address, zipcode, organization, company, full name, credit card number, account number,
        //

        // for these categories, here are the default functions to use:
        // first name -> another first name (from a fixed list loaded from a data file in a resource folder)
        // last name -> another last name (from a fixed list)
        // email -> mask local part (MaskEmail function)
        // phone -> keep 3 first digits and replace last digits
        // Job title -> another job title (from a fixed list)
        // street -> use MaskAddress
        // zipCode -> replace All digits
        // organization -> another organization (from a fixed list)
        // company -> another company (from a fixed list)
        // credit card -> generate a new one
        // account number -> generate a new one
        //
        // Assertions: masked data must never be identical to original data (don't use random seed for the random
        // generator to check that)
        //

        // data types to mask
        // date, string, numeric

        // create a ValueDataMasker for data that have no semantic category
        // use ValueDataMasker masker = SemanticCategoryMaskerFactory.createMasker(dataType);

        // here are the default functions to use for the different types:
        // date -> DateVariance with parameter 61 (meaning two months)
        // string -> use ReplaceAll
        // numeric -> use NumericVariance

    }
}
