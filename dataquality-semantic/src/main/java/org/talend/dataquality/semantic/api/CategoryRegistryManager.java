// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.CRC32;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
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
import org.talend.dataquality.semantic.index.PlatformPathUtil;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

/**
 * Singleton class providing API for local category registry management.
 * 
 * A local category registry is composed by the following subfolders:
 * <ul>
 * <li><b>category:</b> lucene index containing metadata of all categories</li>
 * <li><b>index/dictionary:</b> lucene index containing dictionary documents</li>
 * <li><b>index/keyword:</b> lucene index containing keyword documents</li>
 * <li><b>regex:</b> json file containing all categories that can be recognized by regex patterns and eventual subvalidators</li>
 * </ul>
 * In each of the above subfolders, there is still a level of subfolders representing different contexts. The default context name
 * is "default".
 */
public class CategoryRegistryManager {

    private static final Logger LOGGER = Logger.getLogger(CategoryRegistryManager.class);

    /**
     * Map between context names and corresponding instances.
     */
    private static final Map<String, CategoryRegistryManager> instances = new HashMap<>();

    /**
     * Whether the local category registry will be used.
     * Default value is false, which means only initial categories are loaded. This is mostly useful for unit tests.
     * More often, the value is set to true when the localRegistryPath is configured. see
     * {@link CategoryRegistryManager.setLocalRegistryPath()}
     */
    private static boolean usingLocalCategoryRegistry = false;

    private static String localRegistryPath = System.getProperty("user.home") + "/.talend/dataquality/semantic";

    public static final String CATEGORY_SUBFOLDER_NAME = "category";

    public static final String DICTIONARY_SUBFOLDER_NAME = "index/dictionary";

    public static final String KEYWORD_SUBFOLDER_NAME = "index/keyword";

    public static final String REGEX_SUBFOLDER_NAME = "regex";

    public static final String REGEX_CATEGRIZER_FILE_NAME = "categorizer.json";

    /**
     * Map between category ID and the object containing its metadata.
     */
    private Map<String, DQCategory> dqCategories = new LinkedHashMap<String, DQCategory>();

    private String contextName;

    private UserDefinedClassifier udc;

    private LocalDictionaryCache localDictionaryCache;

    private static final Object indexExtractionLock = new Object();

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
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static CategoryRegistryManager getInstance() {
        return getInstance("default");
    }

    public static synchronized CategoryRegistryManager getInstance(String contextName) {
        if (instances.get(contextName) == null) {
            instances.put(contextName, new CategoryRegistryManager(contextName));
        }
        return instances.get(contextName);
    }

    /**
     * Configure the local category registry path.
     * 
     * @param folder the folder to contain the category registry.
     */
    public static void setLocalRegistryPath(String folder) {
        if (folder != null && folder.trim().length() > 0) {
            localRegistryPath = folder;
            usingLocalCategoryRegistry = true;
            getInstance();
        } else {
            LOGGER.warn("Cannot set an empty path as local registy location. Use default one: " + localRegistryPath);
        }
    }

    /**
     * @return the path of local registry.
     */
    public static String getLocalRegistryPath() {
        return localRegistryPath;
    }

    /**
     * @return the {@link LocalDictionaryCache} corresponding to the current context.
     */
    public LocalDictionaryCache getDictionaryCache() {
        if (localDictionaryCache == null) {
            return new LocalDictionaryCache(contextName);
        }
        return localDictionaryCache;
    }

