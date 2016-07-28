package org.talend.dataquality.datamasking.functions;

public class MaskEmailLocalPartRandomly extends MaskEmailRandomly {

    /**
     * Replace local part email by the given replacement
     * 
     * @param address
     * @param replacement
     * @param count
     * @return
     */
    @Override
    protected String maskEmailRandomly(String address, int splitAddress) {
        return parameters[chooseAppropriateDomainIndex(address.substring(0, splitAddress))] + address.substring(splitAddress);
    }
}
