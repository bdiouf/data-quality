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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.email.checkerImpl.CallbackMailServerCheckerImpl;
import org.talend.dataquality.email.checkerImpl.ListDomainsCheckerImpl;
import org.talend.dataquality.email.checkerImpl.LocalPartColumnContentCheckerImpl;
import org.talend.dataquality.email.checkerImpl.LocalPartRegexCheckerImpl;
import org.talend.dataquality.email.checkerImpl.RegularRegexCheckerImpl;
import org.talend.dataquality.email.checkerImpl.TLDsCheckerImpl;
import org.talend.dataquality.email.exception.TalendSMTPRuntimeException;

/**
 * created by talend on 2014.12.26 Detailled comment
 *
 */
public class EmailVerify {

    private static Logger log = Logger.getLogger(EmailVerify.class);

    private IEmailChecker[] checkers;

    public EmailVerify() {
        checkers = new IEmailChecker[6];
    }

    public EmailVerify addRegularRegexChecker(boolean isUseRegularRegex, String userDefinedRegex) {
        if (isUseRegularRegex) {
            checkers[0] = new RegularRegexCheckerImpl(userDefinedRegex);
        }
        return this;
    }

    public EmailVerify addLocalPartRegexChecker(String localPartRegexExpress, boolean isLocalPartCaseSensitive,
            boolean isLocalPartShort) {
        if (!StringUtils.isBlank(localPartRegexExpress)) {
            checkers[1] = new LocalPartRegexCheckerImpl(localPartRegexExpress, isLocalPartShort, isLocalPartCaseSensitive);
        }
        return this;
    }

    public EmailVerify addListDomainsChecker(boolean isBlackListDomains, List<String> listDomains) {
        // if white, and the list is empty, consider: INVALID
        if ((listDomains == null || listDomains.isEmpty()) && !isBlackListDomains) {
            log.warn(
                    " The selected white list is empty. All emails will be invalid. Either select the black list option or add some domains in the white list."); //$NON-NLS-1$
        }

        checkers[2] = new ListDomainsCheckerImpl(isBlackListDomains, listDomains);
        return this;
    }

    public EmailVerify addTLDsChecker(boolean isValidTLDs, List<String> tLDs, boolean isBlackListDomains) {
        if (isValidTLDs) {
            checkers[3] = new TLDsCheckerImpl(tLDs, isBlackListDomains);
        }
        return this;
    }

    /**
     * 
     * Call back mail server checker should be last one.
     * 
     * @param isCallbackMailServer whether should be added for current call back checker
     * @return current EmailVerify instance
     */
    public EmailVerify addCallbackMailServerChecker(boolean isCallbackMailServer) {
        if (isCallbackMailServer) {
            checkers[5] = new CallbackMailServerCheckerImpl();
        }
        return this;
    }

    public EmailVerify addLocalPartColumnContentChecker(boolean isColumnContent, boolean isCaseSensitive,
            String usedCaseToGenerate, String nFOfFirst, String nLOfFirst, String nFOfLast, String nLOfLast, String separator) {
        if (isColumnContent) {
            checkers[4] = new LocalPartColumnContentCheckerImpl(nFOfFirst, nLOfFirst, nFOfLast, nLOfLast, separator,
                    isCaseSensitive, usedCaseToGenerate);
        }
        return this;
    }

    /**
     * get the verify email boolean value.
     * 
     * @param email
     * @return
     */
    public String verify(String email) {
        if (email == null) {
            return EmailVerifyResult.INVALID.getResultValue();
        }

        for (IEmailChecker checker : checkers) {
            try {
                if (checker != null) {
                    String checkResult = checker.checkEmail(email);
                    if (shouldStopCheck(checkResult)) {
                        return checkResult;
                    }
                }
            } catch (TalendSMTPRuntimeException e) {
                continue;
            } catch (Exception e) {
                return EmailVerifyResult.INVALID.getResultValue();
            }
        }
        return EmailVerifyResult.VALID.getResultValue();
    }

    /**
     * The state of the checker is invalid
     * 
     * @param email
     * @param checker
     * @return true if result is invalid or rejected
     */
    private boolean shouldStopCheck(String checkEmailResult) {
        return EmailVerifyResult.INVALID.getResultValue().equalsIgnoreCase(checkEmailResult)
                || EmailVerifyResult.REJECTED.getResultValue().equalsIgnoreCase(checkEmailResult)
                || EmailVerifyResult.VERIFIED.getResultValue().equalsIgnoreCase(checkEmailResult);
    }

    /**
     * get the verify email string value.
     * 
     * @param email
     * @return if it is ok, then return "verfied" else "invalid".
     */
    public String getVerifyResult(String email) {
        return verify(email);
    }

    /**
     * 
     * get the verify email string value.
     * 
     * @param email
     * @param strings firstName and lastName
     * @return
     */
    public String[] getVerifyResult(String email, String... strings) {
        String[] checkResult = { EmailVerifyResult.INVALID.getResultValue(), StringUtils.EMPTY };
        for (IEmailChecker checker : checkers) {
            try {
                if (checker != null) {
                    checkResult = checker.check(email, strings);
                    // only when one result is invalid, return, do NOT contine for other checkers.
                    if (StringUtils.equals(EmailVerifyResult.INVALID.getResultValue(), checkResult[0])) {
                        return checkResult;
                    }
                }
            } catch (TalendSMTPRuntimeException e) {
                continue;
            } catch (Exception e) {
                return checkResult;
            }
        }
        return checkResult;
    }

}
