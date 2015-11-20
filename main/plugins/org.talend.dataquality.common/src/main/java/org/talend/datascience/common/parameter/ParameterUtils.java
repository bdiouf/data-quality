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
package org.talend.datascience.common.parameter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @since 1.3.3
 * @author zhao
 *
 */
public final class ParameterUtils {

    /**
     * Get customized pattern given parameter map.
     * 
     * @since 1.3.3
     * @param parameters
     * @return date pattern string or null.
     */
    public static String getCustomizedPattern(Map<String, String> parameters) {
        String pattern = parameters.get(Parameters.DateParam.DATE_PATTERN.name());
        if (StringUtils.isNotEmpty(pattern)) {
            return pattern;
        }
        return null;
    }


    /**
     * Get locale given parameter set in map.
     * 
     * @since 1.3.3
     * @param parameters
     * @return Locale instance. if the parameter has not set locale, then return null.
     */
    public static Locale getLocale(Map<String, String> parameters) {
        String newLocale = parameters.get(Parameters.DateParam.LOCALE.name());
        if (newLocale == null) {
            return null;
        }
        Locale lc = LOCALE_CACHE.get(newLocale);
        if (lc != null) {
            return lc;
        }
        String[] localeLangAndCountry = newLocale.split("_"); // locale string like "zh_CN","jo_JP"
        if (localeLangAndCountry.length == 1) {
            lc = new Locale(localeLangAndCountry[0]);
            LOCALE_CACHE.put(localeLangAndCountry[0], lc);
        } else {
            lc = new Locale(localeLangAndCountry[0], localeLangAndCountry[1]);
            LOCALE_CACHE.put(localeLangAndCountry[0] + "_" + localeLangAndCountry[1], lc);
        }
        return lc;
    }

    private static Map<String, Locale> LOCALE_CACHE = new HashMap<String, Locale>();
}
