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
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * @author jteuladedenantes
 * 
 * This class allows to generate unique random pattern from a list of fields. Each field define a set of possible
 * values.
 */
public class GenerateUniqueRandomPatterns implements Serializable {

    private static final long serialVersionUID = -5509905086789639724L;

    private static final Logger LOGGER = Logger.getLogger(GenerateUniqueRandomPatterns.class);

    /**
     * The random key to make impossible the decoding
     */
    private Integer key;

    /**
     * The list of all possible values for each field
     */
    private List<AbstractField> fields;

    /**
     * The product of width fields, i.e. the combination of all possibles values
     */
    private long longestWidth;

    /**
     * BasedWidthsList is used to go from a base to an other
     */
    private List<Long> basedWidthsList;

    public GenerateUniqueRandomPatterns(List<AbstractField> fields) {
        super();
        this.fields = fields;

        // longestWidth init
        this.longestWidth = 1L;
        for (int i = 0; i < this.getFieldsNumber(); i++) {
            long width = this.fields.get(i).getWidth();
            this.longestWidth *= width;
        }
        LOGGER.debug("longestWidth = " + longestWidth);

        // basedWidthsList init
        this.basedWidthsList = new ArrayList<Long>();
        this.basedWidthsList.add(1L);
        for (int i = this.getFieldsNumber() - 2; i >= 0; i--)
            this.basedWidthsList.add(0, this.fields.get(i + 1).getWidth() * this.basedWidthsList.get(0));
        LOGGER.debug("basedWidthsList = " + this.basedWidthsList);
    }

    public List<AbstractField> getFields() {
        return fields;
    }

    public void setFields(List<AbstractField> fields) {
        this.fields = fields;
    }

    public int getFieldsNumber() {
        return fields.size();
    }

    public void setKey(int key) {
        this.key = key;
    }

    /**
     * @param strs, the string input to encode
     * @return the new string encoding
     */
    public StringBuilder generateUniqueString(List<String> strs) {
        // check inputs
        if (strs.size() != this.getFieldsNumber())
            return null;

        // encode the fields
        List<Long> listToMask = new ArrayList<Long>();
        long encodeNumber;
        for (int i = 0; i < this.getFieldsNumber(); i++) {
            encodeNumber = this.fields.get(i).encode(strs.get(i));
            if (encodeNumber == -1) {
                return null;
            }
            listToMask.add(encodeNumber);
        }

        // generate the unique random number from the old one
        List<Long> uniqueMaskedNumberList = this.getUniqueRandomNumber(listToMask);

        // decode the fields
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < this.getFieldsNumber(); i++) {
            result.append(this.fields.get(i).decode(uniqueMaskedNumberList.get(i)));
        }
        return result;
    }

    /**
     * @param listToMask, the numbers list for each field
     * @return uniqueMaskedNumberList, the masked list
     */
    private List<Long> getUniqueRandomNumber(List<Long> listToMask) {

        // numberToMask is the number to masked created from listToMask
        long numberToMask = 0L;
        for (int i = 0; i < this.getFieldsNumber(); i++)
            numberToMask += listToMask.get(i) * this.basedWidthsList.get(i);
        LOGGER.debug("numberToMask = " + numberToMask);

        if (this.key == null)
            this.setKey((new Random()).nextInt() % 10000 + 1000);
        long coprimeNumber = findLargestCoprime(Math.abs(this.key));
        // uniqueMaskedNumber is the number we masked
        long uniqueMaskedNumber = (numberToMask * coprimeNumber) % this.longestWidth;
        LOGGER.debug("uniqueMaskedNumber = " + uniqueMaskedNumber);

        // uniqueMaskedNumberList is the unique list created from uniqueMaskedNumber
        List<Long> uniqueMaskedNumberList = new ArrayList<Long>();
        for (int i = 0; i < this.getFieldsNumber(); i++) {
            // baseRandomNumber is the quotient of the Euclidean division between uniqueMaskedNumber and
            // basedWidthsList.get(i)
            long baseRandomNumber = uniqueMaskedNumber / this.basedWidthsList.get(i);
            uniqueMaskedNumberList.add(baseRandomNumber);
            // we reiterate with the remainder of the Euclidean division
            uniqueMaskedNumber %= this.basedWidthsList.get(i);
        }

        return uniqueMaskedNumberList;
    }

    /**
     * 
     * @param the key from we want to find a coprime number with longestWidth
     * @return the largest coprime number with longestWidth less than key
     */
    private long findLargestCoprime(long key) {
        if (pgcdModulo(key, this.longestWidth) == 1) {
            return key;
        } else {
            return findLargestCoprime(key - 1);
        }
    }

    private long pgcdModulo(long a, long b) {
        if (b == 0)
            return a;
        return pgcdModulo(b, a % b);
    }

    /**
     * @return the sum of fields length (i.e. the number of characters in a field)
     */
    public int getFieldsCharsLength() {
        int length = 0;
        for (AbstractField field : fields) {
            length += field.getLength();
        }
        return length;
    }

}
