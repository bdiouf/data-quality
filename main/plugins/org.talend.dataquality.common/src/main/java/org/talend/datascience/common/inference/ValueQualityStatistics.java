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
package org.talend.datascience.common.inference;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class ValueQualityStatistics implements Serializable {

    private static final long serialVersionUID = -4839915401904443142L;

    private final Set<String> invalidValues = new HashSet<>();

    private long validCount;

    private long emptyCount;

    private long invalidCount;

    public Set<String> getInvalidValues() {
        return invalidValues;
    }

    public long getValidCount() {
        return validCount;
    }

    public long getInvalidCount() {
        return invalidCount;
    }

    public long getCount() {
        return validCount + invalidCount + emptyCount;
    }

    public long getEmptyCount() {
        return emptyCount;
    }

    public void incrementEmpty() {
        emptyCount++;
    }

    public void incrementValid() {
        validCount++;
    }

    public void incrementInvalid() {
        invalidCount++;
    }

    public void appendInvalidValue(String value) {
        invalidValues.add(value);
    }

    public void setValidCount(long newCount) {
        validCount = newCount;
    }

    public void setInvalidCount(long newCount) {
        invalidCount = newCount;
    }

    public void setEmptyCount(long newCount) {
        emptyCount = newCount;
    }

    @Override
    public String toString() {
        return "ValueQuality{" + "valid=" + validCount + ", empty=" + emptyCount + ", invalid=" + invalidCount + '}'
                + "InvalidValues{" + invalidValues + "}";
    }
}
