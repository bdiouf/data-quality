package org.talend.dataquality.datamasking.functions;

/**
 * Created by jteuladedenantes on 21/09/16.
 */
public class GenerateUniquePhoneNumberUs extends AbstractGenerateUniquePhoneNumber {

    private static final long serialVersionUID = 3821280930509201884L;

    @Override
    protected int getDigitsNumberToMask() {
        return 6;
    }
}
