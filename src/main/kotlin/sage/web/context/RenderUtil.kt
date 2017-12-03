package sage.web.context

import sage.domain.commons.ContentParser
import sage.transfer.MidForward
import sage.util.Settings

object RenderUtil {
  private val CDN: String = Settings.props.getProperty("cdn") ?: ""

  @JvmStatic fun cdn() = CDN

  @JvmStatic fun resLink(path: String) = CDN + VersionsMapper.getPath(path)

  @JvmStatic fun paginationLinks(uri: String, pagesCount: Int, curPageIndex: Int): String {
    val sb = StringBuilder()
    if (curPageIndex > 1) {
      sb.append("<a href=\"${uri}?pageIndex=${curPageIndex -1}\">上一页</a>")
    }
    for (i in 1..pagesCount) {
      val attrs =
          if (i == curPageIndex) "class=\"page-link current-page-link\""
          else "class=\"page-link\" href=\"${uri}?pageIndex=${i}\""
      sb.append('\n').append("<a ${attrs}>${i}</a>")
    }
    if (curPageIndex < pagesCount) {
      sb.append("\n<a href=\"${uri}?pageIndex=${curPageIndex +1}\">下一页</a>")
    }
    return sb.toString()
  }

  @JvmStatic fun userLinkForMidForward(mf: MidForward) = ContentParser.userLinkForMidForward(mf)
}