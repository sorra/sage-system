package sage.web.context

import org.slf4j.LoggerFactory
import org.springframework.ui.ModelMap

class FrontMap constructor() : ModelMap() {

  fun attr(key: String, value: Any?): FrontMap {
    addAttribute(key, value)
    return this
  }

  /**
   * Use for rendering in template engine
   * @return JSON string
   */
  override fun toString(): String {
    logger.debug("Keys: " + this.keys)
    return Json.json(this)
  }

  companion object {
    val NAME = "frontMap"

    /**
     * Get the front map from model, create one if not exist
     * @param model
     * *
     * @return the front map
     */
    fun from(model: ModelMap): FrontMap {
      var fm: FrontMap? = model[NAME] as FrontMap?
      if (fm == null) {
        fm = FrontMap()
        model.addAttribute(NAME, fm)
      }
      return fm
    }

    private val logger = LoggerFactory.getLogger(FrontMap::class.java)
  }
}
