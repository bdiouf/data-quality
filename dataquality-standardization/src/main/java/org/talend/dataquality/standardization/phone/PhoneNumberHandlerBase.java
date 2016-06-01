// ============================================================================
//
// Copyright (C) 2006-2016 qiongli Inc. - www.qiongli.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.qiongli.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to qiongli SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.standardization.phone;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberToTimeZonesMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class PhoneNumberHandlerBase {

    private static PhoneNumberUtil phoneUtil = null;

    private static final Logger log = Logger.getLogger(PhoneNumberHandlerBase.class);

    public PhoneNumberHandlerBase() {
        phoneUtil = PhoneNumberUtil.getInstance();
    }

    /**
     * 
     * Parses a string or number and returns it in proto buffer format.
     * 
     * @param data A String or Number
     * @param regionCode we are expecting the number to be from. This is only used if the number being parsed is not
     * written in international format. The country_code for the number in this case would be stored as that of the
     * default region supplied. If the number is guaranteed to start with a '+' followed by the country calling code,
     * then "ZZ" or null can be supplied. like as "+86 12345678912"
     * @return
     */
    protected PhoneNumber parseToPhoneNumber(Object data, String regionCode) {
        if (data == null || StringUtils.isBlank(data.toString())) {
            return null;
        }
        PhoneNumber phonenumber = null;
        try {
            phonenumber = phoneUtil.parse(data.toString(), regionCode);
        } catch (Exception e) {
            log.error("Phone number parsing exception with " + data, e); //$NON-NLS-1$
            return null;
        }
        return phonenumber;
    }

    /**
     * 
     * whether a phone number is valid for a certain region.
     * 
     * @param data the data that we want to validate
     * @param regionCode the regionCode that we want to validate the phone number from
     * @return a boolean that indicates whether the number is of a valid pattern
     */
    public boolean isValidPhoneNumber(Object data, String regionCode) {
        PhoneNumber phonenumber = parseToPhoneNumber(data, regionCode);
        if (phonenumber == null) {
            return false;
        }
        return phoneUtil.isValidNumberForRegion(phonenumber, regionCode);
    }

    /**
     * 
     * Check whether a phone number is a possible number given a number in the form of a object, and the region where
     * the number could be dialed from.
     * 
     * @param data the data that we want to validate
     * @param regionCode the regionCode that we are expecting the number to be dialed from.
     * @return
     */
    public boolean isPossiblePhoneNumber(Object data, String regionCode) {
        if (data == null || StringUtils.isBlank(data.toString())) {
            return false;
        }
        return phoneUtil.isPossibleNumber(data.toString(), regionCode);

    }

    /**
     * 
     * Formats a phone number to E164 form .
     * 
     * @param data the data that we want to validate
     * @param regionCode the regionCode that we are expecting the number to be dialed from
     * @return the formatted phone number like as "+12423651234"
     */
    public String formatE164(Object data, String regionCode) {
        PhoneNumber phonemuber = parseToPhoneNumber(data, regionCode);
        if (phonemuber == null) {
            return StringUtils.EMPTY;
        }
        return phoneUtil.format(phonemuber, PhoneNumberFormat.E164);
    }

    /**
     * 
     * Formats a phone number to International form.
     * 
     * @param data the data that we want to validate
     * @param regionCode the regionCode that we are expecting the number to be dialed from
     * @return the formatted phone number like as "+1 242-365-1234"
     */
    public String formatInternational(Object data, String regionCode) {
        PhoneNumber phonemuber = parseToPhoneNumber(data, regionCode);
        if (phonemuber == null) {
            return StringUtils.EMPTY;
        }
        return phoneUtil.format(phonemuber, PhoneNumberFormat.INTERNATIONAL);
    }

    /**
     * 
     * Formats a phone number to National form .
     * 
     * @param data the data that we want to validate
     * @param regionCode the regionCode that we are expecting the number to be dialed from
     * @return the formatted phone number like as "(242) 365-1234"
     */
    public String formatNational(Object data, String regionCode) {
        PhoneNumber phonemuber = parseToPhoneNumber(data, regionCode);
        if (phonemuber == null) {
            return StringUtils.EMPTY;
        }
        return phoneUtil.format(phonemuber, PhoneNumberFormat.NATIONAL);
    }

    /**
     * 
     * Formats a phone number to RFC396 form .
     * 
     * @param data the data that we want to validate
     * @param regionCode the regionCode that we are expecting the number to be dialed from
     * @return the formatted phone number like as "tel:+1-242-365-1234"
     */
    public String formatRFC396(Object data, String regionCode) {
        PhoneNumber phonemuber = parseToPhoneNumber(data, regionCode);
        if (phonemuber == null) {
            return StringUtils.EMPTY;
        }
        return phoneUtil.format(phonemuber, PhoneNumberFormat.RFC3966);
    }

    /**
     * 
     * get all supported regions.
     * 
     * @return
     */
    public Set<String> getSupportedRegions() {
        return phoneUtil.getSupportedRegions();
    }

    /**
     * 
     * Get country code by the region code
     * 
     * @param regionCode
     * @return
     */
    public int getCountryCodeForRegion(String regionCode) {
        return phoneUtil.getCountryCodeForRegion(regionCode);
    }

    /**
     * 
     * DOC qiongli Comment method "getPhoneNumberType".
     * 
     * @param data
     * @param regionCode
     * @return
     */
    public PhoneNumberTypeEnum getPhoneNumberType(Object data, String regionCode) {
        PhoneNumber number = parseToPhoneNumber(data, regionCode);
        if (number != null) {
            PhoneNumberType numberType = phoneUtil.getNumberType(number);
            switch (numberType) {
            case FIXED_LINE:
                return PhoneNumberTypeEnum.FIXED_LINE;
            case MOBILE:
                return PhoneNumberTypeEnum.MOBILE;
            case FIXED_LINE_OR_MOBILE:
                return PhoneNumberTypeEnum.FIXED_LINE_OR_MOBILE;
            case PAGER:
                return PhoneNumberTypeEnum.PAGER;
            case PERSONAL_NUMBER:
                return PhoneNumberTypeEnum.PERSONAL_NUMBER;
            case TOLL_FREE:
                return PhoneNumberTypeEnum.TOLL_FREE;
            case PREMIUM_RATE:
                return PhoneNumberTypeEnum.PREMIUM_RATE;
            case SHARED_COST:
                return PhoneNumberTypeEnum.SHARED_COST;
            case UAN:
                return PhoneNumberTypeEnum.UAN;
            case VOICEMAIL:
                return PhoneNumberTypeEnum.VOICEMAIL;
            case VOIP:
                return PhoneNumberTypeEnum.VOIP;
            default:

            }
        }
        return PhoneNumberTypeEnum.UNKNOWN;
    }

    /**
     * 
     * whether a phone number contain a valid region.
     * 
     * @param data a phone number String or number. the data string must be guaranteed to start with a '+' followed by
     * the country calling code. like as "+1 242-365-1234" or "+12423651234"
     * @return
     */
    public boolean containsValidRegionCode(Object data) {
        String regionCode = extractRegionCode(data);
        return regionCode != null && getSupportedRegions().contains(regionCode);
    }

    /**
     * 
     * get a region code from an phone number.
     * 
     * @param phoneData a phone number String or number. the data string must be guaranteed to start with a '+' followed
     * by the country calling code. like as "+1 242-365-1234" or "+12423651234"
     * @return
     */
    public String extractRegionCode(Object phoneData) {
        PhoneNumber phoneNumber = parseToPhoneNumber(phoneData, null);
        if (phoneNumber != null) {
            return phoneUtil.getRegionCodeForNumber(phoneNumber);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 
     * get a country code from an phone number.
     * 
     * @param phoneData a phone number String or number. the data string must be guaranteed to start with a '+' followed
     * by the country calling code. like as "+1 242-365-1234" or "+12423651234"
     * @return
     */
    public int extractCountrycode(Object phoneData) {
        PhoneNumber phoneNumber = parseToPhoneNumber(phoneData, null);
        if (phoneNumber != null) {
            return phoneNumber.getCountryCode();
        }
        return 0;
    }

    /**
     * 
     * Returns a text description for the given phone number, in the language provided. The description might consist of
     * the name of the country where the phone number is from, or the name of the geographical area the phone number is
     * from if more detailed information is available.
     * 
     * @param data
     * @param regionCode the regionCode that we are expecting the number to be dialed from
     * @param languageCode the language code for which the description should be written.the 'Locale.ENGLISH' is the
     * most commonly used
     * @return
     */
    public String getGeocoderDescriptionForNumber(Object data, String regionCode, Locale languageCode) {
        PhoneNumber number = parseToPhoneNumber(data, regionCode);
        if (number != null) {
            return PhoneNumberOfflineGeocoder.getInstance().getDescriptionForNumber(number, languageCode);
        }

        return StringUtils.EMPTY;
    }

    /**
     * 
     * Gets the name of the carrier for the given phone number, in the language provided.The carrier name is the one the
     * number was originally allocated to, however if the country supports mobile number portability the number might
     * not belong to the returned carrier anymore. If no mapping is found an empty string is returned.
     * 
     * @param data the phone number for which we want to get a carrier name
     * @param regionCode the regionCode that we are expecting the number to be dialed from
     * @param languageCode the language code for which the description should be written.the 'Locale.ENGLISH' is the
     * most commonly used
     * @return
     */
    public String getCarrierNameForNumber(Object data, String regionCode, Locale languageCode) {
        PhoneNumber number = parseToPhoneNumber(data, regionCode);
        if (number == null) {
            return StringUtils.EMPTY;
        }
        return PhoneNumberToCarrierMapper.getInstance().getNameForNumber(number, languageCode);

    }

    /**
     * 
     * Returns a list of time zones to which a phone number belongs. when the PhoneNumber is invalid ,return UNKONW TIME
     * ZONE;when the PhoneNumberType is Not FIXED_LINE,MOBILE,FIXED_LINE_OR_MOBILE,return the list of time zones
     * corresponding to the country calling code; or else,return the list of corresponding time zones
     * 
     * @param data the phone number for which we want to get a list of Time zones
     * @param regionCode
     * @return
     */
    public List<String> getTimeZonesForNumber(Object data, String regionCode) {
        PhoneNumber number = parseToPhoneNumber(data, regionCode);
        if (number == null) {
            List<String> UNKNOWN_TIME_ZONE_LIST = new ArrayList<String>(1);
            UNKNOWN_TIME_ZONE_LIST.add(PhoneNumberToTimeZonesMapper.getUnknownTimeZone());
            return UNKNOWN_TIME_ZONE_LIST;
        }
        return PhoneNumberToTimeZonesMapper.getInstance().getTimeZonesForNumber(number);
    }

}
