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
package org.talend.dataquality.datamasking;

import org.talend.dataquality.datamasking.Functions.BetweenIndexes;
import org.talend.dataquality.datamasking.Functions.BetweenIndexesKeep;
import org.talend.dataquality.datamasking.Functions.BetweenIndexesRemove;
import org.talend.dataquality.datamasking.Functions.BetweenIndexesReplace;
import org.talend.dataquality.datamasking.Functions.DateVariance;
import org.talend.dataquality.datamasking.Functions.GenerateAccountNumberFormat;
import org.talend.dataquality.datamasking.Functions.GenerateAccountNumberSimple;
import org.talend.dataquality.datamasking.Functions.GenerateBetween;
import org.talend.dataquality.datamasking.Functions.GenerateBetweenDate;
import org.talend.dataquality.datamasking.Functions.GenerateBetweenDouble;
import org.talend.dataquality.datamasking.Functions.GenerateBetweenFloat;
import org.talend.dataquality.datamasking.Functions.GenerateBetweenInteger;
import org.talend.dataquality.datamasking.Functions.GenerateBetweenLong;
import org.talend.dataquality.datamasking.Functions.GenerateBetweenString;
import org.talend.dataquality.datamasking.Functions.GenerateCreditCardFormatLong;
import org.talend.dataquality.datamasking.Functions.GenerateCreditCardFormatString;
import org.talend.dataquality.datamasking.Functions.GenerateCreditCardLong;
import org.talend.dataquality.datamasking.Functions.GenerateCreditCardSimple;
import org.talend.dataquality.datamasking.Functions.GenerateCreditCardString;
import org.talend.dataquality.datamasking.Functions.GenerateFromFile;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileHash;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileHashInteger;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileHashLong;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileHashString;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileInteger;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileLong;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileString;
import org.talend.dataquality.datamasking.Functions.GenerateFromList;
import org.talend.dataquality.datamasking.Functions.GenerateFromListHash;
import org.talend.dataquality.datamasking.Functions.GenerateFromListHashInteger;
import org.talend.dataquality.datamasking.Functions.GenerateFromListHashLong;
import org.talend.dataquality.datamasking.Functions.GenerateFromListHashString;
import org.talend.dataquality.datamasking.Functions.GenerateFromListInteger;
import org.talend.dataquality.datamasking.Functions.GenerateFromListLong;
import org.talend.dataquality.datamasking.Functions.GenerateFromListString;
import org.talend.dataquality.datamasking.Functions.GenerateFromPattern;
import org.talend.dataquality.datamasking.Functions.GeneratePhoneNumberFrench;
import org.talend.dataquality.datamasking.Functions.GeneratePhoneNumberGermany;
import org.talend.dataquality.datamasking.Functions.GeneratePhoneNumberJapan;
import org.talend.dataquality.datamasking.Functions.GeneratePhoneNumberUK;
import org.talend.dataquality.datamasking.Functions.GeneratePhoneNumberUS;
import org.talend.dataquality.datamasking.Functions.GenerateSequenceDouble;
import org.talend.dataquality.datamasking.Functions.GenerateSequenceFloat;
import org.talend.dataquality.datamasking.Functions.GenerateSequenceInteger;
import org.talend.dataquality.datamasking.Functions.GenerateSequenceLong;
import org.talend.dataquality.datamasking.Functions.GenerateSequenceString;
import org.talend.dataquality.datamasking.Functions.GenerateSsnFr;
import org.talend.dataquality.datamasking.Functions.GenerateSsnGermany;
import org.talend.dataquality.datamasking.Functions.GenerateSsnJapan;
import org.talend.dataquality.datamasking.Functions.GenerateSsnUk;
import org.talend.dataquality.datamasking.Functions.GenerateSsnUs;
import org.talend.dataquality.datamasking.Functions.GenerateUuid;
import org.talend.dataquality.datamasking.Functions.KeepFirstAndGenerate;
import org.talend.dataquality.datamasking.Functions.KeepFirstAndGenerateInteger;
import org.talend.dataquality.datamasking.Functions.KeepFirstAndGenerateLong;
import org.talend.dataquality.datamasking.Functions.KeepFirstAndGenerateString;
import org.talend.dataquality.datamasking.Functions.KeepLastAndGenerate;
import org.talend.dataquality.datamasking.Functions.KeepLastAndGenerateInteger;
import org.talend.dataquality.datamasking.Functions.KeepLastAndGenerateLong;
import org.talend.dataquality.datamasking.Functions.KeepLastAndGenerateString;
import org.talend.dataquality.datamasking.Functions.KeepYear;
import org.talend.dataquality.datamasking.Functions.MaskAddress;
import org.talend.dataquality.datamasking.Functions.MaskEmail;
import org.talend.dataquality.datamasking.Functions.NumericVariance;
import org.talend.dataquality.datamasking.Functions.NumericVarianceDouble;
import org.talend.dataquality.datamasking.Functions.NumericVarianceFloat;
import org.talend.dataquality.datamasking.Functions.NumericVarianceInteger;
import org.talend.dataquality.datamasking.Functions.NumericVarianceLong;
import org.talend.dataquality.datamasking.Functions.RemoveFirstChars;
import org.talend.dataquality.datamasking.Functions.RemoveFirstCharsInteger;
import org.talend.dataquality.datamasking.Functions.RemoveFirstCharsLong;
import org.talend.dataquality.datamasking.Functions.RemoveFirstCharsString;
import org.talend.dataquality.datamasking.Functions.RemoveLastChars;
import org.talend.dataquality.datamasking.Functions.RemoveLastCharsInteger;
import org.talend.dataquality.datamasking.Functions.RemoveLastCharsLong;
import org.talend.dataquality.datamasking.Functions.RemoveLastCharsString;
import org.talend.dataquality.datamasking.Functions.ReplaceAll;
import org.talend.dataquality.datamasking.Functions.ReplaceCharacters;
import org.talend.dataquality.datamasking.Functions.ReplaceFirstChars;
import org.talend.dataquality.datamasking.Functions.ReplaceFirstCharsInteger;
import org.talend.dataquality.datamasking.Functions.ReplaceFirstCharsLong;
import org.talend.dataquality.datamasking.Functions.ReplaceFirstCharsString;
import org.talend.dataquality.datamasking.Functions.ReplaceLastChars;
import org.talend.dataquality.datamasking.Functions.ReplaceLastCharsInteger;
import org.talend.dataquality.datamasking.Functions.ReplaceLastCharsLong;
import org.talend.dataquality.datamasking.Functions.ReplaceLastCharsString;
import org.talend.dataquality.datamasking.Functions.ReplaceNumeric;
import org.talend.dataquality.datamasking.Functions.ReplaceNumericDouble;
import org.talend.dataquality.datamasking.Functions.ReplaceNumericFloat;
import org.talend.dataquality.datamasking.Functions.ReplaceNumericInteger;
import org.talend.dataquality.datamasking.Functions.ReplaceNumericLong;
import org.talend.dataquality.datamasking.Functions.ReplaceNumericString;
import org.talend.dataquality.datamasking.Functions.SetToNull;

