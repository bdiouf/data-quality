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
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * DOC qzhao class global comment. Detailled comment<br>
 * 
 * This MaskEmailDomain class extends {@link Function} class. It offers the methods to verify the validation of a given
 * email address and other auxiliary methods.<br>
 * 
 */
public abstract class MaskEmailDomain extends Function<String> {

    private static final long serialVersionUID = 3837984827035744721L;

    protected static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w-]+\\.)+[\\w-]+[\\w-]$");

    protected final transient List<String> replacements = new ArrayList<String>();

    protected boolean maskByX = false;

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

    /**
     * 
     * DOC qzhao Comment method "maskTopLevelDomainByX".<br>
     * 
     * Masks the top-level domain name by X
     * 
     * @param address
     * @return masked address
     */
    protected String maskTopLevelDomainByX(String address) {
        StringBuilder sb = new StringBuilder(address);
        int splitAddress = address.indexOf('@');
        ArrayList<Integer> indexes = getPointPostions(address, splitAddress);

        Character maskingCrct = getMaskingCharacter();

        for (Integer index : indexes) {
            for (int i = splitAddress + 1; i < index; i++)
                sb.setCharAt(i, maskingCrct);
            splitAddress = index;
        }
        return sb.toString();
    }

    /**
     * 
     * DOC qzhao Comment method "maskTopLevelDomainByX".<br>
     * 
     * Masks the top-level domain name by X
     * 
     * @param address
     * @param splitAddress
     * @param splitDomain
     * @return masked address
     */
    protected String maskTopLevelDomainByX(String address, int splitAddress) {
        StringBuilder sb = new StringBuilder(address);

        ArrayList<Integer> indexes = getPointPostions(address, splitAddress);
        int seperation = splitAddress;
        for (Integer index : indexes) {
            for (int i = seperation + 1; i < index; i++)
                sb.setCharAt(i, 'X');
            seperation = index;
        }
        return sb.toString();
    }

    protected String maskTopLevelDomainRandomly(String address, String replacement, int splitAddress, int splitDomain) {
        return address.substring(0, splitAddress + 1) + replacement + address.substring(splitDomain);
    }

    /**
     * DOC qzhao Comment method "choosePropriateDomainIndex".<br>
     * 
     * Chooses a appropriate index in the replacements where the item is different with the original input<br>
     * 
     * @param originalDomain
     * @return
     */
    protected int chooseAppropriateDomainIndex(String originalDomain) {
        int domainIndex = 0;
        do {
            domainIndex = rnd.nextInt(replacements.size());
        } while (originalDomain.equals(replacements.get(domainIndex)));
        return domainIndex;
    }

    /**
     * DOC qzhao Comment method "replaceFullDomainByX".<br>
     * 
     * Replaces all the domains by X with the original points
     * 
     * @param str
     * @param sb
     * @param count
     * @return masked full domain address
     */
    protected String maskFullDomainByX(String str, int count) {
        StringBuilder sb = new StringBuilder(str);
        ArrayList<Integer> pointsPosition = getPointPostions(str, count);
        pointsPosition.add(str.length());
        Character maskingCrct = getMaskingCharacter();
        int c = count;

        for (Integer position : pointsPosition) {
            for (int i = c + 1; i < position; i++) {
                sb.setCharAt(i, maskingCrct);
            }
            c = position;
        }
        return sb.toString();
    }

    private Character getMaskingCharacter() {
        String replacement = (replacements.size() == 1) ? replacements.get(0) : null;
        return (replacement != null && replacement.length() == 1 && Character.isLetter(replacement.charAt(0)))
                ? replacement.charAt(0) : 'X';
    }

    /**
     * 
     * DOC qzhao Comment method "maskFullDomainRandomly".<br>
     * 
     * Replace full email domain by the given replacement
     * 
     * @param address
     * @param replacement
     * @param count
     * @return
     */
    protected String maskFullDomainRandomly(String address, String replacement, int count) {
        return address.substring(0, count + 1) + replacement;
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        for (String element : parameters) {
            replacements.add(element);
        }
        if (replacements.size() != 1) {
            replacements.remove("");
            replacements.remove(null);
        }
    }

}
