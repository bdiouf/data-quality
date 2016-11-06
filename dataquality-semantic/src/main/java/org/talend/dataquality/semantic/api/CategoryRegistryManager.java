package org.talend.dataquality.semantic.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.JSONArray;
import org.json.JSONObject;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.custom.UDCategorySerDeser;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;

public class CategoryRegistryManager {

    private static final Logger LOGGER = Logger.getLogger(CategoryRegistryManager.class);

    private static final Map<String, CategoryRegistryManager> instances = new HashMap<>();

    private static boolean usingLocalCategoryRegistry = false;

    private static String localRegistryPath = System.getProperty("user.home") + "/.talend/dataquality/semantic";

    public static final String CATEGORY_SUBFOLDER_NAME = "category";

    public static final String DICTIONARY_SUBFOLDER_NAME = "index/dictionary";

    public static final String KEYWORD_SUBFOLDER_NAME = "index/keyword";

    public static final String REGEX_SUBFOLDER_NAME = "regex";

    public static final String REGEX_CATEGRIZER_FILE_NAME = "categorizer.json";

    private Map<String, DQCategory> dqCategories = new LinkedHashMap<String, DQCategory>();

    private String contextName;

    private UserDefinedClassifier udc;

    private CategoryRegistryManager() {
        this("default");
    }

