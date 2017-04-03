package sage.util

import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

object MarkdownUtil {
  fun render(text: String): String {
    val extensions = listOf(AutolinkExtension.create(), StrikethroughExtension.create(), TablesExtension.create(), HeadingAnchorExtension.create())
    val parser = Parser.builder().extensions(extensions).build()
    val renderer = HtmlRenderer.builder().extensions(extensions).build()

    return renderer.render(parser.parse(text))
  }
}