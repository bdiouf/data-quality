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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * DOC jteuladedenantes class global comment. Detailled comment
 */
public class GenerateUniqueRandomNumbers implements Serializable {

    private static final long serialVersionUID = -5509905086789639724L;

    private static final Logger LOGGER = Logger.getLogger(GenerateUniqueRandomNumbers.class);

    /**
     * 
     * DOC jteuladedenantes Comment method "generateUniqueString".
     * 
     * @param strs, the string input to encode
     * @param fields, the possible values for strs
     * @param key, the masked list
     * @return
     */
    public static StringBuilder generateUniqueString(List<String> strs, List<Field> fields, int key) {
        // encode the fields
        List<Long> widthsList = new ArrayList<Long>();
        List<Long> ssnListToMask = new ArrayList<Long>();
        long encodeNumber;
        for (int i = 0; i < fields.size(); i++) {
            widthsList.add(fields.get(i).getWidth());
            encodeNumber = fields.get(i).encode(strs.get(i));
            // unvalid field
            if (encodeNumber == -1) {
                return null;
            }
            ssnListToMask.add(encodeNumber);
        }

        // generate the unique random number from the old one
        List<Long> uniqueMaskedNumberList = GenerateUniqueRandomNumbers.getUniqueRandomNumber(ssnListToMask, widthsList, key);

        // decode the fields
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < fields.size(); i++) {
            result.append(fields.get(i).decode(uniqueMaskedNumberList.get(i)));
        }
        return result;
    }

    /**
     * 
     * DOC jteuladedenantes Comment method "getUniqueRandomNumbersList".
     * 
     * @param listToMask, the numbers list for each field
     * @param widthsList, the width list of all possible number for each field.
     * @param key, the random key to make impossible the decoding
     * @return uniqueMaskedNumberList, the masked list
     */
    public static List<Long> getUniqueRandomNumber(List<Long> listToMask, List<Long> widthsList, int key) {

        // check inputs
        if (listToMask.size() != widthsList.size())
            return null;
        for (int i = 0; i < listToMask.size(); i++)
            if (listToMask.get(i) >= widthsList.get(i))
                return null;

        // productWidthsList allows to revert the process to the original widthsList list
        List<Long> productWidthsList = new ArrayList<Long>();
        productWidthsList.add(1L);
        for (int i = widthsList.size() - 2; i >= 0; i--)
            productWidthsList.add(0, widthsList.get(i + 1) * productWidthsList.get(0));

        LOGGER.debug("productWidthsList = " + productWidthsList);

        // numberToMask is the number to masked created from listToMask
        long numberToMask = 0L;
        for (int i = 0; i < listToMask.size(); i++)
            numberToMask += listToMask.get(i) * productWidthsList.get(i);

        LOGGER.debug("numberToMask = " + numberToMask);

        // longestWidth is the product of widthsList numbers
        long longestWidth = 1L;
        for (long width : widthsList)
            longestWidth *= width;

        LOGGER.debug("longestWidth = " + longestWidth);
        // uniqueMaskedNumber is the number we masked
        long uniqueMaskedNumber = getUniqueRandomNumberBis(numberToMask, longestWidth, Math.abs(key));

        LOGGER.debug("uniqueMaskedNumber = " + uniqueMaskedNumber);
        // uniqueMaskedNumberList is the unique list created from uniqueMaskedNumber
        List<Long> uniqueMaskedNumberList = new ArrayList<Long>();
        for (int i = 0; i < productWidthsList.size(); i++) {
            // baseRandomNumber is the quotient of the Euclidean division between randomNumber and
            // productWidthsList.get(i)
            long baseRandomNumber = uniqueMaskedNumber / productWidthsList.get(i);
            uniqueMaskedNumberList.add(baseRandomNumber);
            // we reiterate with the remainder of the Euclidean division
            uniqueMaskedNumber %= productWidthsList.get(i);
        }

        return uniqueMaskedNumberList;
    }

    /**
     * 
     * DOC jteuladedenantes Comment method "getUniqueRandomNumbersListBis".
     * 
     * @param numberToMask is the number we want to mask
     * @param width, the width of all possible generated number.
     * @param key, the random key to make impossible the decoding
     * @return listSizeToGenerate different numbers betWeen 0 and the biggest prime number less than numberWidth
     */
    private static long getUniqueRandomNumberBis(long numberToMask, long width, int key) {
        // we find the biggest coprime number with numberWidth less than key
        long coprimeNumber = findLargestCoprime(key, width);
        LOGGER.debug("coprimeNumber = " + coprimeNumber);
        return (numberToMask * coprimeNumber) % width;

    }

    private static long findLargestCoprime(int key, long num) {
        if (pgcdModulo(key, num) == 1) {
            return key;
        } else {
            return findLargestCoprime(key - 1, num);
        }
    }

    /**
     * Return the pgcd of two long
     * 
     * @param a, a long
     * @param b, the other long
     */
    private static long pgcdModulo(long a, long b) {
        if (b == 0)
            return a;
        return pgcdModulo(b, a % b);
    }

}
