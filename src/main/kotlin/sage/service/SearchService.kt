package sage.service

import org.apache.commons.io.IOUtils
import org.apache.http.HttpHost
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.Request
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.PutMappingRequest
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sage.transfer.BlogView
import sage.transfer.Searchable
import sage.transfer.TweetView
import sage.util.ClasspathUtil
import sage.util.Json
import javax.annotation.PreDestroy

/**
 * ElasticSearch client wrapper & index manager
 */
@Component
class SearchService {

  private val client: RestHighLevelClient = RestHighLevelClient(
      RestClient.builder(HttpHost("localhost", 9200, "http"))
  )

  @PreDestroy
  fun shutdown() {
    client.close()
  }

  fun setupIndex(indexName: String) {
    // Create the index if missing
    val findIndexResponse = client.lowLevelClient.performRequest(Request("HEAD", "/$indexName"))
    if (findIndexResponse.statusLine.statusCode == 404) {
      log.info("Create index {}", indexName)
      val request = CreateIndexRequest(indexName)
      client.indices().create(request, RequestOptions.DEFAULT)
    }

    // Put mapping
    val mapping = loadTextFile("search/mappings/$indexName.json")
    val putMappingRequest = PutMappingRequest(indexName).source(mapping, XContentType.JSON)
    client.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT)
  }

  private fun loadTextFile(fullName: String): String {
    return ClasspathUtil.classLoader().getResourceAsStream(fullName).use {
      IOUtils.toString(it, "UTF-8")
    }
  }

  fun dropIndex(indexName: String) {
    log.info("Drop index {}", indexName)
    client.indices().delete(DeleteIndexRequest(indexName), RequestOptions.DEFAULT)
  }

  /**
   * Only accepts DTO
   *
   * @param id key
   * @param obj DTO
   */
  fun index(indexName: String, id: Long, obj: Searchable) {
    val json = Json.json(obj)
    val request = IndexRequest(indexName)
        .id(id.toString())
        .source(json, XContentType.JSON)

    client.index(request, RequestOptions.DEFAULT)
  }

  fun delete(indexName: String, id: Long) {
    val request = DeleteRequest(indexName)
        .id(id.toString())

    client.delete(request, RequestOptions.DEFAULT)
  }

  fun search(indexName: String, query: String): SearchResponse {
    val searchSourceBuilder = SearchSourceBuilder()
        .query(QueryBuilders.simpleQueryStringQuery(query))
        .from(0).size(60)
        .explain(true)

    val request = SearchRequest(indexName)
        .searchType(SearchType.DFS_QUERY_THEN_FETCH)
        .source(searchSourceBuilder)

    return client.search(request, RequestOptions.DEFAULT)
  }

  companion object {
    const val BLOG = "blog"
    const val TWEET = "tweet"

    private val log = LoggerFactory.getLogger(SearchService::class.java)
  }
}
