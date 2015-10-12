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
package org.talend.dataquality.semantic.classifier.custom;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.talend.dataquality.semantic.validator.AbstractRegexSemanticValidator;
import org.talend.dataquality.semantic.validator.ISemanticValidator;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class UserDefinedRegexValidator extends AbstractRegexSemanticValidator {

    private static Logger log = Logger.getLogger(UserDefinedRegexValidator.class);

    private String patternString;

    private Boolean caseInsensitive = true;

    /**
     * an optional secondary validator.
     */
    private String subValidatorClassName = "";

    private ISemanticValidator subValidator = createSubValidator(subValidatorClassName);

    /**
     * Getter for subValidatorClassName.
     * 
     * @return the subValidatorClassName
     */
    public String getSubValidatorClassName() {
        return this.subValidatorClassName;
    }

    /**
     * Sets the subValidatorClassName.
     * 
     * @param subValidatorClassName the subValidatorClassName to set
     */
    public void setSubValidatorClassName(String subValidatorClassName) {
        this.subValidatorClassName = subValidatorClassName;
    }

    /**
     * Getter for caseInsensitive.
     * 
     * @return the caseInsensitive
     */
    public Boolean getCaseInsensitive() {
        return this.caseInsensitive;
    }

    /**
     * Sets the caseInsensitive.
     * 
     * @param caseInsensitive the caseInsensitive to set
     */
    public void setCaseInsensitive(Boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public String getPatternString() {
        return patternString;
    }

    public void setPatternString(String patternString) {
        this.patternString = patternString;
        pattern = caseInsensitive ? Pattern.compile(patternString, Pattern.CASE_INSENSITIVE) : Pattern.compile(patternString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.semantic.validator.AbstractRegexSemanticValidator#isValid(java.lang.String)
     */
    @Override
    public boolean isValid(String str) {
        return super.isValid(str) && this.validateWithSubValidator(str);
    }

    private static ISemanticValidator createSubValidator(String validatorName) {
        if (validatorName != null && !validatorName.isEmpty()) {
            try {
                Class<?> subValidator = Class.forName(validatorName);
                return (ISemanticValidator) subValidator.newInstance();
            } catch (ClassNotFoundException e) {
                log.error(e, e);
            } catch (InstantiationException e) {
                log.error(e, e);
            } catch (IllegalAccessException e) {
                log.error(e, e);
            }
            // exception caught => default subValidator
        }
        // else return a default subvalidator
        return new ISemanticValidator() {

            @Override
            public boolean isValid(String str) {
                return true;
            }
        };
    }

    /**
     * Method "validateWithSubValidator".
     * 
     * @param str the string to check
     * @return true when the subValidator class validates the given string, false otherwise.
     */
    private boolean validateWithSubValidator(String str) {
        return this.subValidator.isValid(str);
    }
}
