package org.talend.dataquality.datamasking.functions;

public class MaskEmailLocalPartByX extends MaskEmailByX {

    private static final long serialVersionUID = -4581611905559460132L;

    /**
     * 
     * Masks the email local part by X
     * 
     * @param address
     * @return masked address
     */
    @Override
    protected String maskEmail(String address) {
        StringBuilder sb = new StringBuilder(address);
        int splitAddress = address.indexOf('@');
        Character maskingCrct = getMaskingCharacter();
        for (int i = 0; i < splitAddress; i++) {
            sb.setCharAt(i, maskingCrct);
        }
        return sb.toString();
    }
}