    private CategoryRegistryManager(String contextName) {
        this.contextName = contextName;

        loadBaseCategories();
        if (usingLocalCategoryRegistry) {
            try {
                loadRegisteredCategories();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static CategoryRegistryManager getInstance() {
        return getInstance("default");
    }

    public static CategoryRegistryManager getInstance(String contextName) {
        if (instances.get(contextName) == null) {
            instances.put(contextName, new CategoryRegistryManager(contextName));
        }
        return instances.get(contextName);
    }

    public static void setLocalRegistryPath(String folder) {
        if (folder != null && folder.trim().length() > 0) {
            localRegistryPath = folder;
            usingLocalCategoryRegistry = true;
            getInstance();
        } else {
            LOGGER.warn("Cannot set an empty path as local registy location. Use default one: " + localRegistryPath);
        }
    }

    public static String getLocalRegistryPath() {
        return localRegistryPath;
    }

    public void reloadCategoriesFromRegistry() {
        LOGGER.info("Reload categories from local registry.");
        File categorySubFolder = new File(
                localRegistryPath + File.separator + CATEGORY_SUBFOLDER_NAME + File.separator + contextName);
        if (categorySubFolder.exists()) {
            dqCategories.clear();
            try {
                final Directory indexDir = FSDirectory.open(categorySubFolder);
                final DirectoryReader reader = DirectoryReader.open(indexDir);

                for (int i = 0; i < reader.maxDoc(); i++) {
                    Document doc = reader.document(i);
                    DQCategory dqCat = new DQCategory();
                    dqCat.setId(doc.getField(DictionaryConstants.ID).stringValue());
                    dqCat.setName(doc.getField(DictionaryConstants.NAME).stringValue());
                    dqCat.setLabel(doc.getField(DictionaryConstants.LABEL) == null ? ""
                            : doc.getField(DictionaryConstants.LABEL).stringValue());
                    dqCat.setType(CategoryType.valueOf(doc.getField(DictionaryConstants.TYPE).stringValue()));
                    dqCat.setCompleteness(Boolean.valueOf(doc.getField(DictionaryConstants.COMPLETENESS).stringValue()));
                    dqCat.setDescription(doc.getField(DictionaryConstants.DESCRIPTION) == null ? ""
                            : doc.getField(DictionaryConstants.DESCRIPTION).stringValue());
                    dqCategories.put(dqCat.getName(), dqCat);
                }
            } catch (IOException e) {
                LOGGER.error("Error while reloading categories from local registry.", e);
            }
        }
    }

    private void loadRegisteredCategories() throws IOException {
        // read local DD categories
        LOGGER.info("Loading categories from local registry.");
        File categorySubFolder = new File(
                localRegistryPath + File.separator + CATEGORY_SUBFOLDER_NAME + File.separator + contextName);
        if (categorySubFolder.exists()) {
            try (final DirectoryReader reader = DirectoryReader.open(FSDirectory.open(categorySubFolder))) {
                for (int i = 0; i < reader.maxDoc(); i++) {
                    Document doc = reader.document(i);
                    IndexableField idField = doc.getField(DictionaryConstants.ID);
                    IndexableField nameField = doc.getField(DictionaryConstants.NAME);
                    IndexableField labelField = doc.getField(DictionaryConstants.LABEL);
                    IndexableField typeField = doc.getField(DictionaryConstants.TYPE);
                    IndexableField completenessField = doc.getField(DictionaryConstants.COMPLETENESS);
                    IndexableField descriptionField = doc.getField(DictionaryConstants.DESCRIPTION);

                    DQCategory dqCat = new DQCategory();
                    dqCat.setId(idField.stringValue());
                    dqCat.setName(nameField.stringValue());
                    String label = labelField == null ? null : labelField.stringValue();
                    dqCat.setLabel(StringUtils.isBlank(label) ? dqCat.getName() : label);
                    dqCat.setType(typeField == null ? null : CategoryType.valueOf(typeField.stringValue()));
                    dqCat.setCompleteness(completenessField == null ? false : Boolean.valueOf(completenessField.stringValue()));
                    dqCat.setDescription(descriptionField == null ? null : descriptionField.stringValue());
                    dqCategories.put(dqCat.getName(), dqCat);
                }
            }
        } else {
            // persist all base categories
            LOGGER.info("Local category registry is not found, initialize it with base categories.");
            FSDirectory outputDir = FSDirectory.open(categorySubFolder);
            IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer(CharArraySet.EMPTY_SET));
            IndexWriter writer = new IndexWriter(outputDir, writerConfig);

            for (DQCategory cat : dqCategories.values()) {
                Document doc = new Document();
                doc.add(new StringField(DictionaryConstants.ID, cat.getId(), Field.Store.YES));
                doc.add(new StringField(DictionaryConstants.NAME, cat.getName(), Field.Store.YES));
                doc.add(new StringField(DictionaryConstants.LABEL, cat.getLabel(), Field.Store.YES));
                doc.add(new StringField(DictionaryConstants.TYPE, cat.getType().name(), Field.Store.YES));
                doc.add(new StringField(DictionaryConstants.COMPLETENESS, String.valueOf(cat.isCompleteness()), Field.Store.YES));
                doc.add(new StringField(DictionaryConstants.DESCRIPTION, cat.getDescription(), Field.Store.YES));
                writer.addDocument(doc);
            }
            writer.commit();
            writer.close();
        }

        // read local RE categories
        File regexRegistryFolder = new File(
                localRegistryPath + File.separator + REGEX_SUBFOLDER_NAME + File.separator + contextName);
        if (!regexRegistryFolder.exists()) {
            // load provided RE into registry
            InputStream is = CategoryRecognizer.class.getResourceAsStream(REGEX_CATEGRIZER_FILE_NAME);
            StringBuilder sb = new StringBuilder();
            for (String line : IOUtils.readLines(is)) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            JSONArray array = obj.getJSONArray("classifiers");
            regexRegistryFolder.mkdirs();
            FileOutputStream fos = new FileOutputStream(regexRegistryFolder + File.separator + REGEX_CATEGRIZER_FILE_NAME);
            IOUtils.write(array.toString(2), fos);
            fos.close();
        }
    }

    private void loadBaseCategories() {
        LOGGER.info("Loading base categories.");
        for (SemanticCategoryEnum cat : SemanticCategoryEnum.values()) {
            DQCategory dqCat = new DQCategory();

            CRC32 checksum = new CRC32();
            checksum.update(cat.getId().getBytes(), 0, cat.getId().getBytes().length);
            final String hash = Long.toHexString(checksum.getValue());
            dqCat.setId(hash);
            dqCat.setName(cat.getId());
            dqCat.setLabel(cat.getDisplayName());
            dqCat.setDescription(cat.getDescription());
            dqCat.setType(cat.getCategoryType());
            dqCat.setCompleteness(cat.getCompleteness());

            dqCategories.put(cat.getId(), dqCat);
        }
    }

    public Collection<DQCategory> listCategories() {
        return dqCategories.values();

    }

    public List<DQCategory> listCategories(CategoryType type) {
        List<DQCategory> catList = new ArrayList<DQCategory>();
        for (DQCategory dqCat : dqCategories.values()) {
            if (type.equals(dqCat.getType())) {
                catList.add(dqCat);
            }
        }
        return catList;
    }

    public String getCategoryLabel(String catId) {
        if ("".equals(catId)) {
            return "";
        }
        return getCategoryMetadataByName(catId).getLabel();
    }

    public DQCategory getCategoryMetadataByName(String name) {
        return dqCategories.get(name);
    }

    public UserDefinedClassifier getRegexClassifier() throws IOException {
        return getRegexClassifier(true);
    }

    public UserDefinedClassifier getRegexClassifier(boolean refresh) throws IOException {
        if (!usingLocalCategoryRegistry) {
            return UDCategorySerDeser.getRegexClassifier();
        } else {
            // load regexes from local registry
            if (udc == null || refresh) {
                final File regexRegistryFile = new File(localRegistryPath + File.separator + REGEX_SUBFOLDER_NAME + File.separator
                        + contextName + File.separator + REGEX_CATEGRIZER_FILE_NAME);

                if (!regexRegistryFile.exists()) {
                    regexRegistryFile.getParentFile().mkdirs();
                    // load provided RE into registry
                    InputStream is = CategoryRecognizer.class.getResourceAsStream(REGEX_CATEGRIZER_FILE_NAME);
                    StringBuilder sb = new StringBuilder();
                    for (String line : IOUtils.readLines(is)) {
                        sb.append(line);
                    }
                    JSONObject obj = new JSONObject(sb.toString());
                    JSONArray array = obj.getJSONArray("classifiers");
                    FileOutputStream fos = new FileOutputStream(regexRegistryFile);
                    IOUtils.write(array.toString(2), fos);
                    fos.close();
                }

                final String content = new String(Files.readAllBytes(regexRegistryFile.toPath()));
                JSONArray array = new JSONArray(content);
                JSONObject obj = new JSONObject();
                obj.put("classifiers", array);
                udc = UDCategorySerDeser.readJsonFile(obj.toString());
            }
        }
        return udc;
    }
}