/**
 * created by jgonzalez on 18 juin 2015. This enum stores all the functions that can be used in the component.
 *
 */
public enum FunctionType {
    BETWEEN_INDEXES(BetweenIndexes.class),
    BETWEEN_INDEXES_REPLACE(BetweenIndexesReplace.class),
    BETWEEN_INDEXES_KEEP(BetweenIndexesKeep.class),
    BETWEEN_INDEXES_REMOVE(BetweenIndexesRemove.class),
    DATE_VARIANCE(DateVariance.class),
    GENERATE_ACCOUNT_NUMBER(GenerateAccountNumberSimple.class),
    GENERATE_ACCOUNT_NUMBER_FORMAT(GenerateAccountNumberFormat.class),
    GENERATE_BETWEEN(GenerateBetween.class),
    GENERATE_BETWEEN_DATE(GenerateBetweenDate.class),
    GENERATE_BETWEEN_DOUBLE(GenerateBetweenDouble.class),
    GENERATE_BETWEEN_FLOAT(GenerateBetweenFloat.class),
    GENERATE_BETWEEN_INT(GenerateBetweenInteger.class),
    GENERATE_BETWEEN_LONG(GenerateBetweenLong.class),
    GENERATE_BETWEEN_STRING(GenerateBetweenString.class),
    GENERATE_CREDIT_CARD_FORMAT(GenerateCreditCardSimple.class),
    GENERATE_CREDIT_CARD_FORMAT_LONG(GenerateCreditCardFormatLong.class),
    GENERATE_CREDIT_CARD_FORMAT_STRING(GenerateCreditCardFormatString.class),
    GENERATE_CREDIT_CARD(GenerateCreditCardSimple.class),
    GENERATE_CREDIT_CARD_LONG(GenerateCreditCardLong.class),
    GENERATE_CREDIT_CARD_STRING(GenerateCreditCardString.class),
    GENERATE_FROM_FILE(GenerateFromFile.class),
    GENERATE_FROM_FILE_INT(GenerateFromFileInteger.class),
    GENERATE_FROM_FILE_LONG(GenerateFromFileLong.class),
    GENERATE_FROM_FILE_STRING(GenerateFromFileString.class),
    GENERATE_FROM_FILE_HASH(GenerateFromFileHash.class),
    GENERATE_FROM_FILE_HASH_INT(GenerateFromFileHashInteger.class),
    GENERATE_FROM_FILE_HASH_LONG(GenerateFromFileHashLong.class),
    GENERATE_FROM_FILE_HASH_STRING(GenerateFromFileHashString.class),
    GENERATE_FROM_LIST(GenerateFromList.class),
    GENERATE_FROM_LIST_INT(GenerateFromListInteger.class),
    GENERATE_FROM_LIST_LONG(GenerateFromListLong.class),
    GENERATE_FROM_LIST_STRING(GenerateFromListString.class),
    GENERATE_FROM_LIST_HASH(GenerateFromListHash.class),
    GENERATE_FROM_LIST_HASH_INT(GenerateFromListHashInteger.class),
    GENERATE_FROM_LIST_HASH_LONG(GenerateFromListHashLong.class),
    GENERATE_FROM_LIST_HASH_STRING(GenerateFromListHashString.class),
    GENERATE_FROM_PATTERN(GenerateFromPattern.class),
    GENERATE_PHONE_NUMBER_FRENCH(GeneratePhoneNumberFrench.class),
    GENERATE_PHONE_NUMBER_GERMANY(GeneratePhoneNumberGermany.class),
    GENERATE_PHONE_NUMBER_JAPAN(GeneratePhoneNumberJapan.class),
    GENERATE_PHONE_NUMBER_UK(GeneratePhoneNumberUK.class),
    GENERATE_PHONE_NUMBER_US(GeneratePhoneNumberUS.class),
    GENERATE_SEQUENCE(null),
    GENERATE_SEQUENCE_DOUBLE(GenerateSequenceDouble.class),
    GENERATE_SEQUENCE_FLOAT(GenerateSequenceFloat.class),
    GENERATE_SEQUENCE_INT(GenerateSequenceInteger.class),
    GENERATE_SEQUENCE_LONG(GenerateSequenceLong.class),
    GENERATE_SEQUENCE_STRING(GenerateSequenceString.class),
    GENERATE_SSN_FRENCH(GenerateSsnFr.class),
    GENERATE_SSN_GERMANY(GenerateSsnGermany.class),
    GENERATE_SSN_JAPAN(GenerateSsnJapan.class),
    GENERATE_SSN_UK(GenerateSsnUk.class),
    GENERATE_SSN_US(GenerateSsnUs.class),
    GENERATE_UUID(GenerateUuid.class),
    KEEP_FIRST_AND_GENERATE(KeepFirstAndGenerate.class),
    KEEP_FIRST_AND_GENERATE_INT(KeepFirstAndGenerateInteger.class),
    KEEP_FIRST_AND_GENERATE_LONG(KeepFirstAndGenerateLong.class),
    KEEP_FIRST_AND_GENERATE_STRING(KeepFirstAndGenerateString.class),
    KEEP_LAST_AND_GENERATE(KeepLastAndGenerate.class),
    KEEP_LAST_AND_GENERATE_INT(KeepLastAndGenerateInteger.class),
    KEEP_LAST_AND_GENERATE_LONG(KeepLastAndGenerateLong.class),
    KEEP_LAST_AND_GENERATE_STRING(KeepLastAndGenerateString.class),
    KEEP_YEAR(KeepYear.class),
    MASK_ADDRESS(MaskAddress.class),
    MASK_EMAIL(MaskEmail.class),
    NUMERIC_VARIANCE(NumericVariance.class),
    NUMERIC_VARIANCE_DOUBLE(NumericVarianceDouble.class),
    NUMERIC_VARIANCE_FlOAT(NumericVarianceFloat.class),
    NUMERIC_VARIANCE_INT(NumericVarianceInteger.class),
    NUMERIC_VARIANCE_LONG(NumericVarianceLong.class),
    REMOVE_FIRST_CHARS(RemoveFirstChars.class),
    REMOVE_FIRST_CHARS_INT(RemoveFirstCharsInteger.class),
    REMOVE_FIRST_CHARS_LONG(RemoveFirstCharsLong.class),
    REMOVE_FIRST_CHARS_STRING(RemoveFirstCharsString.class),
    REMOVE_LAST_CHARS(RemoveLastChars.class),
    REMOVE_LAST_CHARS_INT(RemoveLastCharsInteger.class),
    REMOVE_LAST_CHARS_LONG(RemoveLastCharsLong.class),
    REMOVE_LAST_CHARS_STRING(RemoveLastCharsString.class),
    REPLACE_ALL(ReplaceAll.class),
    REPLACE_CHARACTERS(ReplaceCharacters.class),
    REPLACE_FIRST_CHARS(ReplaceFirstChars.class),
    REPLACE_FIRST_CHARS_INT(ReplaceFirstCharsInteger.class),
    REPLACE_FIRST_CHARS_LONG(ReplaceFirstCharsLong.class),
    REPLACE_FIRST_CHARS_STRING(ReplaceFirstCharsString.class),
    REPLACE_LAST_CHARS(ReplaceLastChars.class),
    REPLACE_LAST_CHARS_INT(ReplaceLastCharsInteger.class),
    REPLACE_LAST_CHARS_LONG(ReplaceLastCharsLong.class),
    REPLACE_LAST_CHARS_STRING(ReplaceLastCharsString.class),
    REPLACE_NUMERIC(ReplaceNumeric.class),
    REPLACE_NUMERIC_DOUBLE(ReplaceNumericDouble.class),
    REPLACE_NUMERIC_FLOAT(ReplaceNumericFloat.class),
    REPLACE_NUMERIC_INT(ReplaceNumericInteger.class),
    REPLACE_NUMERIC_LONG(ReplaceNumericLong.class),
    REPLACE_NUMERIC_STRING(ReplaceNumericString.class),
    SET_TO_NULL(SetToNull.class);

    private final Class<?> clazz;

    FunctionType(Class<?> clzz) {
        this.clazz = clzz;
    }

    /**
     * Getter for clazz.
     * 
     * @return The class of the function.
     */
    public Class<?> getClazz() {
        return this.clazz;
    }

}