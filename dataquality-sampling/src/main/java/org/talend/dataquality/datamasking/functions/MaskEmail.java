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
package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * DOC qzhao class global comment. Detailled comment<br>
 * 
 * This MaskEmailDomain class extends {@link Function} class. It offers the methods to verify the validation of a given
 * email address and other auxiliary methods.<br>
 * 
 */
public abstract class MaskEmail extends Function<String> {

    private static final long serialVersionUID = 3837984827035744721L;

    protected static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w-]+\\.)+[\\w-]+[\\w-]$");

    /**
     * DOC qzhao Comment method "isValidEmailAddress".<br>
     * Verifies whether it is a valid email address
     * 
     * @param email email address
     * @return true when the input is valid
     */
    protected boolean isValidEmailAddress(String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }

    /**
     * DOC qzhao Comment method "getPointPostions".<br>
     * Gets the points' postions in the email domain
     * 
     * @param address the original email address
     * @param count @'s position
     * @return a list of integer
     */
    protected ArrayList<Integer> getPointPostions(String address, int count) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int c = count;
        while (true) {
            c = address.indexOf('.', c);
            if (c > 0) {
                list.add(c++);
            } else {
                break;
            }
        }
        return list;
    }

    // @Override
    // public void parse(String extraParameter, boolean keepNullValues, Random rand) {
    // super.parse(extraParameter, keepNullValues, rand);
    // for (String element : parameters) {
    // replacements.add(element);
    // }
    // if (replacements.size() != 1) {
    // replacements.remove("");
    // replacements.remove(null);
    // }
    // }

}
