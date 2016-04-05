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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
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

    /**
     * 
     * DOC qiongli Comment method "isValidPhoneNumber".
     * 
     * @param data
     * @param regionCode
     * @return
     */
    public boolean isValidPhoneNumber(Object data, String regionCode) {
        if (data == null || StringUtils.isBlank(data.toString())) {
            return false;
        }
        PhoneNumber phonemuber = null;
        boolean isValid = false;
        try {
            phonemuber = phoneUtil.parse(data.toString(), regionCode);
            isValid = phoneUtil.isValidNumberForRegion(phonemuber, regionCode);
        } catch (Exception e) {
            log.error(e);
            return false;
        }
        return isValid;
    }

    /**
     * 
     * DOC qiongli Comment method "isValidPhoneNumber".
     * 
     * @param phonemuber
     * @return
     */
    public boolean isValidPhoneNumber(PhoneNumber phonemuber) {
        if (phonemuber == null) {
            return false;
        }
        return phoneUtil.isValidNumber(phonemuber);
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
     * DOC qiongli Comment method "isPossiblePhoneNumber".
     * 
     * @param phonemuber
     * @return
     */
    public boolean isPossiblePhoneNumber(PhoneNumber phonemuber) {
        if (phonemuber == null) {
            return false;
        }
        return phoneUtil.isPossibleNumber(phonemuber);
    }

    /**
     * 
     * DOC qiongli Comment method "format".
     * 
     * @param data
     * @param regionCode
     * @param numberFormat
     * @return
     */
    public String format(Object data, String regionCode, PhoneNumberFormat numberFormat) {
        if (data == null || StringUtils.isBlank(data.toString())) {
            return "";
        }
        PhoneNumber phonemuber = null;
        String formated = null;
        try {
            phonemuber = phoneUtil.parse(data.toString(), regionCode);
            formated = format(phonemuber, numberFormat);
        } catch (Exception e) {
            log.error(e);
            return data.toString();
        }
        return formated;
    }

    /**
     * 
     * DOC qiongli Comment method "format".
     * 
     * @param number
     * @param numberFormat
     * @return
     */
    public String format(PhoneNumber number, PhoneNumberFormat numberFormat) {
        if (numberFormat == null) {
            return phoneUtil.format(number, PhoneNumberFormat.E164);
        }
        return phoneUtil.format(number, numberFormat);
    }
}
