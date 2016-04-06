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

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class PhoneNumberHandler {

    private static PhoneNumberHandler phoneStandardizeUtil = null;

    private static PhoneNumberUtil phoneUtil = null;

    private static final Logger log = Logger.getLogger(PhoneNumberHandler.class);

    public static PhoneNumberHandler getInstance() {
        if (phoneStandardizeUtil == null) {
            phoneStandardizeUtil = new PhoneNumberHandler();
            phoneUtil = PhoneNumberUtil.getInstance();
        }
        return phoneStandardizeUtil;
    }

    private PhoneNumberHandler() {

    }

    public PhoneNumber parseToPhoneNumber(Object data, String regionCode) {
        if (data == null || StringUtils.isBlank(data.toString())) {
            return null;
        }
        PhoneNumber phonenumber = null;
        try {
            phonenumber = phoneUtil.parse(data.toString(), regionCode);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return phonenumber;
    }

    /**
     * 
     * DOC qiongli Comment method "isValidPhoneNumber".
     * 
     * @param data
     * @param regionCode
     * @return
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
     * DOC qiongli Comment method "isPossiblePhoneNumber".
     * 
     * @param data
     * @param regionCode
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
     * DOC qiongli Comment method "formatE164".
     * 
     * @param data
     * @param regionCode
     * @return
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
     * DOC qiongli Comment method "formatInternational".
     * 
     * @param data
     * @param regionCode
     * @return
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
     * DOC talend Comment method "formatNational".
     * 
     * @param data
     * @param regionCode
     * @return
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
     * DOC talend Comment method "formatRFC396".
     * 
     * @param data
     * @param regionCode
     * @return
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
     * DOC qiongli Comment method "getSupportedRegions".
     * 
     * @return
     */
    public Set<String> getSupportedRegions() {
        return phoneUtil.getSupportedRegions();
    }

    /**
     * 
     * DOC qiongli Comment method "getCountryCodeForRegion".
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
     * DOC qiongli Comment method "isValidRegionByPhoneNumber".
     * 
     * @param data
     * @return
     */
    public boolean isValidRegionByPhoneNumber(Object data) {
        String regionCode = getRegionCodeByPhoneNumber(data);
        return regionCode != null && getSupportedRegions().contains(regionCode);
    }

    /**
     * 
     * DOC qiongli Comment method "getRegionCodeByPhoneNumber".
     * 
     * @param data
     * @return
     */
    public String getRegionCodeByPhoneNumber(Object data) {
        PhoneNumber phoneNumber = parseToPhoneNumber(data, null);
        if (phoneNumber != null) {
            return phoneUtil.getRegionCodeForNumber(phoneNumber);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 
     * DOC qiongli Comment method "getCountrycodeByPhoneNumber".
     * 
     * @param data
     * @return
     */
    public int getCountrycodeByPhoneNumber(Object data) {
        PhoneNumber phoneNumber = parseToPhoneNumber(data, null);
        if (phoneNumber != null) {
            return phoneNumber.getCountryCode();
        }
        return 0;
    }

}
