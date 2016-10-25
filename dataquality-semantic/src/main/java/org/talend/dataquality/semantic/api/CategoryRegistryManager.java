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

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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

    private static boolean includeLocalCategoryRegistry = false;

    private static String localRegistryPath = System.getProperty("java.io.tmpdir") + File.separator
            + "org.talend.dataquality.semantic";

    public static final String CATEGORY_SUBFOLDER_NAME = "index/category";

    public static final String DICTIONARY_SUBFOLDER_NAME = "index/dictionary";

    public static final String KEYWORD_SUBFOLDER_NAME = "index/keyword";

    public static final String REGEX_SUBFOLDER_NAME = "regex";

    private static final String REGEX_CATEGRIZER_FILE_NAME = "categorizer.json";

    private Map<String, DQCategory> dqCategories = new LinkedHashMap<String, DQCategory>();

    private String contextName;

    private UserDefinedClassifier udc;

    private CategoryRegistryManager() {
        this("default");
    }

    private CategoryRegistryManager(String contextName) {
        this.contextName = contextName;

        loadBaseCategories();
        if (includeLocalCategoryRegistry) {
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
        localRegistryPath = folder;
        includeLocalCategoryRegistry = true;
    }

    public static String getLocalRegistryPath() {
        return localRegistryPath;
    }

    private void loadRegisteredCategories() throws IOException {
        // read local DD categories
        File categorySubFolder = new File(
                localRegistryPath + File.separator + CATEGORY_SUBFOLDER_NAME + File.separator + contextName);
        if (categorySubFolder.exists()) {
            final Directory indexDir = FSDirectory.open(categorySubFolder);
            final DirectoryReader reader = DirectoryReader.open(indexDir);

            for (int i = 0; i < reader.maxDoc(); i++) {
                Document doc = reader.document(i);
                DQCategory dqCat = new DQCategory();
                dqCat.setId(doc.getField("id").stringValue());
                dqCat.setName(doc.getField("name").stringValue());
                dqCat.setLabel(doc.getField("label").stringValue());
                dqCat.setType(CategoryType.valueOf(doc.getField("type").stringValue()));
                dqCat.setComplete(Boolean.valueOf(doc.getField("complete").stringValue()));
                dqCat.setDescription(doc.getField("description").stringValue());
                dqCategories.put(dqCat.getName(), dqCat);
            }
        } else {
            // persist all base categories
            FSDirectory outputDir = FSDirectory.open(categorySubFolder);
            IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer(CharArraySet.EMPTY_SET));
            IndexWriter writer = new IndexWriter(outputDir, writerConfig);

            for (DQCategory cat : dqCategories.values()) {
                Document doc = new Document();
                doc.add(new StringField("id", cat.getId(), Field.Store.YES));
                doc.add(new StringField("name", cat.getName(), Field.Store.YES));
                doc.add(new StringField("label", cat.getLabel(), Field.Store.YES));
                doc.add(new StringField("type", cat.getType().name(), Field.Store.YES));
                doc.add(new StringField("complete", String.valueOf(cat.isComplete()), Field.Store.YES));
                doc.add(new StringField("description", cat.getDescription(), Field.Store.YES));
                writer.addDocument(doc);
            }
            writer.commit();
            writer.close();
        }

        // read local RE categories
        File regexRegistryFolder = new File(localRegistryPath + File.separator + REGEX_SUBFOLDER_NAME);
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
        for (SemanticCategoryEnum cat : SemanticCategoryEnum.values()) {
            DQCategory dqCat = new DQCategory();
            dqCat.setId("base");
            dqCat.setName(cat.getId());
            dqCat.setLabel(cat.getDisplayName());
            dqCat.setDescription(cat.getDescription());

            switch (cat.getRecognizerType()) {
            case REGEX:
                dqCat.setType(CategoryType.RE);
                dqCat.setComplete(true);
                break;
            case OPEN_INDEX:
                dqCat.setType(CategoryType.DD);
                dqCat.setComplete(false);
                break;
            case CLOSED_INDEX:
                dqCat.setType(CategoryType.DD);
                dqCat.setComplete(true);
                break;
            case KEYWORD:
                dqCat.setType(CategoryType.KW);
                dqCat.setComplete(false);
                break;
            default:
                dqCat.setType(CategoryType.OT);
                dqCat.setComplete(false);
                break;
            }

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

    public DQCategory getCategoryMetadataByName(String name) {
        return dqCategories.get(name);
    }

    public UserDefinedClassifier getRegexClassifier() throws IOException {
        return getRegexClassifier(true);
    }

    public UserDefinedClassifier getRegexClassifier(boolean refresh) throws IOException {
        if (!includeLocalCategoryRegistry) {
            return UDCategorySerDeser.getRegexClassifier();
        } else {
            // load regexes from local registry
            if (udc == null || refresh) {
                final File regexRegistryFolder = new File(
                        localRegistryPath + File.separator + REGEX_SUBFOLDER_NAME + File.separator + REGEX_CATEGRIZER_FILE_NAME);
                final String content = new String(Files.readAllBytes(regexRegistryFolder.toPath()));
                JSONArray array = new JSONArray(content);
                JSONObject obj = new JSONObject();
                obj.put("classifiers", array);
                udc = UDCategorySerDeser.readJsonFile(obj.toString());
            }
        }
        return udc;
    }
}
