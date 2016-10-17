package sage.domain.commons

import sage.web.context.Json
import java.util.*


data class RichElement(val type: String = "", val value: String = "") {
  companion object {
    private val listType = Json.typeFactory().constructCollectionType(ArrayList::class.java, RichElement::class.java)

    fun fromJsonToList(json: String) = Json.`object`<List<RichElement>>(json, listType)
  }
}