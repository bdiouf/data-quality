package org.talend.dataquality.datamasking.functions;

public abstract class MaskEmailRandomly extends MaskEmail {

    private static final long serialVersionUID = 8651785081691625301L;

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
        // Arbitrarily, we choose 50 iterations
        int i = 0;
        do {
            index = rnd.nextInt(parameters.length);
        } while (str.equals(parameters[index]) && i++ < 50);
        return index;
    }
}
