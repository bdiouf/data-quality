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
package org.talend.dataquality.semantic.recognizer;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.index.DictionarySearchMode;
import org.talend.dataquality.semantic.index.LuceneIndex;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class CategoryRecognizerBuilder implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CategoryRecognizerBuilder.class);

    private static final long serialVersionUID = -4113525921010790756L;

    private static CategoryRecognizerBuilder INSTANCE;

    public static final String DEFAULT_DD_PATH = "/index/dictionary/";

    public static final String DEFAULT_KW_PATH = "/index/keyword/";

    private Mode mode;

    private URI ddPath;

    private URI kwPath;

    private LuceneIndex dataDictIndex;

    private LuceneIndex keywordIndex;

    private Directory ddDirectory;

    private Directory kwDirectory;

    public static CategoryRecognizerBuilder newBuilder() {
        if (INSTANCE == null) {
            INSTANCE = new CategoryRecognizerBuilder();
        }
        return INSTANCE;
    }

    public CategoryRecognizerBuilder ddPath(URI ddPath) {
        this.ddPath = ddPath;
        return this;
    }

    public CategoryRecognizerBuilder ddDirectory(Directory ddDirectory) {
        this.ddDirectory = ddDirectory;
        return this;
    }

    public CategoryRecognizerBuilder kwPath(URI kwPath) {
        this.kwPath = kwPath;
        return this;
    }

    public CategoryRecognizerBuilder kwDirectory(Directory kwDirectory) {
        this.kwDirectory = kwDirectory;
        return this;
    }

    public CategoryRecognizerBuilder lucene() {
        this.mode = Mode.LUCENE;
        return this;
    }

    public CategoryRecognizer build() throws IOException {

        switch (mode) {
        case LUCENE:
            LuceneIndex dict = getDataDictIndex();
            LuceneIndex keyword = getKeywordIndex();
            return new DefaultCategoryRecognizer(dict, keyword);
        case ELASTIC_SEARCH:
            throw new IllegalArgumentException("Elasticsearch mode is not supported any more");
        default:
            throw new IllegalArgumentException("no mode specified.");
        }

    }

    private LuceneIndex getDataDictIndex() {
        if (dataDictIndex == null) {
            if (ddDirectory == null) {
                if (ddPath == null) {
                    try {
                        ddPath = CategoryRecognizerBuilder.class.getResource(DEFAULT_DD_PATH).toURI();
                    } catch (URISyntaxException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                dataDictIndex = new LuceneIndex(ddPath, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
            } else {
                if (ddPath == null) {
                    dataDictIndex = new LuceneIndex(ddDirectory, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
                } else {
                    throw new IllegalArgumentException("Please call either ddDirectory() or ddPath() but not both!");
                }
            }
        }
        return dataDictIndex;
    }

    private LuceneIndex getKeywordIndex() {
        if (keywordIndex == null) {
            if (kwDirectory == null) {
                if (kwPath == null) {
                    try {
                        kwPath = CategoryRecognizerBuilder.class.getResource(DEFAULT_KW_PATH).toURI();
                    } catch (URISyntaxException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                keywordIndex = new LuceneIndex(kwPath, DictionarySearchMode.MATCH_SEMANTIC_KEYWORD);
            } else {
                if (kwPath == null) {
                    keywordIndex = new LuceneIndex(kwDirectory, DictionarySearchMode.MATCH_SEMANTIC_DICTIONARY);
                } else {
                    throw new IllegalArgumentException("Please call either kwDirectory() or kwPath() but not both!");
                }
            }
        }
        return keywordIndex;
    }

    public enum Mode {
        LUCENE,
        ELASTIC_SEARCH
    }

    public Mode getMode() {
        return mode;
    }

    public URI getDDPath() {
        return ddPath;
    }

    public URI getKWPath() {
        return kwPath;
    }

    public void initIndex() {
        if (dataDictIndex != null) {
            dataDictIndex.initIndex();
        }
        if (keywordIndex != null) {
            keywordIndex.initIndex();
        }
    }

}
