package org.talend.dataquality.statistics.type;

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
        try {
            return DataTypeEnum.valueOf(typeName.toUpperCase());
        } catch (Exception e) {
            return DataTypeEnum.STRING;
        }
    }
}
