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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.talend.dataquality.datamasking.Function;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * DOC qzhao class global comment. Detailled comment<br>
 * 
 * This MaskEmailDomain class extends {@link Function} class. It offers the methods to verify the validation of a given
 * email address and other auxiliary methods.<br>
 * 
 */
public abstract class MaskEmailDomain extends GenerateFromFile<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");

    protected List<String> replacements = new ArrayList<String>();

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

    private void addReplacement(String[] para) {
        if (para.length > 0) {
            try {
                replacements = KeysLoader.loadKeys(para[0]);
            } catch (IOException | NullPointerException e) {
                for (String element : para) {
                    replacements.add(element.trim());
                }
            } finally {
                if (replacements.size() != 1) {
                    replacements.remove("");
                    replacements.remove(null);
                }
            }
        }
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues, RandomWrapper rand) {
        super.parse(extraParameter, keepNullValues, rand);
        addReplacement(parameters);
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
        while (true) {
            count = address.indexOf('.', count);
            if (count > 0) {
                list.add(count++);
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
        int splitDomain = address.lastIndexOf('.', splitAddress + 1);
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
    protected String maskTopLevelDomainByX(String address, int splitAddress, int splitDomain) {
        StringBuilder sb = new StringBuilder(address);

        ArrayList<Integer> indexes = getPointPostions(address, splitAddress);

        for (Integer index : indexes) {
            for (int i = splitAddress + 1; i < index; i++)
                sb.setCharAt(i, 'X');
            splitAddress = index;
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

        for (Integer position : pointsPosition) {
            for (int i = count + 1; i < position; i++) {
                sb.setCharAt(i, maskingCrct);
            }
            count = position;
        }
        return sb.toString();
    }

    private Character getMaskingCharacter() {
        String replacement = (replacements.size() == 1) ? replacements.get(0) : null;
        Character maskingCrct = (replacement != null && replacement.length() == 1 && Character.isLetter(replacement.charAt(0)))
                ? replacement.charAt(0) : 'X';
        return maskingCrct;
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

}
