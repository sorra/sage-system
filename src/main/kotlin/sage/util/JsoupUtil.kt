package sage.util

import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

object JsoupUtil {
  // Though adding "#" and "/" has no effect
  val whitelist: Whitelist = Whitelist.relaxed().addProtocols("a", "href", "#", "/")
  // Allow style attribute for rich-text editor
  init {
    listOf("a", "b", "blockquote", "br", "caption", "cite", "code", "col",
    "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
    "i", "img", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong",
    "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u",
    "ul").forEach { tag ->
      whitelist.addAttributes(tag, "style")
    }
  }

  fun clean(html: String): String = Jsoup.clean(html, whitelist)
}