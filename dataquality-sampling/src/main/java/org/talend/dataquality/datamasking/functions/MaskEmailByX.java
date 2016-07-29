package org.talend.dataquality.datamasking.functions;

import java.util.ArrayList;

public abstract class MaskEmailByX extends MaskEmail {

    private static final long serialVersionUID = 6433172298783284738L;

    /**
     * DOC qzhao Comment method "getPointPostions".<br>
     * Gets the points' postions in the email domain
     * 
     * @param address the original email address
     * @param count @'s position
     * @return a list of integer
     */
    protected ArrayList<Integer> getPointPostions(String address, int count) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int c = address.indexOf('.', count);
        while (c > 0) {
            list.add(c++);
            c = address.indexOf('.', c);
        }
        return list;
    }

    protected Character getMaskingCharacter() {
        String replacement = (parameters.length == 1) ? parameters[0] : null;
        return (replacement != null && replacement.length() == 1 && Character.isLetter(replacement.charAt(0)))
                ? replacement.charAt(0) : 'X';
    }
}
