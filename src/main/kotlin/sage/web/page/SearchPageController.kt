package sage.web.page

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import sage.service.SearchService
import sage.web.auth.Auth
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets

@Controller
open class SearchPageController @Autowired constructor(private val searchService: SearchService) {
  private val logger = LoggerFactory.getLogger(javaClass)

  @RequestMapping("/search")
  @Throws(UnsupportedEncodingException::class)
  open fun search(@RequestParam q: String, model: ModelMap): String {
    if (q.isEmpty()) {
      return "forward:/"
    }
    logger.info("/search uid=${Auth.uid()}, query=$q")
    val (types, results) = searchService.search(q)
    model.addAttribute("types", types).addAttribute("results", results)
    return "search-result"
  }
}
