package sage.util

import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

object JsoupUtil {
  val whitelist = Whitelist.relaxed().addProtocols("a", "href", "#", "/") // Though adding "#" and "/" has no effect

  fun clean(html: String) = Jsoup.clean(html, whitelist)
}