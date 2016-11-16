package org.talend.dataquality.semantic.index;

public enum DictionarySearchMode {

    MATCH_SEMANTIC_DICTIONARY("MATCH_SEMANTIC_DICTIONARY"), // Used only for searching semantic dictionary
    MATCH_SEMANTIC_KEYWORD("MATCH_SEMANTIC_KEYWORD");// Used only for searching semantic keyword

    private String label;

    DictionarySearchMode(String label) {
        this.label = label;
    }

    private String getLabel() {
        return label;
    }

    /**
     * Method "get".
     *
     * @param label the label of the match mode
     * @return the match mode type given the label or null
     */
    public static DictionarySearchMode get(String label) {
        for (DictionarySearchMode type : DictionarySearchMode.values()) {
            if (type.getLabel().equalsIgnoreCase(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid search mode: " + label);
    }
}