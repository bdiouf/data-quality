package org.talend.dataquality.record.linkage.attribute;

import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;

public interface ITokenization {

    /**
     * @return set the type of tokenization when computing the measure
     */

    void setTokenMethod(TokenizedResolutionMethod tokenMethod);
}
