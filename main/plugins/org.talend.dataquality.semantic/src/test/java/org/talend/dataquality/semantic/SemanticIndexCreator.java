// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.json.JSONObject;
import org.talend.dataquality.semantic.index.ESIndex;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

/**
 * Create DD and KW indices on ElasticSearch.
 */
public class SemanticIndexCreator {

    private static final Logger log = Logger.getLogger(SemanticIndexCreator.class);

    static final String indexPathKW = "/misc/repo-siq/tdq-siq/org.talend.creationIndex.resources/KW";

    static final String indexPathDD = "/misc/repo-siq/tdq-siq/org.talend.creationIndex.resources/DD";

    private static TransportClient client;

    private boolean useElasticSearch = false;

    public static TransportClient getTransportClient() {
        if (client == null) {
            Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();
            client = new TransportClient(settings);
            // client.addTransportAddress(new InetSocketTransportAddress("192.168.59.103", 9300));
            client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

        }
        return client;
    }

    public static void closeTransportClient() {
        if (client != null) {
            client.close();
        }
    }

    // Use standard analyzer without English stop words like "an", "was"
    private Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);

    // Default value points to an SVN working copy.
    // The provided indexes are located at "addons" folder of the studio.
    // private String inputPath = "/misc/repo/tdq/org.talend.dataquality.data.resources/data/synonym";//$NON-NLS-1$
    private String inputPath = "/path/to/studio/addons/data/synonym";//$NON-NLS-1$

    private String outputPath = "";

    public static final String F_WORD = "word";

    public static final String F_SYN = "syn";

    public static final String F_WORDTERM = "wordterm";

    public static final String F_SYNTERM = "synterm";

    /**
     * Sets the inputPath.
     *
     * @param inputPath the inputPath to set
     */
    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    /**
     * Sets the outputPath.
     *
     * @param outputPath the outputPath to set
     */
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * Deletes all files and sub-directories under a specified directory.
     *
     * @param dir
     * @return true if all deletions were successful
     */
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * prepare I/O folders and call regeneration process.
     *
     * @throws java.io.IOException
     */
    public int run(String targetIndexName, String targetIndexType) throws IOException {
        File inputFolder = new File(inputPath);
        log.info(inputFolder.getAbsolutePath());
        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            System.err.println("The input path <" + inputPath + "> does not exist or is not a folder.");
            System.err.println("Usage: java -jar IndexMigrator.jar <inputPath> <outputPath(optinal)>");
            return -1;
        }

        File outputFolder = new File(outputPath);
        if (useElasticSearch) {
            new ESIndex(getTransportClient(), targetIndexName, targetIndexType).initIndex();
        } else {
            if (inputFolder.equals(outputFolder)) {
                System.err.println("The I/O path should not be identical.");
                return -2;
            }
            log.info("Migrating all indexes in folder <" + inputPath + ">");
            if ("".equals(outputPath)) {
                log.info("No output folder specified. The new index(es) will be genenrated in <" + inputPath
                        + "_REGENERATED> folder");
                outputFolder = new File(inputPath + "_REGENERATED");
            } else {
                outputFolder = new File(outputPath);
            }
            if (outputFolder.exists() && outputFolder.isDirectory()) {
                log.info("The path <" + outputFolder + "> already exists.\nDeleting before migration...");
                deleteDir(outputFolder);
            }
        }
        return regenerate(inputFolder, outputFolder, targetIndexName, targetIndexType);
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
        ftWord.setIndexed(true);
        ftWord.setOmitNorms(true);
        ftWord.freeze();
        FieldType ftSyn = new FieldType();
        ftSyn.setStored(false);
        ftSyn.setIndexed(true);
        ftSyn.setOmitNorms(true);
        ftSyn.freeze();

        Field wordField = new Field(SynonymIndexSearcher.F_WORD, tempWord, ftWord);
        doc.add(wordField);
        Field wordTermField = new StringField(SynonymIndexSearcher.F_WORDTERM, tempWord.toLowerCase(), Field.Store.NO);
        doc.add(wordTermField);
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
                    doc.add(new StringField(SynonymIndexSearcher.F_SYNTERM, syn.toLowerCase(), Field.Store.NO));
                }
            }
        }
        return doc;
    }

    /**
     * regenerate all indexes recursively.
     *
     * @param inputFolder
     * @param outputFolder
     * @throws java.io.IOException
     */
    private int regenerate(File inputFolder, File outputFolder, String targetIndexName, String targetIndexType)
            throws IOException {
        FSDirectory indexDir = FSDirectory.open(inputFolder);
        CheckIndex check = new CheckIndex(indexDir);
        Status status = check.checkIndex();
        if (status.missingSegments) {
            for (File f : inputFolder.listFiles()) {
                if (f.isDirectory()) {
                    File out = new File(outputFolder.getAbsolutePath() + "/" + f.getName());
                    out.mkdir();
                    regenerate(f, out, targetIndexName, targetIndexType);
                }
            }
        } else {

            IndexReader reader = DirectoryReader.open(indexDir);
            // IndexSearcher searcher = new IndexSearcher(reader);

            if (useElasticSearch == false) {

                log.info("REGENERATE: " + inputFolder.getAbsoluteFile());
                FSDirectory outputDir = FSDirectory.open(outputFolder);
                IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, analyzer);
                IndexWriter writer = new IndexWriter(outputDir, writerConfig);
                // Collection<String> fieldNames = searcher.getIndexReader().FieldNames(FieldOption.ALL);
                // Collection<String> fieldNames = new ArrayList<>();

                Document newDoc = null;
                log.info("maxDoc: " + reader.maxDoc());
                for (int i = 0; i < reader.maxDoc(); i++) {
                    newDoc = reader.document(i);
                    IndexableField wordField = newDoc.getField("word");
                    IndexableField synField = newDoc.getField("syn");
                    String wordValue = wordField.stringValue();
                    IndexableField[] synFields = newDoc.getFields("syn");
                    Set<String> synValues = new HashSet<String>();
                    for (IndexableField f : synFields) {
                        synValues.add(f.stringValue());
                    }

                    newDoc.getValues("synterm");
                    if (wordField == null || synField == null) {
                        continue;
                    }

                    // if (!"CITY".equals(wordValue) && !"AIRPORT".equals(wordValue)) {
                    writer.addDocument(generateDocument(wordValue, synValues));
                    // }
                }
                writer.commit();
                writer.close();
                outputDir.close();

                // copy all other files such as "readMe.txt"
                // for (File file : inputFolder.listFiles()) {
                // if (file.isFile() && !isLuceneIndexFile(file)) {
                // // copy to destination folder
                // copyFile(file, outputFolder);
                // }
                // }
            } else {

                TransportClient client = getTransportClient();

                BulkRequestBuilder bulkRequest = client.prepareBulk();

                Set<String> categoryNameSet = new HashSet<String>();

                for (int i = 0; i < reader.maxDoc(); i++) {
                    Document doc = reader.document(i);
                    IndexableField wordField = doc.getField("word");
                    IndexableField synField = doc.getField("syn");
                    if (wordField == null || synField == null) {
                        // log.info("This DOC has Null field: " + wordField + " " + synField);
                        continue;
                    }

                    String wordValue = wordField.stringValue();
                    String synValue = synField.stringValue();

                    // if (!"CITY".equals(wordValue) && !"AIRPORT".equals(wordValue)) {

                    categoryNameSet.add(wordValue);

                    JSONObject jsonObj = new JSONObject();
                    jsonObj.accumulate("word", wordValue).accumulate("syn", synValue);
                    // log.info(jsonObj);

                    bulkRequest.add(client.prepareIndex(targetIndexName, targetIndexType).setSource(jsonObj.toString()));

                    // log.info(doc);
                    // }

                    // newDoc = searcher.doc(i);
                    // writer.addDocument(newDoc);

                    if (i % 10000 == 0 || i == reader.maxDoc() - 1) {
                        log.info(targetIndexName + " " + targetIndexType);
                        log.info("row count: " + i);
                        if (bulkRequest.numberOfActions() > 0) {
                            BulkResponse bulkResponse = bulkRequest.execute().actionGet();

                            if (bulkResponse.hasFailures()) {
                                log.info(bulkResponse.buildFailureMessage());
                            }
                            bulkRequest = client.prepareBulk();
                        } else {
                            // log.info("this bulk is empty");
                        }

                    }
                }

                for (String catName : categoryNameSet) {
                    log.info("- " + catName);
                }

            }

        }
        return 0;
    }

    public static void main(String[] args) throws IOException {
        // new ESIndex(getTransportClient(), "tdq_semantic_meta", "").deleteIndex();
        // new ESIndex(getTransportClient(), "tdq_semantic_dictionary", "").deleteIndex();
        // new ESIndex(getTransportClient(), "tdq_semantic_keyword", "").deleteIndex();

        SemanticIndexCreator migration = new SemanticIndexCreator();

        migration.setInputPath(indexPathDD);
        migration.run(ESIndex.ES_DATADICT_INDEX, ESIndex.ES_DATADICT_TYPE);

        migration.setInputPath(indexPathKW);
        migration.run(ESIndex.ES_KEYWORD_INDEX, ESIndex.ES_KEYWORD_TYPE);

        closeTransportClient();
    }

}
