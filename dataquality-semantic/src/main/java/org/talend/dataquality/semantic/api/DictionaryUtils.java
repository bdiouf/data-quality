package org.talend.dataquality.semantic.api;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.talend.dataquality.semantic.index.DictionarySearcher;

public class DictionaryUtils {

    private static FieldType ftSyn;

    static {
        ftSyn = new FieldType();
        ftSyn.setStored(false);
        ftSyn.setIndexed(true);
        ftSyn.setOmitNorms(true);
        ftSyn.freeze();
    }

    /**
     * generate a document.
     *
     * @param word
     * @param synonyms
     * @return
     */
    public static Document generateDocument(String id, String word, Set<String> synonyms) {
        String tempWord = word.trim();
        Document doc = new Document();

        if (id != null && id.trim().length() > 0) {
            Field idTermField = new StringField(DictionarySearcher.F_ID, id, Field.Store.NO);
            doc.add(idTermField);
        }
        Field wordTermField = new StringField(DictionarySearcher.F_WORD, tempWord, Field.Store.YES);
        doc.add(wordTermField);
        for (String syn : synonyms) {
            if (syn != null) {
                syn = syn.trim();
                if (syn.length() > 0 && !syn.equals(tempWord)) {
                    List<String> tokens = DictionarySearcher.getTokensFromAnalyzer(syn);
                    doc.add(new StringField(DictionarySearcher.F_SYNTERM, StringUtils.join(tokens, ' '), Field.Store.NO));
                    if (tokens.size() > 1) {
                        doc.add(new Field(DictionarySearcher.F_SYN, syn, ftSyn));
                    }
                }
            }
        }
        return doc;
    }
}
