package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jteuladedenantes on 21/09/16.
 */
public class GenerateUniquePhoneNumberUs extends AbstractGenerateUniquePhoneNumber {

    @Override
    protected int getDigitsNumberToMask() {
        return 6;
    }
}
