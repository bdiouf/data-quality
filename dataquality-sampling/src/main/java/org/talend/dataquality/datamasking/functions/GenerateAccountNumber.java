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

import java.math.BigInteger;

/**
 * created by jgonzalez on 24 juin 2015. This class holds all the functions required by GenerateAccountNumberFormat and
 * GenerateAccountNumberFormat.
 *
 */
public abstract class GenerateAccountNumber extends Function<String> {

    private static final long serialVersionUID = -631455182627735683L;

    private static final BigInteger MOD97 = new BigInteger("97"); //$NON-NLS-1$

    /**
     * This functions checks if the account number (ie, FR043568953359583693) is correct according to its size and
     * country.
     * 
     * @param accountNumber The account number given in input.
     * @return A boolean holding if the account number is correct or not.
     */
    private static boolean sizeOk(String accountNumber) {
        int length = accountNumber.length();
        String country = accountNumber.substring(0, 2);

        switch (length) {
        case 15:
            return (country.equals("NO")); //$NON-NLS-1$
        case 16:
            return (country.equals("BE") || country.equals("BI")); //$NON-NLS-1$ //$NON-NLS-2$
        case 18:
            return (country.equals("DK") || country.equals("FO") || country.equals("FI") || country.equals("GL") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    || country.equals("NL")); //$NON-NLS-1$
        case 19:
            return (country.equals("MK") || country.equals("SI")); //$NON-NLS-1$ //$NON-NLS-2$
        case 20:
            return (country.equals("AT") || country.equals("BA") || country.equals("EE") || country.equals("KZ") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    || country.equals("XK") || country.equals("LT") //$NON-NLS-1$ //$NON-NLS-2$
                    || country.equals("LU")); //$NON-NLS-1$
        case 21:
            return (country.equals("CR") || country.equals("HR") || country.equals("LV") || country.equals("LI") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    || country.equals("CH")); //$NON-NLS-1$
        case 22:
            return (country.equals("BH") || country.equals("BG") || country.equals("GE") || country.equals("DE") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    || country.equals("IE") || country.equals("ME") //$NON-NLS-1$ //$NON-NLS-2$
                    || country.equals("RS") || country.equals("GB")); //$NON-NLS-1$ //$NON-NLS-2$
        case 23:
            return (country.equals("GI") || country.equals("IL") || country.equals("AE")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        case 24:
            return (country.equals("AD") || country.equals("CZ") || country.equals("CZ") || country.equals("MD") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    || country.equals("PK") || country.equals("RO") //$NON-NLS-1$ //$NON-NLS-2$
                    || country.equals("SA") || country.equals("SK") || country.equals("ES") || country.equals("SE") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    || country.equals("TN") || country.equals("VG") //$NON-NLS-1$ //$NON-NLS-2$
                    || country.equals("DZ")); //$NON-NLS-1$
        case 25:
            return (country.equals("PT") || country.equals("AO") || country.equals("CV") || country.equals("MZ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        case 26:
            return (country.equals("IS") || country.equals("TR") || country.equals("IR")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        case 27:
            return (country.equals("FR") || country.equals("GR") || country.equals("IT") || country.equals("MR") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    || country.equals("MC") || country.equals("SM") //$NON-NLS-1$ //$NON-NLS-2$
                    || country.equals("CM") || country.equals("MG") || country.equals("BF")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        case 28:
            return (country.equals("AL") || country.equals("AZ") || country.equals("CY") || country.equals("DO") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    || country.equals("GT") || country.equals("HU") //$NON-NLS-1$ //$NON-NLS-2$
                    || country.equals("LB") || country.equals("PL") || country.equals("BJ") || country.equals("CI") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    || country.equals("ML") || country.equals("SN")); //$NON-NLS-1$ //$NON-NLS-2$
        case 29:
            return (country.equals("BR") || country.equals("PS") || country.equals("QA") || country.equals("UA")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        case 30:
            return (country.equals("JO") || country.equals("KW") || country.equals("MU")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        case 31:
            return (country.equals("MT")); //$NON-NLS-1$
        }
        return false;
    }

    /**
     * This function generates an american account number.
     * 
     * @param accountNumber The account number given in input.
     * @return An american account number, the nine first digits belonging to the original input and the others randomly
     * generated.
     */
    private String generateAmericanAccountNumber(String accountNumber, boolean keep) {
        StringBuilder sb = new StringBuilder(replaceSpacesInString(accountNumber).substring(0, 9)); // $NON-NLS-1$
                                                                                                    // //$NON-NLS-2$
        for (int i = 0; i < 10; ++i) {
            sb.append(String.valueOf(rnd.nextInt(10)));
        }
        if (keep) {
            for (int i = 0; i < accountNumber.length(); ++i) {
                if (String.valueOf(" ").equals(accountNumber.charAt(i))) { //$NON-NLS-1$
                    sb.insert(i, ' ');
                }
            }
        } else {
            sb.insert(9, ' ');
        }
        return sb.toString();
    }

    /**
     * This function checks if the account number is an American number account.
     * 
     * @param accountNumber The account number given in input.
     * @return A boolean holding if the account number is correct or not.
     */
    private boolean isAmericanAccount(String accountNumber) {
        String rtn = replaceSpacesInString(accountNumber).substring(0, 9); // $NON-NLS-1$ //$NON-NLS-2$

        char[] rtn_chars = rtn.toCharArray();
        for (char c : rtn_chars) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return ((3
                * (Integer.parseInt(String.valueOf(rtn.charAt(0))) + Integer.parseInt(String.valueOf(rtn.charAt(3)))
                        + Integer.parseInt(String.valueOf(rtn.charAt(6))))
                + 7 * (Integer.parseInt(String.valueOf(rtn.charAt(1))) + Integer.parseInt(String.valueOf(rtn.charAt(4)))
                        + Integer.parseInt(String.valueOf(rtn.charAt(7))))
                + (Integer.parseInt(String.valueOf(rtn.charAt(2))) + Integer.parseInt(String.valueOf(rtn.charAt(5)))
                        + Integer.parseInt(String.valueOf(rtn.charAt(8)))))
                % 10 == 0);
    }

    /**
     * This function generates an account number. If the parameter is a correct iban number, this function will generate
     * an iban number, keeping the original country of where it's from (if the iban it's incorrect, the countru will be
     * France (see generateIban())). If the parameter is an correct american account number, this function will generate
     * a american number (see generateAmericanAccount).
     * 
     * @param number The account number given in input.
     * @return A correct account number.
     */
    public String generateIban(String number, boolean keep) {
        if (Character.isDigit(number.charAt(0))) {
            if (isAmericanAccount(number)) {
                return generateAmericanAccountNumber(number, keep).toString();
            }
        }

        String accountNumber = replaceSpacesInString(number); // $NON-NLS-1$ //$NON-NLS-2$
        if (!sizeOk(accountNumber)) {
            return generateIban();
        }

        StringBuilder sb = new StringBuilder(accountNumber.substring(0, 2));
        sb.append("00"); //$NON-NLS-1$
        for (int i = 0; i < accountNumber.length() - 4; ++i) {
            sb.append(String.valueOf(rnd.nextInt(10)));
        }

        String sb2 = new String(sb);
        sb2 = sb2.substring(4) + sb2.substring(0, 4);

        StringBuilder numericAccountNumber = new StringBuilder();
        for (int i = 0; i < sb2.length(); i++) {
            numericAccountNumber.append(Character.getNumericValue(sb2.charAt(i)));
        }

        BigInteger ibanNumber = new BigInteger(numericAccountNumber.toString());
        int check_digits = 98 - ibanNumber.mod(MOD97).intValue();

        sb.setCharAt(2, Character.forDigit(check_digits / 10, 10));
        sb.setCharAt(3, Character.forDigit(check_digits % 10, 10));

        if (keep) {
            for (int i = 0; i < number.length(); ++i) {
                if (String.valueOf(" ").equals(number.charAt(i))) { //$NON-NLS-1$
                    sb.insert(i, ' ');
                }
            }
        } else {
            for (int i = 4; i < sb.length(); i += 5) {
                sb.insert(i, ' ');
            }
        }

        return sb.toString();
    }

    /**
     * This function is called when generating a French Iban number. The last two digits must be generated using an
     * algorithm.
     * 
     * @param sb The French Iban number missing its two last digits.
     * @return An int holding the two digits needed to complete the iban.
     */
    private static int generateRib(String sb) {
        Long b = Long.valueOf(sb.substring(0, 5));
        Long g = Long.valueOf(sb.substring(5, 10));

        StringBuilder csb = new StringBuilder(""); //$NON-NLS-1$
        for (int i = 10; i < 16; ++i) {
            csb.append(Character.getNumericValue(sb.charAt(i)));
        }
        Long d = Long.valueOf(csb.toString());

        csb = new StringBuilder(""); //$NON-NLS-1$

        for (int i = 16; i < 21; ++i) {
            csb.append(Character.getNumericValue(sb.charAt(i)));
        }
        Long c = Long.valueOf(csb.toString());

        return (int) (97 - ((89 * b + 15 * g + 76 * d + 3 * c) % 97));
    }

    /**
     * This function generates a correct French Iban number.
     * 
     * @return A string holding a correct French Iban number.
     */
    public String generateIban() {
        StringBuilder sb = new StringBuilder("FR00"); //$NON-NLS-1$
        for (int i = 0; i < 10; ++i) {
            sb.append(String.valueOf(rnd.nextInt(10)));
        }
        for (int i = 0; i < 11; ++i) {
            int choice = rnd.nextInt(2);
            if (choice == 0) {
                sb.append(String.valueOf(rnd.nextInt(10)));
            } else {
                sb.append(new Character((char) (rnd.nextInt(26) + 'A')));
            }
        }

        int rib = generateRib(sb.substring(4));
        sb.append(rib / 10);
        sb.append(rib % 10);

        String sb2 = new String(sb);
        sb2 = sb2.substring(4) + sb2.substring(0, 4);

        StringBuilder numericAccountNumber = new StringBuilder();
        for (int i = 0; i < sb2.length(); i++) {
            numericAccountNumber.append(Character.getNumericValue(sb2.charAt(i)));
        }

        BigInteger ibanNumber = new BigInteger(numericAccountNumber.toString());
        int check_digits = 98 - ibanNumber.mod(MOD97).intValue();

        sb.setCharAt(2, Character.forDigit(check_digits / 10, 10));
        sb.setCharAt(3, Character.forDigit(check_digits % 10, 10));

        for (int i = 4; i < sb.length(); i += 5) {
            sb.insert(i, ' ');
        }

        return sb.toString();
    }

}
