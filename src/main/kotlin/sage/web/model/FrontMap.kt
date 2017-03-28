package sage.web.model

import org.slf4j.LoggerFactory
import org.springframework.ui.ModelMap
import sage.web.context.Json
import javax.servlet.http.HttpServletRequest

class FrontMap constructor() : ModelMap() {

  fun attr(key: String, value: Any?): FrontMap {
    addAttribute(key, value)
    return this
  }

  /**
   * Used for rendering in template engine
   * @return JSON string
   */
  override fun toString(): String {
    if (logger.isDebugEnabled) {
      logger.debug("Keys: " + this.keys)
    }
    return Json.json(this)
  }

  companion object {
    val NAME = "frontMap"

    fun from(request: HttpServletRequest): FrontMap {
      var fm = request.getAttribute(NAME) as FrontMap?
      if (fm == null) {
        fm = FrontMap()
        request.setAttribute(NAME, fm)
      }
      return fm
    }

    private val logger = LoggerFactory.getLogger(FrontMap::class.java)
  }
}
