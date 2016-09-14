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
package org.talend.dataquality.email.checkerImpl;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.email.api.EmailVerifyResult;
import org.talend.dataquality.email.api.IEmailChecker;
import org.talend.dataquality.email.exception.TalendSMTPRuntimeException;

/**
 * created by yyin on 2015年3月26日 Detailled comment
 *
 */
public abstract class AbstractEmailChecker implements IEmailChecker {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#check(java.lang.String)
     */
    @Override
    abstract public boolean check(String email);

    @Override
    public String checkEmail(String email) throws TalendSMTPRuntimeException {
        return check(email) ? EmailVerifyResult.VALID.getResultValue() : EmailVerifyResult.INVALID.getResultValue();
    }

    /*
     * For other checkers who do not use this check method, will return: verify or invalid
     * 
     * @see org.talend.dataquality.email.api.IEmailChecker#check(java.lang.String, java.lang.String[])
     */
    @Override
    public String[] check(String email, String... strings) {
        String[] results = new String[2];
        if (check(email)) {
            results[0] = EmailVerifyResult.VALID.getResultValue();
        } else {
            results[0] = EmailVerifyResult.INVALID.getResultValue();
        }
        results[1] = StringUtils.EMPTY;
        return results;
    }

}
