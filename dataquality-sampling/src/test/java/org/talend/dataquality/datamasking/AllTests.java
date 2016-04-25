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
package org.talend.dataquality.datamasking;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.dataquality.datamasking.functions.BetweenIndexesKeepTest;
import org.talend.dataquality.datamasking.functions.BetweenIndexesRemoveTest;
import org.talend.dataquality.datamasking.functions.BetweenIndexesReplaceTest;
import org.talend.dataquality.datamasking.functions.DateVarianceTest;
import org.talend.dataquality.datamasking.functions.GenerateAccountNumberFormatTest;
import org.talend.dataquality.datamasking.functions.GenerateAccountNumberSimpleTest;
import org.talend.dataquality.datamasking.functions.GenerateBetweenDateTest;
import org.talend.dataquality.datamasking.functions.GenerateBetweenIntegerTest;
import org.talend.dataquality.datamasking.functions.GenerateBetweenLongTest;
import org.talend.dataquality.datamasking.functions.GenerateBetweenStringTest;
import org.talend.dataquality.datamasking.functions.GenerateCreditCardFormatLongTest;
import org.talend.dataquality.datamasking.functions.GenerateCreditCardFormatStringTest;
import org.talend.dataquality.datamasking.functions.GenerateCreditCardLongTest;
import org.talend.dataquality.datamasking.functions.GenerateCreditCardStringTest;
import org.talend.dataquality.datamasking.functions.GenerateFromFileHashIntegerTest;
import org.talend.dataquality.datamasking.functions.GenerateFromFileHashLongTest;
import org.talend.dataquality.datamasking.functions.GenerateFromFileHashStringTest;
import org.talend.dataquality.datamasking.functions.GenerateFromFileIntegerTest;
import org.talend.dataquality.datamasking.functions.GenerateFromFileLongTest;
import org.talend.dataquality.datamasking.functions.GenerateFromFileStringTest;
import org.talend.dataquality.datamasking.functions.GenerateFromListHashIntegerTest;
import org.talend.dataquality.datamasking.functions.GenerateFromListHashLongTest;
import org.talend.dataquality.datamasking.functions.GenerateFromListHashStringTest;
import org.talend.dataquality.datamasking.functions.GenerateFromListIntegerTest;
import org.talend.dataquality.datamasking.functions.GenerateFromListLongTest;
import org.talend.dataquality.datamasking.functions.GenerateFromListStringTest;
import org.talend.dataquality.datamasking.functions.GenerateFromPatternTest;
import org.talend.dataquality.datamasking.functions.GeneratePhoneNumberFrenchTest;
import org.talend.dataquality.datamasking.functions.GeneratePhoneNumberGermanyTest;
import org.talend.dataquality.datamasking.functions.GeneratePhoneNumberJapanTest;
import org.talend.dataquality.datamasking.functions.GeneratePhoneNumberUkTest;
import org.talend.dataquality.datamasking.functions.GeneratePhoneNumberUsTest;
import org.talend.dataquality.datamasking.functions.GenerateSsnFrenchTest;
import org.talend.dataquality.datamasking.functions.GenerateSsnGermanyTest;
import org.talend.dataquality.datamasking.functions.GenerateSsnJapanTest;
import org.talend.dataquality.datamasking.functions.GenerateSsnUkTest;
import org.talend.dataquality.datamasking.functions.GenerateSsnUsTest;
import org.talend.dataquality.datamasking.functions.KeepFirstAndGenerateIntegerTest;
import org.talend.dataquality.datamasking.functions.KeepFirstAndGenerateLongTest;
import org.talend.dataquality.datamasking.functions.KeepFirstAndGenerateStringTest;
import org.talend.dataquality.datamasking.functions.KeepLastAndGenerateIntegerTest;
import org.talend.dataquality.datamasking.functions.KeepLastAndGenerateLongTest;
import org.talend.dataquality.datamasking.functions.KeepLastAndGenerateStringTest;
import org.talend.dataquality.datamasking.functions.KeepYearTest;
import org.talend.dataquality.datamasking.functions.MaskAddressTest;
import org.talend.dataquality.datamasking.functions.MaskEmailTest;
import org.talend.dataquality.datamasking.functions.NumericVarianceIntegerTest;
import org.talend.dataquality.datamasking.functions.NumericVarianceLongTest;
import org.talend.dataquality.datamasking.functions.RemoveFirstCharsIntegerTest;
import org.talend.dataquality.datamasking.functions.RemoveFirstCharsLongTest;
import org.talend.dataquality.datamasking.functions.RemoveFirstCharsStringTest;
import org.talend.dataquality.datamasking.functions.RemoveLastCharsIntegerTest;
import org.talend.dataquality.datamasking.functions.RemoveLastCharsLongTest;
import org.talend.dataquality.datamasking.functions.RemoveLastCharsStringTest;
import org.talend.dataquality.datamasking.functions.ReplaceAllTest;
import org.talend.dataquality.datamasking.functions.ReplaceCharactersTest;
import org.talend.dataquality.datamasking.functions.ReplaceFirstCharsIntegerTest;
import org.talend.dataquality.datamasking.functions.ReplaceFirstCharsLongTest;
import org.talend.dataquality.datamasking.functions.ReplaceFirstCharsStringTest;
import org.talend.dataquality.datamasking.functions.ReplaceLastCharsIntegerTest;
import org.talend.dataquality.datamasking.functions.ReplaceLastCharsLongTest;
import org.talend.dataquality.datamasking.functions.ReplaceLastCharsStringTest;
import org.talend.dataquality.datamasking.functions.ReplaceNumericIntegerTest;
import org.talend.dataquality.datamasking.functions.ReplaceNumericLongTest;
import org.talend.dataquality.datamasking.functions.ReplaceNumericStringTest;
import org.talend.dataquality.datamasking.functions.SetToNullTest;

