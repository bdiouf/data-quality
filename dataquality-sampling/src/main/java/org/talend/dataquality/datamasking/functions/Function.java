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
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 18 juin 2015. This class is an abstract class that all other functions extends. All the
 * methods and fiels that all functions share are stored here.
 *
 */
public abstract class Function<T> implements Serializable {

    private static final long serialVersionUID = 6333987486134315822L;

    protected static final Logger LOGGER = Logger.getLogger(Function.class);

    protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$

    public static final String LOWER = "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

    protected Random rnd;

    protected String[] parameters = new String[1];

    protected boolean keepNull = false;

    protected boolean keepInvalidPattern = false;

    protected static final Pattern patternSpace = Pattern.compile("\\s+");

    /**
     * setter for random
     * 
     * @param rand The RandomWrapper.
     */
    public void setRandomWrapper(Random rand) {
        if (rand == null) {
            rnd = new RandomWrapper();
        } else {
            rnd = rand;
        }
    }

    /**
     * getter for random
     * 
     * @return the random object
     */
    public Random getRandom() {
        return rnd;
    }

    /**
     * DOC jgonzalez Comment method "setKeepNull". This function sets a boolean used to keep null values.
     * 
     * @param keep The value of the boolean.
     */
    public void setKeepNull(boolean keep) {
        this.keepNull = keep;
    }

    public void setKeepInvalidPattern(boolean keepInvalidPattern) {
        this.keepInvalidPattern = keepInvalidPattern;
    }

    /**
     * DOC jgonzalez Comment method "parse". This function is called at the beginning of the job and parses the
     * parameter. Moreover, it will call methods setKeepNull and setRandomWrapper
     * 
     * @param extraParameter The parameter we try to parse.
     * @param keepNullValues The parameter used for setKeepNull.
     * @param rand The parameter used for setRandomMWrapper.
     */
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        if (extraParameter != null) {
            parameters = extraParameter.split(","); //$NON-NLS-1$
            if (parameters.length == 1) { // check if it's a path to a readable file
                try {
                    List<String> aux = KeysLoader.loadKeys(parameters[0].trim());
                    parameters = new String[aux.size()];
                    int i = 0;
                    for (String str : aux)
                        parameters[i++] = str.trim();
                } catch (IOException | NullPointerException e2) { // otherwise, we just get the parameter
                    LOGGER.debug("The parameter is not a path to a file.");
                    parameters[0] = parameters[0].trim();
                }
            } else {
                for (int i = 0; i < parameters.length; i++)
                    parameters[i] = parameters[i].trim();
            }
        }
        setKeepNull(keepNullValues);
        setRandomWrapper(rand);
    }

    public T generateMaskedRow(T t) {
        if (t == null && keepNull) {
            return null;
        }
        return doGenerateMaskedField(t);
    }

    /**
     * @param strWithSpaces, resWithoutSpaces
     * @return the res with spaces
     */
    protected String insertSpacesInString(String strWithSpaces, String resWithoutSpaces) {
        if (strWithSpaces == null || resWithoutSpaces == null)
            return resWithoutSpaces;
        StringBuilder res = new StringBuilder();
        int j = 0;
        for (int i = 0; i < strWithSpaces.length(); i++)
            if (strWithSpaces.charAt(i) == ' ')
                res.append(' ');
            else
                res.append(resWithoutSpaces.charAt(j++));
        return res.toString();
    }

    /**
     * Replaces all the spaces in the input string
     * 
     * @param input
     * @return
     */
    protected String replaceSpacesInString(String input) {
        if (input == null) {
            return null;
        }
        return patternSpace.matcher(input).replaceAll("");
    }

    /**
     * DOC jgonzalez Comment method "generateMaskedRow". This method applies a function on a field and returns the its
     * new value.
     * 
     * @param t The input value.
     * @return A new value after applying the function.
     */
    protected abstract T doGenerateMaskedField(T t);
}
