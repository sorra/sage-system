package sage.web.page

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import sage.domain.search.SearchBase
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.util.*

@Controller
open class SearchPageController {
  private val logger = LoggerFactory.getLogger(javaClass)
  @Autowired
  private val searchBase: SearchBase? = null

  @RequestMapping("/search")
  @Throws(UnsupportedEncodingException::class)
  open fun search(@RequestParam q: String, model: ModelMap): String {
    var q = q
    if (q.isEmpty()) {
      return "forward:"
    }
    q = String(q.toByteArray(charset("ISO-8859-1")), StandardCharsets.UTF_8)
    logger.info("query: " + q)
    val hits = searchBase!!.search(q).hits.hits

    val sources = ArrayList<String>()
    for (hit in hits) {
      logger.info("~hit~ id:{} type:{}", hit.id(), hit.type())
      //      if (hit.sourceAsMap().values().toString().toLowerCase().contains(q.toLowerCase())) {
      //        jsons.add(hit.sourceAsString());
      //      }
      sources.add(hit.sourceAsString())
    }
    model.addAttribute("hits", sources)
    return "search-result"
  }
}
