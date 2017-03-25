package sage.domain.search

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.transport.NoNodeAvailableException
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.index.query.QueryBuilders.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sage.domain.commons.DomainException
import sage.transfer.BlogView
import sage.transfer.TweetView
import sage.web.context.Json
import java.net.InetAddress
import java.nio.file.Files
import java.nio.file.Paths
import javax.annotation.PreDestroy

@Component
class SearchBase {
  private val log = LoggerFactory.getLogger(javaClass)

  private var client: TransportClient = {
    val client = TransportClient.builder().build().addTransportAddresses(InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300))
    if (client.connectedNodes().isEmpty()) {
      log.error("Cannot connect to search server!")
      client.close()
    }
    client
  }()

  @PreDestroy
  internal fun shutdown() {
    client.close()
  }

  fun setupMappings() {
      val source = String(Files.readAllBytes(Paths.get(javaClass.classLoader.getResource("search/mappings.json")!!.toURI())))
    client.run {admin().indices().preparePutMapping(INDEX).setSource(source).setUpdateAllTypes(true).execute()}
  }

  /**
   * Only accepts transfer object

   * @param id
   * *          key
   * *
   * @param obj
   * *          a transfer object
   */
  fun index(id: Long, obj: Any) {
    try {
      val json = Json.json(obj)
      client.prepareIndex(INDEX, mapType(obj.javaClass), id.toString()).setSource(json).execute()
    } catch (e: NoNodeAvailableException) {
      log.error(e.toString())
    } catch (e: Exception) {
      log.error("", e)
    }
  }

  fun delete(clazz: Class<*>, id: Long) {
    try {
      client.prepareDelete().setIndex(INDEX).setType(mapType(clazz)).setId(id.toString()).execute()
    } catch (e: NoNodeAvailableException) {
      log.error(e.toString())
    } catch (e: Exception) {
      log.error("", e)
    }
  }

  fun search(query: String): SearchResponse {
    try {
      return client.prepareSearch(INDEX).setTypes(BLOG, TWEET)
          .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(queryStringQuery(query))
          .setFrom(0).setSize(60).setExplain(true)
          .execute().actionGet()
    } catch (e: NoNodeAvailableException) {
      log.error(e.toString())
      throw DomainException("搜索服务出问题了！")
    } catch (e: Exception) {
      log.error("", e)
      throw e
    }
  }

  companion object {
    val INDEX = "sage"
    val BLOG = "blog"
    val TWEET = "tweet"

    private val typeMap = mapOf(
        BlogView::class.java to BLOG,
        TweetView::class.java to TWEET)

    private fun mapType(clazz: Class<*>): String {
      val type = typeMap[clazz] ?: throw IllegalArgumentException(clazz.name + " is not indexed type!")
      return type
    }
  }
}