/**
 * created by jgonzalez on 20 août 2015 Detailled comment
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ BetweenIndexesKeepTest.class, BetweenIndexesRemoveTest.class, BetweenIndexesReplaceTest.class,
        DateVarianceTest.class, GenerateAccountNumberFormatTest.class, GenerateAccountNumberSimpleTest.class,
        GenerateBetweenDateTest.class, GenerateBetweenIntegerTest.class, GenerateBetweenLongTest.class,
        GenerateBetweenStringTest.class, GenerateCreditCardFormatLongTest.class, GenerateCreditCardFormatStringTest.class,
        GenerateCreditCardLongTest.class, GenerateCreditCardStringTest.class, GenerateFromFileHashIntegerTest.class,
        GenerateFromFileHashLongTest.class, GenerateFromFileHashStringTest.class, GenerateFromFileIntegerTest.class,
        GenerateFromFileLongTest.class, GenerateFromFileStringTest.class, GenerateFromListHashIntegerTest.class,
        GenerateFromListHashLongTest.class, GenerateFromListHashStringTest.class, GenerateFromListIntegerTest.class,
        GenerateFromListLongTest.class, GenerateFromListStringTest.class, GenerateFromPatternTest.class,
        GeneratePhoneNumberFrenchTest.class, GeneratePhoneNumberGermanyTest.class, GeneratePhoneNumberJapanTest.class,
        GeneratePhoneNumberUkTest.class, GeneratePhoneNumberUsTest.class, GenerateSsnFrenchTest.class,
        GenerateSsnGermanyTest.class, GenerateSsnJapanTest.class, GenerateSsnUkTest.class, GenerateSsnUsTest.class,
        KeepFirstAndGenerateIntegerTest.class, KeepFirstAndGenerateLongTest.class, KeepFirstAndGenerateStringTest.class,
        KeepLastAndGenerateIntegerTest.class, KeepLastAndGenerateLongTest.class, KeepLastAndGenerateStringTest.class,
        KeepYearTest.class, MaskAddressTest.class, MaskEmailTest.class, NumericVarianceIntegerTest.class,
        NumericVarianceLongTest.class, RemoveFirstCharsIntegerTest.class, RemoveFirstCharsLongTest.class,
        RemoveFirstCharsStringTest.class, RemoveLastCharsIntegerTest.class, RemoveLastCharsLongTest.class,
        RemoveLastCharsStringTest.class, ReplaceAllTest.class, ReplaceCharactersTest.class, ReplaceFirstCharsIntegerTest.class,
        ReplaceFirstCharsLongTest.class, ReplaceFirstCharsStringTest.class, ReplaceLastCharsIntegerTest.class,
        ReplaceLastCharsLongTest.class, ReplaceLastCharsStringTest.class, ReplaceNumericIntegerTest.class,
        ReplaceNumericLongTest.class, ReplaceNumericStringTest.class, SetToNullTest.class })
public class AllTests {
    /*
     * Bloc intentionnaly left empty
     */
}