    /**
     * Reload the category from local registry. This method is typically called following category or dictionary enrichments.
     */
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
                    DQCategory dqCat = DictionaryUtils.categoryFromDocument(doc);
                    dqCategories.put(dqCat.getName(), dqCat);
                }
                reader.close();
                indexDir.close();
            } catch (IOException e) {
                LOGGER.error("Error while reloading categories from local registry.", e);
            }
        }
    }

    private void loadRegisteredCategories() throws IOException, URISyntaxException {
        // read local DD categories
        LOGGER.info("Loading categories from local registry.");
        final File categorySubFolder = new File(
                localRegistryPath + File.separator + CATEGORY_SUBFOLDER_NAME + File.separator + contextName);
        if (categorySubFolder.exists()) {
            try (final DirectoryReader reader = DirectoryReader.open(FSDirectory.open(categorySubFolder))) {
                for (int i = 0; i < reader.maxDoc(); i++) {
                    Document doc = reader.document(i);
                    DQCategory dqCat = DictionaryUtils.categoryFromDocument(doc);
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
                Document doc = DictionaryUtils.categoryToDocument(cat);
                writer.addDocument(doc);
            }
            writer.commit();
            writer.close();
        }

        // extract initial DD categories if not present
        final File dictionarySubFolder = new File(
                localRegistryPath + File.separator + DICTIONARY_SUBFOLDER_NAME + File.separator + contextName);
        loadBaseIndex(dictionarySubFolder, DICTIONARY_SUBFOLDER_NAME);

        // extract initial KW categories if not present
        final File keywordSubFolder = new File(
                localRegistryPath + File.separator + KEYWORD_SUBFOLDER_NAME + File.separator + contextName);
        loadBaseIndex(keywordSubFolder, KEYWORD_SUBFOLDER_NAME);

        // read local RE categories
        final File regexRegistryFolder = new File(
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

    private void loadBaseIndex(final File destSubFolder, String sourceSubFolder) throws IOException, URISyntaxException {
        if (!destSubFolder.exists()) {
            synchronized (indexExtractionLock) {
                final URI indexSourceURI = this.getClass().getResource("/" + sourceSubFolder).toURI();
                Path start = getFileSystemPath(indexSourceURI);
                destSubFolder.mkdirs();
                Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        Path destFilePath = Paths.get(destSubFolder.toString(), file.getFileName().toString());
                        Files.copy(file, destFilePath, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
                if ("jar".equals(indexSourceURI.getScheme())) {
                    try {
                        FileSystem fs = FileSystems.getFileSystem(indexSourceURI);
                        fs.close();
                    } catch (IOException e) {
                        LOGGER.error("Failed to close FileSystem for: " + indexSourceURI, e);
                    }
                }
            }
        }
    }

    private Path getFileSystemPath(URI uri) {
        LOGGER.info("Opening '" + uri + "' ...");
        if ("jar".equals(uri.getScheme())) {
            try {
                FileSystem fs = FileSystems.newFileSystem(uri, Collections.<String, String> emptyMap());
                final String directory = StringUtils.substringAfterLast(uri.toString(), "!"); //$NON-NLS-1$
                final Path path = fs.getPath(directory);
                return path;
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to open JAR '" + uri + "'.", e);
            }
        } else if ("file".equals(uri.getScheme())) {
            return Paths.get(uri);
        } else if ("bundleresource".equals(uri.getScheme())) { // for OSGI environment
            try {
                final String path = PlatformPathUtil.getFilePathByPlatformURL(uri.toURL());
                return Paths.get(path);
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to open bundleresource '" + uri + "'.", e);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported scheme '" + uri.getScheme() + "'.");
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

    /**
     * List all categories.
     * 
     * @return collection of category objects
     */
    public List<DQCategory> listCategories() {
        return new ArrayList<>(dqCategories.values());
    }

    /**
     * List all categories of a given {@link CategoryType}.
     * 
     * @param type the given category type
     * @return collection of category objects of the given type
     */
    public List<DQCategory> listCategories(CategoryType type) {
        List<DQCategory> catList = new ArrayList<DQCategory>();
        for (DQCategory dqCat : dqCategories.values()) {
            if (type.equals(dqCat.getType())) {
                catList.add(dqCat);
            }
        }
        return catList;
    }

    /**
     * Get the label of a category by its ID.
     * 
     * @param catId the category ID
     * @return the category label
     */
    public String getCategoryLabel(String catId) {
        if ("".equals(catId)) {
            return "";
        }
        return getCategoryMetadataByName(catId).getLabel();
    }

    /**
     * Get the category object by its ID.
     * 
     * @param catId the category ID
     * @return the category object
     */
    public DQCategory getCategoryMetadataByName(String catId) {
        return dqCategories.get(catId);
    }

    /**
     * get instance of UserDefinedClassifier
     */
    public UserDefinedClassifier getRegexClassifier() throws IOException {
        return getRegexClassifier(true);
    }

    /**
     * get instance of UserDefinedClassifier
     * 
     * @param refresh whether classifiers should be reloaded from local json file
     */
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

    /**
     * get URI of local dictionary
     */
    public URI getDictionaryURI() throws URISyntaxException {
        if (usingLocalCategoryRegistry) {
            return Paths.get(localRegistryPath, DICTIONARY_SUBFOLDER_NAME, contextName).toUri();
        } else {
            return CategoryRecognizerBuilder.class.getResource(CategoryRecognizerBuilder.DEFAULT_DD_PATH).toURI();
        }
    }
}
