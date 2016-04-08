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
package org.talend.dataquality.standardization.phone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class PhoneNumberHandlerTest {

    private String FR_NUM_1 = "+33656965822"; //$NON-NLS-1$

    private String FR_NUM_2 = "+33(0)147554323"; //$NON-NLS-1$

    private String FR_NUM_3 = "000147554323"; //$NON-NLS-1$

    private String FR_NUM_4 = "00(0)147554323"; //$NON-NLS-1$

    private String FR_NUM_5 = "0662965822"; //$NON-NLS-1$

    private String US_NUM_1 = "+1-541-754-3010"; //$NON-NLS-1$

    private String US_NUM_2 = "1-541-754-3010"; //$NON-NLS-1$

    private String US_NUM_3 = "001-541-754-3010"; //$NON-NLS-1$

    private String US_NUM_4 = "(541) 754-3010"; //$NON-NLS-1$

    private String US_NUM_5 = "754-3010"; //$NON-NLS-1$

    private String US_NUM_6 = "191 541 754 3010"; //$NON-NLS-1$

    private String US_NUM_7 = "(724) 203-2300"; //$NON-NLS-1$

    private String DE_NUM_1 = "+49-89-636-48018"; //$NON-NLS-1$

    private String DE_NUM_2 = "19-49-89-636-48018"; //$NON-NLS-1$

    private String DE_NUM_3 = "(089) / 636-48018"; //$NON-NLS-1$

    private String DE_NUM_4 = "636-48018"; //$NON-NLS-1$

    private String REGCODE_FR = "FR"; //$NON-NLS-1$

    private String REGCODE_US = "US"; //$NON-NLS-1$

    private String REGCODE_DE = "DE"; //$NON-NLS-1$

    private PhoneNumberHandler phoneNumberHandler = new PhoneNumberHandler();

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandler#isValidPhoneNumber(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testIsValidPhoneNumber() {
        assertTrue(phoneNumberHandler.isValidPhoneNumber(FR_NUM_1, REGCODE_FR));
        assertTrue(phoneNumberHandler.isValidPhoneNumber(FR_NUM_2, REGCODE_FR));
        assertTrue(phoneNumberHandler.isValidPhoneNumber(FR_NUM_5, REGCODE_FR));
        assertFalse(phoneNumberHandler.isValidPhoneNumber(FR_NUM_3, REGCODE_FR));
        assertFalse(phoneNumberHandler.isValidPhoneNumber(FR_NUM_4, REGCODE_FR));

        assertTrue(phoneNumberHandler.isValidPhoneNumber(US_NUM_1, REGCODE_US));
        assertTrue(phoneNumberHandler.isValidPhoneNumber(US_NUM_2, REGCODE_US));
        assertTrue(phoneNumberHandler.isValidPhoneNumber(US_NUM_4, REGCODE_US));
        assertTrue(phoneNumberHandler.isValidPhoneNumber(US_NUM_7, REGCODE_US));
        assertFalse(phoneNumberHandler.isValidPhoneNumber(US_NUM_3, REGCODE_US));
        assertFalse(phoneNumberHandler.isValidPhoneNumber(US_NUM_6, REGCODE_US));
        assertFalse(phoneNumberHandler.isValidPhoneNumber(US_NUM_5, REGCODE_US));

        assertTrue(phoneNumberHandler.isValidPhoneNumber(DE_NUM_1, REGCODE_DE));
        assertTrue(phoneNumberHandler.isValidPhoneNumber(DE_NUM_3, REGCODE_DE));
        assertTrue(phoneNumberHandler.isValidPhoneNumber(DE_NUM_4, REGCODE_DE));
        assertFalse(phoneNumberHandler.isValidPhoneNumber(DE_NUM_2, REGCODE_DE));

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandler#parseToPhoneNumber(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testParseToPhoneNumber() {
        assertNull(phoneNumberHandler.parseToPhoneNumber(null, REGCODE_FR));
        assertNull(phoneNumberHandler.parseToPhoneNumber("", REGCODE_FR)); //$NON-NLS-1$
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(FR_NUM_1, null));

        assertNotNull(phoneNumberHandler.parseToPhoneNumber(FR_NUM_1, REGCODE_FR));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(FR_NUM_2, REGCODE_FR));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(FR_NUM_5, REGCODE_FR));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(FR_NUM_3, REGCODE_FR));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(FR_NUM_4, REGCODE_FR));

        assertNotNull(phoneNumberHandler.parseToPhoneNumber(US_NUM_1, REGCODE_US));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(US_NUM_2, REGCODE_US));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(US_NUM_4, REGCODE_US));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(US_NUM_7, REGCODE_US));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(US_NUM_3, REGCODE_US));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(US_NUM_6, REGCODE_US));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(US_NUM_5, REGCODE_US));

        assertNotNull(phoneNumberHandler.parseToPhoneNumber(DE_NUM_1, REGCODE_DE));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(DE_NUM_3, REGCODE_DE));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(DE_NUM_4, REGCODE_DE));
        assertNotNull(phoneNumberHandler.parseToPhoneNumber(DE_NUM_2, REGCODE_DE));

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandler#isPossiblePhoneNumber(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testIsPossiblePhoneNumberObjectString() {
        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(FR_NUM_1, null));
        assertFalse(phoneNumberHandler.isPossiblePhoneNumber(FR_NUM_3, null));
        assertFalse(phoneNumberHandler.isPossiblePhoneNumber(null, REGCODE_FR));

        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(FR_NUM_1, REGCODE_FR));
        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(FR_NUM_2, REGCODE_FR));
        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(FR_NUM_5, REGCODE_FR));
        assertFalse(phoneNumberHandler.isPossiblePhoneNumber(FR_NUM_3, REGCODE_FR));
        assertFalse(phoneNumberHandler.isPossiblePhoneNumber(FR_NUM_4, REGCODE_FR));

        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(US_NUM_1, REGCODE_US));
        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(US_NUM_2, REGCODE_US));
        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(US_NUM_4, REGCODE_US));
        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(US_NUM_7, REGCODE_US));
        assertFalse(phoneNumberHandler.isPossiblePhoneNumber(US_NUM_3, REGCODE_US));
        assertFalse(phoneNumberHandler.isPossiblePhoneNumber(US_NUM_6, REGCODE_US));

        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(US_NUM_5, REGCODE_US));

        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(DE_NUM_1, REGCODE_DE));
        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(DE_NUM_3, REGCODE_DE));
        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(DE_NUM_4, REGCODE_DE));

        assertTrue(phoneNumberHandler.isPossiblePhoneNumber(DE_NUM_2, REGCODE_DE));

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandler#formatE164(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatE164() {
        assertEquals("+33656965822", phoneNumberHandler.formatE164(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33147554323", phoneNumberHandler.formatE164(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33662965822", phoneNumberHandler.formatE164(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("+15417543010", phoneNumberHandler.formatE164(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+15417543010", phoneNumberHandler.formatE164(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+15417543010", phoneNumberHandler.formatE164(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("+498963648018", phoneNumberHandler.formatE164(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+4919498963648018", phoneNumberHandler.formatE164(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+498963648018", phoneNumberHandler.formatE164(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandler#formatInternational(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatInternational() {

        assertEquals("+33 6 56 96 58 22", phoneNumberHandler.formatInternational(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33 1 47 55 43 23", phoneNumberHandler.formatInternational(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("+33 6 62 96 58 22", phoneNumberHandler.formatInternational(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("+1 541-754-3010", phoneNumberHandler.formatInternational(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+1 541-754-3010", phoneNumberHandler.formatInternational(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("+1 541-754-3010", phoneNumberHandler.formatInternational(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("+49 89 63648018", phoneNumberHandler.formatInternational(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+49 19498963648018", phoneNumberHandler.formatInternational(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("+49 89 63648018", phoneNumberHandler.formatInternational(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandler#formatNational(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatNational() {

        System.out.println("Nationnal-2--" + phoneNumberHandler.formatNational(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("06 56 96 58 22", phoneNumberHandler.formatNational(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("01 47 55 43 23", phoneNumberHandler.formatNational(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("06 62 96 58 22", phoneNumberHandler.formatNational(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("(541) 754-3010", phoneNumberHandler.formatNational(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("(541) 754-3010", phoneNumberHandler.formatNational(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("(541) 754-3010", phoneNumberHandler.formatNational(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("089 63648018", phoneNumberHandler.formatNational(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("19498963648018", phoneNumberHandler.formatNational(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("089 63648018", phoneNumberHandler.formatNational(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandler#formatRFC396(java.lang.Object, java.lang.String)}
     * .
     */
    @Test
    public void testFormatRFC396() {

        assertEquals("tel:+33-6-56-96-58-22", phoneNumberHandler.formatRFC396(FR_NUM_1, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("tel:+33-1-47-55-43-23", phoneNumberHandler.formatRFC396(FR_NUM_2, REGCODE_FR)); //$NON-NLS-1$
        assertEquals("tel:+33-6-62-96-58-22", phoneNumberHandler.formatRFC396(FR_NUM_5, REGCODE_FR)); //$NON-NLS-1$

        assertEquals("tel:+1-541-754-3010", phoneNumberHandler.formatRFC396(US_NUM_1, REGCODE_US)); //$NON-NLS-1$
        assertEquals("tel:+1-541-754-3010", phoneNumberHandler.formatRFC396(US_NUM_2, REGCODE_US)); //$NON-NLS-1$
        assertEquals("tel:+1-541-754-3010", phoneNumberHandler.formatRFC396(US_NUM_4, REGCODE_US)); //$NON-NLS-1$

        assertEquals("tel:+49-89-63648018", phoneNumberHandler.formatRFC396(DE_NUM_1, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("tel:+49-19498963648018", phoneNumberHandler.formatRFC396(DE_NUM_2, REGCODE_DE)); //$NON-NLS-1$
        assertEquals("tel:+49-89-63648018", phoneNumberHandler.formatRFC396(DE_NUM_3, REGCODE_DE)); //$NON-NLS-1$

    }

    /**
     * Test method for {@link org.talend.dataquality.standardization.phone.PhoneNumberHandler#getSupportedRegions()}.
     */
    @Test
    public void testGetSupportedRegions() {
        assertEquals(244, phoneNumberHandler.getSupportedRegions().size());

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.phone.PhoneNumberHandler#getCountryCodeForRegion(java.lang.String)}
     * .
     */
    @Test
    public void testExtractCountryCode() {

        assertEquals(33, phoneNumberHandler.extractCountrycode(FR_NUM_1));
        assertEquals(33, phoneNumberHandler.extractCountrycode(FR_NUM_2));
        assertEquals(0, phoneNumberHandler.extractCountrycode(FR_NUM_5));
        assertEquals(0, phoneNumberHandler.extractCountrycode(FR_NUM_3));
        assertEquals(0, phoneNumberHandler.extractCountrycode(FR_NUM_4));

        assertEquals(1, phoneNumberHandler.extractCountrycode(US_NUM_1));
        assertEquals(0, phoneNumberHandler.extractCountrycode(US_NUM_2));
        assertEquals(0, phoneNumberHandler.extractCountrycode(US_NUM_4));
        assertEquals(0, phoneNumberHandler.extractCountrycode(US_NUM_7));
        assertEquals(0, phoneNumberHandler.extractCountrycode(US_NUM_3));
        assertEquals(0, phoneNumberHandler.extractCountrycode(US_NUM_6));
        assertEquals(0, phoneNumberHandler.extractCountrycode(US_NUM_5));

        assertEquals(49, phoneNumberHandler.extractCountrycode(DE_NUM_1));
        assertEquals(0, phoneNumberHandler.extractCountrycode(DE_NUM_3));
        assertEquals(0, phoneNumberHandler.extractCountrycode(DE_NUM_4));
        assertEquals(0, phoneNumberHandler.extractCountrycode(DE_NUM_2));

    }

    @Test
    public void testEtractRegionCode() {
        assertEquals("FR", phoneNumberHandler.extractRegionCode(FR_NUM_1)); //$NON-NLS-1$
        assertEquals("FR", phoneNumberHandler.extractRegionCode(FR_NUM_2)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(FR_NUM_5));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(FR_NUM_3));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(FR_NUM_4));

        assertEquals("US", phoneNumberHandler.extractRegionCode(US_NUM_1)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(US_NUM_2));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(US_NUM_4));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(US_NUM_7));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(US_NUM_3));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(US_NUM_6));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(US_NUM_5));

        assertEquals("DE", phoneNumberHandler.extractRegionCode(DE_NUM_1)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(DE_NUM_3));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(DE_NUM_4));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.extractRegionCode(DE_NUM_2));

    }

    @Test
    public void testGetCarrierNameForNumber() {
        PhoneNumber UK_MOBILE1 = new PhoneNumber().setCountryCode(244).setNationalNumber(917654321L);
        System.out.println("aa5:" + PhoneNumberToCarrierMapper.getInstance().getNameForNumber(UK_MOBILE1, Locale.ENGLISH)); //$NON-NLS-1$

        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(FR_NUM_2, Locale.FRANCE));
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(FR_NUM_3, Locale.FRANCE));
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(FR_NUM_4, Locale.FRANCE));
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(FR_NUM_5, Locale.FRANCE));
        // //
        // System.out.println("aa2:" + phoneNumberHandler.getCarrierNameForNumber(US_NUM_1, Locale.US));
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(US_NUM_2, Locale.US));
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(US_NUM_3, Locale.US));
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(US_NUM_4, Locale.US));
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(US_NUM_5, Locale.US));
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(US_NUM_6, Locale.US));
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(US_NUM_7, Locale.US));
        // //
        // System.out.println("aa3:" + phoneNumberHandler.getCarrierNameForNumber(DE_NUM_1, Locale.GERMANY));
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(DE_NUM_2, Locale.GERMANY));
        // System.out.println("aa--" + phoneNumberHandler.getCarrierNameForNumber(DE_NUM_3, Locale.GERMANY) + "--cc");
        // System.out.println(phoneNumberHandler.getCarrierNameForNumber(DE_NUM_4, Locale.GERMANY));

    }

    @Test
    public void testgetGeocoderDescriptionForNumber() {

        // System.out.println("china--" + phoneNumberHandler.getGeocoderDescriptionForNumber("7927234582",
        // Locale.SIMPLIFIED_CHINESE));
        assertEquals("北京市", phoneNumberHandler.getGeocoderDescriptionForNumber("1065267475", Locale.SIMPLIFIED_CHINESE)); //$NON-NLS-1$//$NON-NLS-2$
        assertEquals("France", phoneNumberHandler.getGeocoderDescriptionForNumber(FR_NUM_1, Locale.FRANCE)); //$NON-NLS-1$
        assertEquals("Paris", phoneNumberHandler.getGeocoderDescriptionForNumber(FR_NUM_2, Locale.FRANCE)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.getGeocoderDescriptionForNumber(FR_NUM_3, Locale.FRANCE));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.getGeocoderDescriptionForNumber(FR_NUM_4, Locale.FRANCE));
        assertEquals("France", phoneNumberHandler.getGeocoderDescriptionForNumber(FR_NUM_5, Locale.FRANCE)); //$NON-NLS-1$

        assertEquals("Corvallis, OR", phoneNumberHandler.getGeocoderDescriptionForNumber(US_NUM_1, Locale.US)); //$NON-NLS-1$
        assertEquals("Corvallis, OR", phoneNumberHandler.getGeocoderDescriptionForNumber(US_NUM_2, Locale.US)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.getGeocoderDescriptionForNumber(US_NUM_3, Locale.US));
        assertEquals("Corvallis, OR", phoneNumberHandler.getGeocoderDescriptionForNumber(US_NUM_4, Locale.US)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.getGeocoderDescriptionForNumber(US_NUM_5, Locale.US));
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.getGeocoderDescriptionForNumber(US_NUM_6, Locale.US));
        assertEquals("Pennsylvania", phoneNumberHandler.getGeocoderDescriptionForNumber(US_NUM_7, Locale.US)); //$NON-NLS-1$

        assertEquals("München", phoneNumberHandler.getGeocoderDescriptionForNumber(DE_NUM_1, Locale.GERMANY)); //$NON-NLS-1$
        assertEquals(StringUtils.EMPTY, phoneNumberHandler.getGeocoderDescriptionForNumber(DE_NUM_2, Locale.GERMANY));
        assertEquals("München", phoneNumberHandler.getGeocoderDescriptionForNumber(DE_NUM_3, Locale.GERMANY)); //$NON-NLS-1$
        assertEquals("Nußbach Pfalz", phoneNumberHandler.getGeocoderDescriptionForNumber(DE_NUM_4, Locale.GERMANY)); //$NON-NLS-1$

    }

    @Test
    public void testGetCountryCodeForRegion() {
        assertEquals(0, phoneNumberHandler.getCountryCodeForRegion(null));
        assertEquals(0, phoneNumberHandler.getCountryCodeForRegion("")); //$NON-NLS-1$
        assertEquals(33, phoneNumberHandler.getCountryCodeForRegion("FR")); //$NON-NLS-1$
        assertEquals(1, phoneNumberHandler.getCountryCodeForRegion("US")); //$NON-NLS-1$
        assertEquals(86, phoneNumberHandler.getCountryCodeForRegion("CN")); //$NON-NLS-1$

    }
}
