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

import org.talend.dataquality.datamasking.functions.Function;

/**
 * created by jgonzalez on 18 juin 2015 This class is the factory that will instanciate the correct function.
 *
 */
public class FunctionFactory {

    private Function<?> getFunction(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        return (Function<?>) clazz.newInstance();
    }

    private Function<?> getFunction3(FunctionType type, int javaType) throws InstantiationException, IllegalAccessException {
        switch (type) {
        case REPLACE_LAST_CHARS:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.REPLACE_LAST_CHARS_INT.getClazz());
            case 1:
                return getFunction(FunctionType.REPLACE_LAST_CHARS_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.REPLACE_LAST_CHARS_STRING.getClazz());
            default:
                return null;
            }
        case REPLACE_NUMERIC:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.REPLACE_NUMERIC_INT.getClazz());
            case 1:
                return getFunction(FunctionType.REPLACE_NUMERIC_LONG.getClazz());
            case 2:
                return getFunction(FunctionType.REPLACE_NUMERIC_FLOAT.getClazz());
            case 3:
                return getFunction(FunctionType.REPLACE_NUMERIC_DOUBLE.getClazz());
            case 4:
                return getFunction(FunctionType.REPLACE_NUMERIC_STRING.getClazz());
            default:
                return null;
            }
        default:
            return getFunction(type.getClazz());
        }
    }

    private Function<?> getFunction2(FunctionType type, int javaType) throws InstantiationException, IllegalAccessException {

        switch (type) {
        case GENERATE_FROM_LIST_HASH:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.GENERATE_FROM_LIST_HASH_INT.getClazz());
            case 1:
                return getFunction(FunctionType.GENERATE_FROM_LIST_HASH_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.GENERATE_FROM_LIST_HASH_STRING.getClazz());
            default:
                return null;
            }
        case GENERATE_FROM_FILE_HASH:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.GENERATE_FROM_FILE_HASH_INT.getClazz());
            case 1:
                return getFunction(FunctionType.GENERATE_FROM_FILE_HASH_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.GENERATE_FROM_FILE_HASH_STRING.getClazz());
            default:
                return null;
            }
        case GENERATE_SEQUENCE:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.GENERATE_SEQUENCE_INT.getClazz());
            case 1:
                return getFunction(FunctionType.GENERATE_SEQUENCE_LONG.getClazz());
            case 2:
                return getFunction(FunctionType.GENERATE_SEQUENCE_FLOAT.getClazz());
            case 3:
                return getFunction(FunctionType.GENERATE_SEQUENCE_DOUBLE.getClazz());
            case 4:
                return getFunction(FunctionType.GENERATE_SEQUENCE_STRING.getClazz());
            default:
                return null;
            }
        case NUMERIC_VARIANCE:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.NUMERIC_VARIANCE_INT.getClazz());
            case 1:
                return getFunction(FunctionType.NUMERIC_VARIANCE_LONG.getClazz());
            case 2:
                return getFunction(FunctionType.NUMERIC_VARIANCE_FlOAT.getClazz());
            case 3:
                return getFunction(FunctionType.NUMERIC_VARIANCE_DOUBLE.getClazz());
            default:
                return null;
            }
        case REMOVE_FIRST_CHARS:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.REMOVE_FIRST_CHARS_INT.getClazz());
            case 1:
                return getFunction(FunctionType.REMOVE_FIRST_CHARS_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.REMOVE_FIRST_CHARS_STRING.getClazz());
            default:
                return null;
            }
        case REMOVE_LAST_CHARS:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.REMOVE_LAST_CHARS_INT.getClazz());
            case 1:
                return getFunction(FunctionType.REMOVE_LAST_CHARS_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.REMOVE_LAST_CHARS_STRING.getClazz());
            default:
                return null;
            }
        case REPLACE_FIRST_CHARS:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.REPLACE_FIRST_CHARS_INT.getClazz());
            case 1:
                return getFunction(FunctionType.REPLACE_FIRST_CHARS_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.REPLACE_FIRST_CHARS_STRING.getClazz());
            default:
                return null;
            }
        default:
            return getFunction3(type, javaType);
        }
    }

    /**
     * DOC jgonzalez Comment method "getFunction". This function is used to return the correct function according to the
     * user choice.
     * 
     * @param type The function requested by the user.
     * @param javaType Some functions work and several type, this parameter represents the wanted type.
     * @return A new function.
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Function<?> getFunction(FunctionType type, int javaType) throws InstantiationException, IllegalAccessException {

        switch (type) {
        case KEEP_FIRST_AND_GENERATE:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.KEEP_FIRST_AND_GENERATE_INT.getClazz());
            case 1:
                return getFunction(FunctionType.KEEP_FIRST_AND_GENERATE_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.KEEP_FIRST_AND_GENERATE_STRING.getClazz());
            default:
                return null;
            }
        case KEEP_LAST_AND_GENERATE:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.KEEP_LAST_AND_GENERATE_INT.getClazz());
            case 1:
                return getFunction(FunctionType.KEEP_LAST_AND_GENERATE_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.KEEP_LAST_AND_GENERATE_STRING.getClazz());
            default:
                return null;
            }
        case GENERATE_BETWEEN:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.GENERATE_BETWEEN_INT.getClazz());
            case 1:
                return getFunction(FunctionType.GENERATE_BETWEEN_LONG.getClazz());
            case 2:
                return getFunction(FunctionType.GENERATE_BETWEEN_FLOAT.getClazz());
            case 3:
                return getFunction(FunctionType.GENERATE_BETWEEN_DOUBLE.getClazz());
            case 4:
                return getFunction(FunctionType.GENERATE_BETWEEN_STRING.getClazz());
            case 5:
                return getFunction(FunctionType.GENERATE_BETWEEN_DATE.getClazz());
            default:
                return null;
            }
        case GENERATE_CREDIT_CARD_FORMAT:
            switch (javaType) {
            case 1:
                return getFunction(FunctionType.GENERATE_CREDIT_CARD_FORMAT_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING.getClazz());
            default:
                return null;
            }
        case GENERATE_CREDIT_CARD:
            switch (javaType) {
            case 1:
                return getFunction(FunctionType.GENERATE_CREDIT_CARD_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.GENERATE_CREDIT_CARD_STRING.getClazz());
            default:
                return null;
            }
        case GENERATE_FROM_FILE:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.GENERATE_FROM_FILE_INT.getClazz());
            case 1:
                return getFunction(FunctionType.GENERATE_FROM_FILE_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.GENERATE_FROM_FILE_STRING.getClazz());
            default:
                return null;
            }
        case GENERATE_FROM_LIST:
            switch (javaType) {
            case 0:
                return getFunction(FunctionType.GENERATE_FROM_LIST_INT.getClazz());
            case 1:
                return getFunction(FunctionType.GENERATE_FROM_LIST_LONG.getClazz());
            case 4:
                return getFunction(FunctionType.GENERATE_FROM_LIST_STRING.getClazz());
            default:
                return null;
            }
        default:
            return getFunction2(type, javaType);
        }
    }
}