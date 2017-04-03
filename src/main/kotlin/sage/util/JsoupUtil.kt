package sage.util

import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

object JsoupUtil {
  // Though adding "#" and "/" has no effect
  val whitelist: Whitelist = Whitelist.relaxed().addProtocols("a", "href", "#", "/")

  fun clean(html: String): String = Jsoup.clean(html, whitelist)
}