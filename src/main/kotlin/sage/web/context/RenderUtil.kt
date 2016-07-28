package sage.web.context

import sage.domain.commons.ReformMention
import sage.util.Settings

object RenderUtil {
  val CDN = Settings.props.getProperty("cdn") ?: ""

  @JvmStatic fun cdn() = CDN

  @JvmStatic fun resLink(path: String) = "${CDN}${path}?version=${VersionsMapper.getVersion(path)}"

  @JvmStatic fun reformMention(text: String) = ReformMention.apply(text)
}