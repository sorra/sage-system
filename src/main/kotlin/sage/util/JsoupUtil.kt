package sage.util

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.safety.Cleaner
import org.jsoup.safety.Whitelist
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor

object JsoupUtil {
  // Though adding "#" and "/" has no effect
  val whitelist: Whitelist = Whitelist.relaxed().addProtocols("a", "href", "#", "/")
  // Allow style attribute for rich-text editor
  init {
    listOf("a", "b", "blockquote", "br", "caption", "cite", "code", "col",
    "colgroup", "dd", "del", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
    "i", "img", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong",
    "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u",
    "ul").forEach { tag ->
      whitelist.addAttributes(tag, "style")
    }
  }

  fun clean(html: String): String {
    val dirty = Jsoup.parseBodyFragment(html, "")
    val cleaner = Cleaner(whitelist)
    val clean = cleaner.clean(dirty)

    secureLinks(clean)

    return clean.body().html()
  }

  fun secureLinks(document: Document) {
    NodeTraversor(object : NodeVisitor {
      override fun head(node: Node, depth: Int) {
        if (node is Element && node.tagName() == "a") {
          node.attr("target", "_blank")
          node.attr("rel", "noopener noreferrer")
        }
      }

      override fun tail(node: Node, depth: Int) {}
    }).traverse(document)
  }
}