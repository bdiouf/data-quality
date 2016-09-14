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
package org.talend.dataquality.email;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.dataquality.email.api.EmailVerify;
import org.talend.dataquality.email.api.EmailVerifyResult;
import org.talend.dataquality.email.exception.TalendSMTPRuntimeException;

/**
 * created by qiongli on 2014年12月26日 Detailled comment
 *
 */
public class EmailVerifyTest {

    String email = null;

    String regularPattern = "aw"; //$NON-NLS-1$

    EmailVerify emailVerify;

    @Before
    public void setUp() throws Exception {
        emailVerify = new EmailVerify();
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_regular() {
        email = "ab_2c@sina.com"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, "");
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify(email));
    }

    @Test
    public void testVerify_regular_invalid() {
        email = "ab_2c@sina.com."; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, null);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
    }

    @Test
    public void testVerify_regular_invalid_2() {
        email = "ab_2c"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, "");
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_localPart() {
        email = "abc@sina.com"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, " ").addLocalPartRegexChecker(regularPattern, false, true);
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify(email));
        email = "ab2@sina.com";
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_localPart_2() {
        email = "abc-sina.com"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, null).addLocalPartRegexChecker(regularPattern, false, true);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
    }

    @Test
    public void testVerify_localPart_3() {
        emailVerify = emailVerify.addRegularRegexChecker(true, null).addLocalPartRegexChecker(regularPattern, false, true);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(null));
    }

    @Test
    public void testVerify_RegularAndlocalPart_9985() {
        email = "2@qq.com"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, "^\\w+([-+.]\\w+)@\\w([-.]\\w+)\\.\\w+([-.]\\w+)$")
                .addLocalPartRegexChecker("^\\d$", false, false);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_TLD() {
        email = "ab_2c@sina.com.xyz"; //$NON-NLS-1$
        List<String> tldLs = new ArrayList<String>();
        tldLs.add("xyz".toUpperCase()); //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(false, null).addTLDsChecker(true, tldLs, true);
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify(email));
        email = "ab_2c@sina.com.xy"; //$NON-NLS-1$
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
    }

    @Test
    public void testVerify_TLD_2() {
        email = "ab_2c-sina.com.xy"; //$NON-NLS-1$
        List<String> tldLs = new ArrayList<String>();
        tldLs.add("xyz".toUpperCase()); //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(false, "").addTLDsChecker(true, tldLs, true);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
    }

    @Test
    public void testVerify_TLD_3() {
        List<String> tldLs = new ArrayList<String>();
        tldLs.add("xyz".toUpperCase()); //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(false, "").addTLDsChecker(true, tldLs, true);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(null));
    }

    @Test
    public void testVerify_TLD_4() {
        List<String> tldLs = new ArrayList<String>();
        tldLs.add("xyz".toUpperCase()); //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(false, "").addTLDsChecker(true, tldLs, true);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify("abc"));
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_domain_white() {
        email = "ab@abx.com"; //$NON-NLS-1$
        List<String> ls = new ArrayList<String>();
        ls.add("abx.com"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(false, ls);
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify(email));
    }

    @Test
    public void testVerify_domain_white_2() {
        email = "ab-abx.com"; //$NON-NLS-1$
        List<String> ls = new ArrayList<String>();
        ls.add("abx.com"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(false, ls);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
    }

    @Test
    public void testVerify_domain_white_3() {
        List<String> ls = new ArrayList<String>();
        ls.add("abx.com"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(false, ls);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(null));
    }

    @Test
    public void testVerify_domain_white_empty() {
        email = "ab@abx.com"; //$NON-NLS-1$
        List<String> ls = new ArrayList<String>();
        emailVerify = emailVerify.addListDomainsChecker(false, ls);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_domain_white_multiEmails() {
        List<String> emailLs = new ArrayList<String>();
        emailLs.add("ab@abx.com"); //$NON-NLS-1$
        emailLs.add("ab@;cd12.com"); //$NON-NLS-1$

        List<String> whiteLs = new ArrayList<String>();
        whiteLs.add("abx.com"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(false, whiteLs);
        for (int i = 0; i < emailLs.size(); i++) {
            String em = emailLs.get(i);
            if (i == 0) {
                assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify(em));
            } else {
                assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(em));
            }
        }

    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_domain_black() {
        email = "ab@abx.com"; //$NON-NLS-1$
        List<String> ls = new ArrayList<String>();
        ls.add("abx.com"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(true, ls);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
        email = "ab@cd.com"; //$NON-NLS-1$
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify(email));
    }

    @Test
    public void testVerify_domain_black_9985() {
        String[] emails = { "luck@qq.com", "luck@aa.bbzz11", "luck@bb.zbzz12", "luck@as.cn", "luck@cc.zbzz13" }; //$NON-NLS-1$
        List<String> ls = new ArrayList<String>();
        ls.add("cc.zbzz13"); //$NON-NLS-1$
        emailVerify = emailVerify.addListDomainsChecker(true, ls);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(email));
        email = "ab@cd.com"; //$NON-NLS-1$

        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify(emails[0]));
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify(emails[1]));
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify(emails[2]));
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify(emails[3]));
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(emails[4]));
    }

    @Test
    @Ignore
    public void testCallback_valid1() {
        emailVerify = emailVerify.addRegularRegexChecker(true, "").addCallbackMailServerChecker(true);
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify("237283696@qq.com"));
    }

    @Test
    @Ignore
    public void testCallback_valid_2() {
        emailVerify = emailVerify.addCallbackMailServerChecker(true).addRegularRegexChecker(true, null);
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify("237283696@qq.com")); //$NON-NLS-1$
    }

    @Test
    @Ignore
    public void testCallback_valid2() {
        emailVerify = emailVerify.addRegularRegexChecker(true, null).addCallbackMailServerChecker(true);
        try {
            assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify("qiongli1@163.com"));
        } catch (TalendSMTPRuntimeException e) {
        }
    }

    @Test
    @Ignore
    public void testCallback_invalid_2() {
        emailVerify = emailVerify.addRegularRegexChecker(true, "").addCallbackMailServerChecker(true);
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify("qiongli-163.com"));
    }

    @Test
    @Ignore
    public void testCallback_valid_3() {
        emailVerify = emailVerify.addRegularRegexChecker(true, "").addCallbackMailServerChecker(true);
        try {
            assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.verify("237283696@qq.com"));
        } catch (TalendSMTPRuntimeException e) {
        }
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_All() {

        List<String> domainLs = new ArrayList<String>();
        domainLs.add("abx.com.cn"); //$NON-NLS-1$
        List<String> tldLs = new ArrayList<String>();
        tldLs.add("hk".toUpperCase()); //$NON-NLS-1$
        tldLs.add("cn".toUpperCase()); //$NON-NLS-1$
        email = "ab@abx.com.cn"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, "").addLocalPartRegexChecker(regularPattern, true, true)
                .addListDomainsChecker(false, domainLs).addTLDsChecker(false, tldLs, false);
        // .addCallbackMailServerChecker(true, "qiongli@talend.com");
        // EmailCheckerFactory.createEmailChecker(true, regularPattern, true, true, true, false, false, tldLs,
        // domainLs);
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.getVerifyResult(email));
    }

    @Test
    public void testVerify_All_2() {

        List<String> domainLs = new ArrayList<String>();
        domainLs.add("abx.com.cn"); //$NON-NLS-1$
        List<String> tldLs = new ArrayList<String>();
        tldLs.add("hk".toUpperCase()); //$NON-NLS-1$
        tldLs.add("cn".toUpperCase()); //$NON-NLS-1$
        email = "ab@abx.com.cn"; //$NON-NLS-1$
        emailVerify = emailVerify.addRegularRegexChecker(true, "").addLocalPartRegexChecker("^\\\\d$", true, true)
                .addListDomainsChecker(true, domainLs).addTLDsChecker(true, tldLs, true);
        // it fail to balck domain list
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.getVerifyResult(email));
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.getVerifyResult(null));
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.getVerifyResult("abc"));
    }

    /**
     * Test method for {@link org.talend.dataquality.email.api.EmailVerify#verify(java.lang.String)}.
     */
    @Test
    public void testVerify_email_null() {
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), emailVerify.verify(null));
    }

    /**
     * Test for :use column content, in local part
     */
    @Test
    public void testUseColumnContent_1() {
        // use first 2 of the firstname, use all of the last name
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, false, "L", "2", "0", "0", "10", "-");

        assertEquals(EmailVerifyResult.VALID.getResultValue(),
                emailVerify.getVerifyResult("ab-full@email.com", "abaa", "full")[0]);
        // first n<2
        String[] result = emailVerify.getVerifyResult("a-full@email.com", "abaa", "full"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("ab-full@email.com", result[1]);

        // first n>2
        result = emailVerify.getVerifyResult("aba-full@email.com", "abaa", "full"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("ab-full@email.com", result[1]);

        // last n<all
        result = emailVerify.getVerifyResult("ab-ful@email.com", "abaa", "full");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("ab-full@email.com", result[1]);

        // last n>all
        result = emailVerify.getVerifyResult("ab-fullyy@email.com", "abaa", "full");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("ab-full@email.com", result[1]);

        // both less
        result = emailVerify.getVerifyResult("a-ful@email.com", "abaa", "full");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("ab-full@email.com", result[1]);

        // both more
        result = emailVerify.getVerifyResult("ababb-fullyy@email.com", "abaa", "full");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("ab-full@email.com", result[1]);

        // only first
        result = emailVerify.getVerifyResult("ab-@email.com", "abaa", "full");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("ab-full@email.com", result[1]);

        // only last
        result = emailVerify.getVerifyResult("-full@email.com", "abaa", "full");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("ab-full@email.com", result[1]);
        result = emailVerify.getVerifyResult("full@email.com", "abaa", "full");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("ab-full@email.com", result[1]);

        // not contain '@'
        result = emailVerify.getVerifyResult("ccc", "abaa", "full");
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), result[0]);
        // not contain localpart
        result = emailVerify.getVerifyResult("@ccc", "abaa", "full");
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), result[0]);

        // first name empty
        result = emailVerify.getVerifyResult("ccc", "", "full");
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), result[0]);
        // first empty, only has last
        result = emailVerify.getVerifyResult("ful@email.com", "", "full");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("full@email.com", result[1]);
        result = emailVerify.getVerifyResult("full@email.com", "", "full");
        assertEquals(EmailVerifyResult.VALID.getResultValue(), result[0]);

        // last name empty
        result = emailVerify.getVerifyResult("ccc", "abc", "");
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), result[0]);
        // last empty, only has first
        result = emailVerify.getVerifyResult("a@email.com", "abaa", "");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("ab@email.com", result[1]);
        result = emailVerify.getVerifyResult("ab@email.com", "abaa", "");
        assertEquals(EmailVerifyResult.VALID.getResultValue(), result[0]);

        // empty email -- invalid
        result = emailVerify.getVerifyResult("", "abc", "full");
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), result[0]);

        // email = null
        result = emailVerify.getVerifyResult(null, "abc", "full");
        assertEquals(EmailVerifyResult.INVALID.getResultValue(), result[0]);
    }

    @Test
    public void testUseColumnContent_2() {
        // use all of the firstname, use 2 first of the last name
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, false, "L", "20", "0", "2", "0", "-");

        assertEquals(EmailVerifyResult.VALID.getResultValue(),
                emailVerify.getVerifyResult("full-aa@email.com", "full", "aaaa")[0]);
        // first n<all
        String[] result = emailVerify.getVerifyResult("fu-aa@email.com", "full", "aaaa"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("full-aa@email.com", result[1]);

        // first n>all
        result = emailVerify.getVerifyResult("fullyy-aa@email.com", "full", "aaaa"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("full-aa@email.com", result[1]);

        // last n<2
        result = emailVerify.getVerifyResult("full-b@email.com", "full", "aaaa");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("full-aa@email.com", result[1]);

        // last n>2
        result = emailVerify.getVerifyResult("full-aba@email.com", "full", "aaaa");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("full-aa@email.com", result[1]);

        // both less
        result = emailVerify.getVerifyResult("ful-b@email.com", "full", "aaaa");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("full-aa@email.com", result[1]);

        // both more
        result = emailVerify.getVerifyResult("ababb-fullyy@email.com", "full", "aaaa");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("full-aa@email.com", result[1]);
    }

    @Test
    public void testUseColumnContent_4() {
        // use 2+1 of the firstname, use 4+3 first of the last name
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, false, "L", "2", "1", "4", "3", "-");

        assertEquals(EmailVerifyResult.VALID.getResultValue(),
                emailVerify.getVerifyResult("124-1234678@email.com", "1234", "12345678")[0]);
        // first n<all
        String[] result = emailVerify.getVerifyResult("1-1234678@email.com", "1234", "12345678"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("124-1234678@email.com", result[1]);

        // first n>all
        result = emailVerify.getVerifyResult("1234-1234678@email.com", "1234", "12345678"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("124-1234678@email.com", result[1]);

        // last n<2
        result = emailVerify.getVerifyResult("124-12@email.com", "1234", "12345678");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("124-1234678@email.com", result[1]);

        // last n>2
        result = emailVerify.getVerifyResult("124-123456789@email.com", "1234", "12345678");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("124-1234678@email.com", result[1]);

        // both less
        result = emailVerify.getVerifyResult("1-1@email.com", "1234", "12345678");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("124-1234678@email.com", result[1]);

        // both more
        result = emailVerify.getVerifyResult("11111-222222222@email.com", "1234", "12345678");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(), result[0]);
        assertEquals("124-1234678@email.com", result[1]);
    }

    // use invalid parameters of each string
    @Test
    public void testUseColumnContent_3() {
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, false, "L", "ab", "cc", "x", "z", "-");
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(),
                emailVerify.getVerifyResult("124-1234678@email.com", "1234", "12345678")[0]);

        // use 0 of the firstname, use all of the last name
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, false, "L", "0", "0", "10", "0", "-");
        assertEquals(EmailVerifyResult.VALID.getResultValue(),
                emailVerify.getVerifyResult("12345678@email.com", "1234", "12345678")[0]);

        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(),
                emailVerify.getVerifyResult("1-12345678@email.com", "1234", "12345678")[0]);

    }

    // test case sensitive
    @Test
    public void testUseColumnContent_5() {
        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, true, "K", "0", "2", "0", "2", "-");
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.getVerifyResult("aA-bB@email.com", "AaA", "BbB")[0]);

        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(),
                emailVerify.getVerifyResult("aa-BB@email.com", "AaA", "BbB")[0]);
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(),
                emailVerify.getVerifyResult("aa-bb@email.com", "AaA", "BbB")[0]);

        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, true, "L", "0", "2", "0", "2", "-");
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.getVerifyResult("aa-bb@email.com", "AAA", "BBB")[0]);

        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(),
                emailVerify.getVerifyResult("aa-BB@email.com", "AAA", "BBB")[0]);
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(),
                emailVerify.getVerifyResult("Aa-Bb@email.com", "AAA", "BBB")[0]);

        emailVerify = emailVerify.addLocalPartColumnContentChecker(true, true, "U", "0", "2", "0", "2", "-");
        assertEquals(EmailVerifyResult.VALID.getResultValue(), emailVerify.getVerifyResult("AA-BB@email.com", "aaa", "bbb")[0]);

        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(),
                emailVerify.getVerifyResult("aa-BB@email.com", "AAA", "BBB")[0]);
        assertEquals(EmailVerifyResult.CORRECTED.getResultValue(),
                emailVerify.getVerifyResult("aa-bb@email.com", "AAA", "BBB")[0]);

    }
}
