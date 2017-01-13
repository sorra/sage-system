package sage.service

import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.search.SearchBase
import sage.entity.Blog
import sage.entity.Tweet
import sage.transfer.BlogPreview
import sage.transfer.TweetView

@Service
class SearchService @Autowired constructor(private val searchBase: SearchBase) {
  private val log = LoggerFactory.getLogger(javaClass)

  final val reservedSyms = arrayOf("+", "-", "=", "&&", "||", ">", "<", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?", ":", "\\", "/")
  final val escapedSyms = reservedSyms.map { "\\" + it }.toTypedArray()

  fun setupMappings() = searchBase.setupMappings()

  fun index(id: Long, obj: Any) {
    searchBase.index(id, obj)
  }

  fun delete(clazz: Class<*>, id: Long) {
    searchBase.delete(clazz, id)
  }

  fun search(q: String): Pair<List<String>, List<Any>> {
    val query = StringUtils.replaceEach(q.toLowerCase(), reservedSyms, escapedSyms)
    val words = query.split(" ")

    val hits = searchBase.search(query).hits.hits.filter { hit ->
      val match = hit.sourceAsMap().any { entry ->
        (entry.key == "title" || entry.key == "content")
            && entry.value.toString().toLowerCase().indexOfAny(words) >= 0
      }
      if (!match) {
        log.info("Doc type={} id={} cannot match words={} in query={}", hit.type, hit.id, words, query)
      }
      match
    }
    @Suppress("IMPLICIT_CAST_TO_ANY")
    val results = hits.map { hit ->
      fun findById(f: (Long) -> Any?) = hit.sourceAsMap()["id"]?.run{ f(toString().toLong()) }
      when (hit.type) {
        SearchBase.BLOG -> findById {
          BlogPreview(Blog.get(it))
        }
        SearchBase.TWEET -> findById { id ->
          Tweet.byId(id)?.let { t ->
            TweetView(t, Tweet.getOrigin(t), false, {false})
          }
        }
        else -> null
      }
    }.filterNotNull()
    val types = results.map { it.javaClass.simpleName }
    return types to results
  }
}