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

import org.talend.dataquality.semantic.validator.AbstractRegexSemanticValidator;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class UserDefinedRegexValidator extends AbstractRegexSemanticValidator {

    private String patternString;

    private Boolean caseInsensitive = true;

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

}
