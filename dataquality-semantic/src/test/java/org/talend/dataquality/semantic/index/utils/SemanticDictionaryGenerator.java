package org.talend.dataquality.semantic.index.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.talend.dataquality.semantic.index.utils.optimizer.CategoryOptimizer;
import org.talend.dataquality.standardization.index.SynonymIndexBuilder;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

public class SemanticDictionaryGenerator {

    private static final String DD_PATH = "src/main/resources/luceneIdx/dictionary/";

    private static final String KW_PATH = "src/main/resources/luceneIdx/keyword/";

    private static final Logger log = Logger.getLogger(SemanticDictionaryGenerator.class);

    private static Pattern SPLITTER = Pattern.compile("\\|");

    private Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);

    private SynonymIndexBuilder builder = new SynonymIndexBuilder();

    private static Set<String> STOP_WORDS = new HashSet<String>(
            Arrays.asList("yes", "no", "y", "o", "n", "oui", "non", "true", "false", "vrai", "faux"));

    private void generateDictionaryForSpec(DictionaryGenerationSpec spec, IndexWriter writer) throws IOException {

        System.out.println("-------------------" + spec.name() + "---------------------");
        // load CSV file
        Reader reader = new FileReader(SemanticDictionaryGenerator.class.getResource(spec.getSourceFile()).getPath());
        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(spec.getCsvConfig().getDelimiter());
        if (spec.getCsvConfig().isWithHeader()) {
            csvFormat = csvFormat.withFirstRecordAsHeader();
        }

        // collect synonyms
        Set<String> synonymSet = new LinkedHashSet<String>();
        Iterable<CSVRecord> records = csvFormat.parse(reader);
        if (DictionaryGenerationSpec.CITY.equals(spec)) { // For CITY index, take all columns
            for (CSVRecord record : records) {
                for (int col = 0; col < record.size(); col++) {
                    String value = record.get(col);
                    String[] values = SPLITTER.split(value);
                    for (String syn : values) {
                        if (STOP_WORDS.contains(syn.toLowerCase())) {
                            continue;
                        }
                        synonymSet.add(syn);
                    }
                }
            }
        } else {
            synonymSet.addAll(getSynonymsForDocument(records, spec));
        }

        for (String syn : synonymSet) {
            writer.addDocument(generateDocument(spec.getCategoryName(), Collections.singleton(syn)));
        }
        System.out.println("Total document count: " + synonymSet.size() + "\n");

        reader.close();
    }

    private Set<String> getSynonymsForDocument(Iterable<CSVRecord> records, DictionaryGenerationSpec spec) {
        Set<String> results = new LinkedHashSet<String>();
        int[] columnsToIndex = spec.getColumnsToIndex();
        CategoryOptimizer optimizer = spec.getOptimizer();
        for (CSVRecord record : records) {
            final List<String> allInputColumns = new ArrayList<String>();
            for (int col : columnsToIndex) {
                if (col < record.size()) {
                    String value = record.get(col);
                    if (value != null && value.trim().length() > 0) {
                        String[] values = SPLITTER.split(value);
                        allInputColumns.addAll(Arrays.asList(values));
                    }
                }
            }

            if (optimizer != null) {
                results.addAll(optimizer.optimize(allInputColumns.toArray(new String[0])));
            } else {
                for (String syn : allInputColumns) {
                    if (STOP_WORDS.contains(syn.toLowerCase()) && DictionaryGenerationSpec.COMPANY.equals(spec)) {
                        continue;
                    }
                    results.add(syn);
                }
            }
        }
        return results;

    }

    /**
     * generate a document.
     *
     * @param word
     * @param synonyms
     * @return
     */
    private Document generateDocument(String word, Set<String> synonyms) {
        String tempWord = word.trim();
        Document doc = new Document();
        FieldType ftWord = new FieldType();
        ftWord.setStored(true);
        ftWord.setIndexed(false);
        ftWord.setOmitNorms(true);
        ftWord.freeze();
        FieldType ftSyn = new FieldType();
        ftSyn.setStored(false);
        ftSyn.setIndexed(true);
        ftSyn.setOmitNorms(true);
        ftSyn.freeze();

        Field wordField = new Field(SynonymIndexSearcher.F_WORD, tempWord, ftWord);
        doc.add(wordField);
        // Field wordTermField = new StringField(SynonymIndexSearcher.F_WORDTERM, tempWord.toLowerCase(),
        // Field.Store.NO);
        // doc.add(wordTermField);
        for (String syn : synonyms) {
            if (syn != null) {
                syn = syn.trim();
                if ("CITY".equals(tempWord)) { // ignore city abbreviations
                    if (syn.length() == 3 && syn.charAt(0) >= 'A' && syn.charAt(0) <= 'Z'//
                            && syn.charAt(1) >= 'A' && syn.charAt(1) <= 'Z'//
                            && syn.charAt(2) >= 'A' && syn.charAt(2) <= 'Z') {
                        continue;
                    }
                }

                if (syn.length() > 0 && !syn.equals(tempWord)) {
                    doc.add(new Field(SynonymIndexSearcher.F_SYN, syn, ftSyn));

                    try {
                        String joinedTokens = StringUtils.join(SynonymIndexSearcher.getTokensFromAnalyzer(syn), ' ');
                        doc.add(new StringField(SynonymIndexSearcher.F_SYNTERM, joinedTokens, Field.Store.YES));

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return doc;
    }

    private void generateAll() {
        try {
            builder.deleteIndexFromFS(DD_PATH);
            FSDirectory outputDir = FSDirectory.open(new File(DD_PATH));
            IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, analyzer);
            IndexWriter writer = new IndexWriter(outputDir, writerConfig);
            for (DictionaryGenerationSpec spec : DictionaryGenerationSpec.values()) {
                try {
                    generateDictionaryForSpec(spec, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            writer.commit();
            writer.close();
            outputDir.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    public static void main(String[] args) {
        new SemanticDictionaryGenerator().generateAll();
    }

}
