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
package org.talend.dataquality.semantic.index;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class ESIndex implements Index {

    public static final String ES_DATADICT_INDEX = "tdq_semantic_dictionary";

    public static final String ES_DATADICT_TYPE = "dictionary";

    public static final String ES_KEYWORD_INDEX = "tdq_semantic_keyword";

    public static final String ES_KEYWORD_TYPE = "keyword";

    private static final int RESPONSE_SIZE = 10;

    private final String indexName;

    private final String typeName;

    protected final TransportClient client;

    public ESIndex(TransportClient client, String indexName, String indexType) {
        this.client = client;
        this.indexName = indexName;
        this.typeName = indexType;
    }

    public TransportClient getTransportClient() {
        return client;
    }

    public SearchRequestBuilder initRequest(int responseSize) {
        return client.prepareSearch(indexName).setTypes(typeName).setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(0)
                .setSize(responseSize);
    }

    public ClusterHealthResponse waitForYellowStatus() {
        ClusterHealthRequestBuilder healthRequest = client.admin().cluster().prepareHealth();
        healthRequest.setIndices(indexName);
        healthRequest.setWaitForYellowStatus();
        return healthRequest.execute().actionGet();
    }

    public SearchHits sendQuery(QueryBuilder queryBuilder, int responseSize) {
        SearchResponse response = initRequest(responseSize).setQuery(queryBuilder).execute().actionGet();
        return response.getHits();
    }

    public SearchHits sendQueryWithSort(QueryBuilder queryBuilder, int responseSize, String field, SortOrder order) {
        SearchResponse response = initRequest(responseSize).setQuery(queryBuilder) // Query
                .addSort(field, order).execute().actionGet();
        return response.getHits();
    }

    public SearchHits sendSimpleQuery(String field, String value, int responseSize) {
        QueryBuilder qb = QueryBuilders.queryStringQuery(value).field(field);
        return sendQuery(qb, responseSize);
    }

    public SearchHits sendTermQuery(String field, String value, int responseSize) {
        QueryBuilder qb = QueryBuilders.termQuery(field, value);
        return sendQuery(qb, responseSize);
    }

    public SearchHits sendFuzzyQuery(String field, String value, int responseSize) {
        QueryBuilder qb = QueryBuilders.fuzzyQuery(field, value);
        return sendQuery(qb, responseSize);
    }

    public SearchHits sendWildcardQuery(String field, String value, int responseSize) {
        QueryBuilder qb = QueryBuilders.wildcardQuery(field, "*" + value + "*");
        return sendQuery(qb, responseSize);
    }

    public boolean initIndex(boolean force) {
        boolean newIndexInitialized = false;
        if (isExistsIndex(indexName)) {
            if (force) {
                deleteIndex();
                createIndex();
                newIndexInitialized = true;
            } else {
                // do nothing
            }
        } else {
            createIndex();
            newIndexInitialized = true;
        }
        return newIndexInitialized;
    }

    public void initIndex() {
        initIndex(false);
    }

    @Override
    public void closeIndex() {
        client.close();
    }

    protected boolean isExistsIndex(String index) {
        ActionFuture<IndicesExistsResponse> exists = client.admin().indices().exists(new IndicesExistsRequest(index));
        IndicesExistsResponse actionGet = exists.actionGet();
        return actionGet.isExists();
    }

    private CreateIndexResponse createIndex() {
        XContentBuilder typeMapping = buildJsonMappings();
        return client.admin().indices().create(new CreateIndexRequest(indexName).mapping(typeName, typeMapping)).actionGet();
    }

    private CreateIndexResponse createIndex(String indexName, String typeName) {
        XContentBuilder typeMapping = buildJsonMappings();
        return client.admin().indices().create(new CreateIndexRequest(indexName).mapping(typeName, typeMapping)).actionGet();
    }

    public DeleteIndexResponse deleteIndex() {
        DeleteIndexResponse response = null;
        if (isExistsIndex(indexName)) {
            response = client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
        }
        return response;
    }

    private XContentBuilder buildJsonMappings() {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject().startObject("properties").startObject("ontology").field("type", "string").field("store", "yes")
                    .field("index", "no").endObject().endObject().endObject();
            return builder;
        } catch (IOException e) {
            throw new RuntimeException("Unable to build JSON Mappings.", e);
        }
    }

    public Set<String> findCategories(String data) {

        Set<String> foundCategorySet = new HashSet<String>();
        SearchHits hits = sendSimpleQuery("syn", data, RESPONSE_SIZE);
        if (hits.totalHits() > 0) {

            Iterator<SearchHit> it = hits.iterator();
            while (it.hasNext()) {
                SearchHit hit = it.next();

                Map<String, Object> fieldMap = hit.getSource();

                String category = (String) fieldMap.get("word");

                foundCategorySet.add(category);
            }

        }
        return foundCategorySet;

    }

    // handle with keywords in the index (dico2)
    public Set<String> searchKeyWordDictionary(String data) {

        Set<String> foundCategorySet = new HashSet<String>();
        SearchHits hits = sendSimpleQuery("syn", data, RESPONSE_SIZE);
        if (hits.totalHits() > 0) {

            Iterator<SearchHit> it = hits.iterator();
            while (it.hasNext()) {
                SearchHit hit = it.next();

                Map<String, Object> fieldMap = hit.getSource();

                String category = (String) fieldMap.get("word");

                foundCategorySet.add(category);
            }

        }

        return foundCategorySet;
    }

}
