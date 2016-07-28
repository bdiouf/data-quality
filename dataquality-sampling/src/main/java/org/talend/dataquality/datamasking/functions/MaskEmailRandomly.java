package org.talend.dataquality.datamasking.functions;

import org.talend.dataquality.duplicating.RandomWrapper;

public abstract class MaskEmailRandomly extends MaskEmail {

    private static final long serialVersionUID = 1L;

    /**
     * DOC qzhao Comment method "choosePropriateDomainIndex".<br>
     * 
     * Chooses a appropriate index in the replacements where the item is different with the original input<br>
     * 
     * @param originalDomain
     * @return
     */
    protected int chooseAppropriateDomainIndex(String str) {
        int index;
        // This i allows to avoid infinite loop if the variable parameters contain only str substring
        // Randomly, we choose 50 iterations
        int i = 0;
        do {
            index = rnd.nextInt(parameters.length);
        } while (str.equals(parameters[index]) && i++ < 50);
        return index;
    }

    /**
     * Conditions in masking full email domain randomly:<br>
     * <ul>
     * <li>When user gives a space, masks the full domain with X</li>
     * <li>When user gives a list of parameters, chooses from the list randomly</li>
     * <li>When user gives a list of parameters with one or more space in the list, removes the spaces directly</li>
     * <li>when user gives a local file, gets the choices from the file</li>
     * </ul>
     */
    @Override
    protected String doGenerateMaskedField(String str) {
        if (str == null || str.isEmpty()) {
            return EMPTY_STRING;
        }
        int splitAddress = str.indexOf('@');
        if (isValidEmailAddress(str)) {
            // if (parameters.length == 1 && parameters[0].isEmpty())
            // return maskEmailRandomly(str, EMPTY_STRING, splitAddress);
            rnd = new RandomWrapper();
            return maskEmailRandomly(str, splitAddress);
        }
        return str;
    }

    protected abstract String maskEmailRandomly(String address, int splitAddress);

}
