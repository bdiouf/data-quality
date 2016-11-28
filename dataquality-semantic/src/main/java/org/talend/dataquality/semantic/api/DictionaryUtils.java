package org.talend.dataquality.semantic.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

public class DictionaryUtils {

    public static final FieldType FIELD_TYPE_SYN = new FieldType();

    public static final FieldType FIELD_TYPE_RAW_VALUE = new FieldType();

    static {
        FIELD_TYPE_SYN.setStored(false);
        FIELD_TYPE_SYN.setIndexed(true);
        FIELD_TYPE_SYN.setOmitNorms(true);
        FIELD_TYPE_SYN.freeze();

        FIELD_TYPE_RAW_VALUE.setIndexed(false);
        FIELD_TYPE_RAW_VALUE.setStored(true);
        FIELD_TYPE_RAW_VALUE.freeze();
    }

    /**
     * hide implicit public constructor
     */
    private DictionaryUtils() {
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
                    doc.add(new Field(DictionarySearcher.F_RAW, syn, FIELD_TYPE_RAW_VALUE));
                    if (tokens.size() > 1) {
                        doc.add(new Field(DictionarySearcher.F_SYN, syn, FIELD_TYPE_SYN));
                    }
                }
            }
        }
        return doc;
    }

    public static DQCategory categoryFromDocument(Document doc) {
        DQCategory dqCat = new DQCategory();
        dqCat.setId(doc.getField(DictionaryConstants.ID).stringValue());
        dqCat.setName(doc.getField(DictionaryConstants.NAME).stringValue());
        dqCat.setLabel(
                doc.getField(DictionaryConstants.LABEL) == null ? "" : doc.getField(DictionaryConstants.LABEL).stringValue());
        dqCat.setType(CategoryType.valueOf(doc.getField(DictionaryConstants.TYPE).stringValue()));
        dqCat.setCompleteness(Boolean.valueOf(doc.getField(DictionaryConstants.COMPLETENESS).stringValue()));
        dqCat.setDescription(doc.getField(DictionaryConstants.DESCRIPTION) == null ? ""
                : doc.getField(DictionaryConstants.DESCRIPTION).stringValue());
        return dqCat;
    }

    public static Document categoryToDocument(DQCategory cat) {
        Document doc = new Document();
        doc.add(new StringField(DictionaryConstants.ID, cat.getId(), Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.NAME, cat.getName(), Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.LABEL, cat.getLabel(), Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.TYPE, cat.getType().name(), Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.COMPLETENESS, String.valueOf(cat.isCompleteness()), Field.Store.YES));
        doc.add(new StringField(DictionaryConstants.DESCRIPTION, cat.getDescription(), Field.Store.YES));
        return doc;
    }

    public static DQDocument dictionaryEntryFromDocument(Document doc) {
        String catName = doc.getField(DictionarySearcher.F_WORD).stringValue();
        return dictionaryEntryFromDocument(doc, catName);
    }

    public static DQDocument dictionaryEntryFromDocument(Document doc, String knownCategoryName) {
        DQDocument dqCat = new DQDocument();
        // dqCat.setId(doc.getField(DictionarySearcher.F_ID).stringValue());
        dqCat.setCategory(CategoryRegistryManager.getInstance().getCategoryMetadataByName(knownCategoryName));
        IndexableField[] synTermFields = doc.getFields(DictionarySearcher.F_SYN);
        Set<String> synSet = new HashSet<String>();
        for (IndexableField f : synTermFields) {
            synSet.add(f.stringValue());
        }
        dqCat.setSynterm(synSet);
        return dqCat;
    }

}
