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
package org.talend.dataquality.email.api;

/**
 * any kind of check interface.
 *
 */
public interface IEmailChecker {

    /**
     * <p>
     * Checks if a field has a valid e-mail address.
     * </p>
     *
     * @param email The value validation is being performed on. A <code>null</code> value is considered invalid.
     * @return true if the email address is valid.
     */
    public boolean check(String email);

    /**
     * 
     * <p>
     * Checks if a field has a valid e-mail address.
     * </p>
     * 
     * @param email The value validation is being performed on. A <code>null</code> value is considered invalid.
     * @param strings Some parameter when check the email(e.g. LocalPartColumnContentCheckerImpl will put firstName and
     * lastName from here)
     * @return current result of check and suggested email if the result is "CORRECTED"
     */
    public String[] check(String email, String... inputParameters);

    /**
     * 
     * <p>
     * Checks if a field has a valid e-mail address.
     * </p>
     * 
     * @param email The value validation is being performed on. A <code>null</code> value is considered invalid.
     * @return current result of check 
     */
    public String checkEmail(String email);
}
