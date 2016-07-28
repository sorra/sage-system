package sage.web.context

import sage.domain.commons.ReformMention
import sage.util.Settings

object RenderUtil {
  val CDN: String = Settings.props.getProperty("cdn") ?: ""

  @JvmStatic fun cdn() = CDN

  @JvmStatic fun resLink(path: String) = CDN + VersionsMapper.getPath(path)

  @JvmStatic fun reformMention(text: String) = ReformMention.apply(text)
}