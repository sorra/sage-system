package sage.domain.search

import org.apache.http.HttpHost
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.PutMappingRequest
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.stereotype.Component
import sage.transfer.BlogView
import sage.transfer.TweetView
import sage.util.ClasspathUtil
import sage.util.Json
import java.nio.file.Files
import java.nio.file.Paths
import javax.annotation.PreDestroy

/**
 * ElasticSearch client wrapper & index manager
 */
@Component
class SearchBase {

  private val client: RestHighLevelClient = RestHighLevelClient(
      RestClient.builder(HttpHost("localhost", 9200, "http"))
  )

  @PreDestroy
  fun shutdown() {
    client.close()
  }

  fun setupMappings() {
    typeMap.values
        .map { loadMappingFile(it) }
        .forEach {
          val request = PutMappingRequest(INDEX).source(it, XContentType.JSON)
          client.indices().putMapping(request, RequestOptions.DEFAULT)
        }
  }

  private fun loadMappingFile(type: String): String {
    val mappingFilePath = Paths.get(
        ClasspathUtil.classLoader().getResource("search/mappings/$type.json")!!.toURI())
    return String(Files.readAllBytes(mappingFilePath))
  }

  /**
   * Only accepts DTO
   *
   * @param id key
   * @param obj DTO
   */
  fun index(id: Long, obj: Any) {
    val json = Json.json(obj)
    val request = IndexRequest(INDEX)
        .type(mapType(obj.javaClass))
        .id(id.toString())
        .source(json, XContentType.JSON)

    client.index(request, RequestOptions.DEFAULT)
  }

  fun delete(clazz: Class<*>, id: Long) {
    val request = DeleteRequest(INDEX)
        .type(mapType(clazz))
        .id(id.toString())

    client.delete(request, RequestOptions.DEFAULT)
  }

  fun search(query: String): SearchResponse {
    val searchSourceBuilder = SearchSourceBuilder()
        .query(QueryBuilders.queryStringQuery(query))
        .from(0).size(60)
        .explain(true)

    val request = SearchRequest(INDEX)
        .searchType(SearchType.DFS_QUERY_THEN_FETCH)
        .source(searchSourceBuilder)

    return client.search(request, RequestOptions.DEFAULT)
  }

  companion object {
    const val INDEX = "sage"
    const val BLOG = "blog"
    const val TWEET = "tweet"

    private val typeMap = mapOf(
        BlogView::class.java to BLOG,
        TweetView::class.java to TWEET)

    private fun mapType(clazz: Class<*>): String {
      return typeMap[clazz] ?: throw IllegalArgumentException(clazz.name + " is not an indexed type!")
    }
  }
}
