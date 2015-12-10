package org.talend.datascience.common.inference.type;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public enum DataTypeEnum {
    BOOLEAN,
    INTEGER,
    DOUBLE,
    STRING,
    DATE,
    TIME,
    EMPTY;

    public static DataTypeEnum get(String typeName) {
        if (BOOLEAN.name().equalsIgnoreCase(typeName)) {
            return BOOLEAN;
        } else if (INTEGER.name().equalsIgnoreCase(typeName)) {
            return INTEGER;
        } else if (DOUBLE.name().equalsIgnoreCase(typeName)) {
            return DOUBLE;
        } else if (STRING.name().equalsIgnoreCase(typeName)) {
            return STRING;
        } else if (DATE.name().equalsIgnoreCase(typeName)) {
            return DATE;
        }else if (TIME.name().equalsIgnoreCase(typeName)) {
            return TIME;
        } else if (EMPTY.name().equalsIgnoreCase(typeName)) {
            return EMPTY;
        }
        return STRING;
    }
}