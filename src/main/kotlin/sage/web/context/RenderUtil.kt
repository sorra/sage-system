package sage.web.context

import sage.domain.commons.ReformMention

object RenderUtil {
  @JvmStatic fun reformMention(text: String) = ReformMention.apply(text)
}