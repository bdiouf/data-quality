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

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class PhoneNumberOfflineGeocoderHanlder {

    private static PhoneNumberOfflineGeocoderHanlder PhoneNumberOfflineGeocoderHanlder = null;

    public PhoneNumberOfflineGeocoderHanlder getInstance() {
        if (PhoneNumberOfflineGeocoderHanlder == null) {
            PhoneNumberOfflineGeocoderHanlder = new PhoneNumberOfflineGeocoderHanlder();
        }
        return PhoneNumberOfflineGeocoderHanlder;
    }

    private PhoneNumberOfflineGeocoderHanlder() {

    }

    /**
     * 
     * DOC qiongli Comment method "getDescriptionForNumber".
     * 
     * @param data
     * @param regionCode
     * @param languageCode
     * @return
     */
    public String getDescriptionForNumber(Object data, String regionCode, Locale languageCode) {
        PhoneNumber number = parseToPhoneNumber(data, regionCode);
        if (number != null) {
            PhoneNumberOfflineGeocoder.getInstance().getDescriptionForNumber(number, languageCode);
        }

        return StringUtils.EMPTY;
    }

    private PhoneNumber parseToPhoneNumber(Object data, String regionCode) {
        if (data == null || StringUtils.isBlank(data.toString())) {
            return null;
        }
        PhoneNumber phonenumber = null;
        try {
            phonenumber = PhoneNumberUtil.getInstance().parse(data.toString(), regionCode);
        } catch (Exception e) {
            return null;
        }
        return phonenumber;
    }

}
