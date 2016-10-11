package sage.web.context

import sage.domain.commons.ContentParser
import sage.transfer.MidForward
import sage.util.Settings

object RenderUtil {
  val CDN: String = Settings.props.getProperty("cdn") ?: ""

  @JvmStatic fun cdn() = CDN

  @JvmStatic fun resLink(path: String) = CDN + VersionsMapper.getPath(path)

  @JvmStatic fun paginationLinks(uri: String, pagesCount: Int, currentPage: Int): String {
    val sb = StringBuilder()
    if (currentPage > 1) {
      sb.append("<a href=\"${uri}?page=${currentPage-1}\">上一页</a>")
    }
    for (i in 1..pagesCount) {
      val attrs =
          if (i == currentPage) "class=\"page-link current-page-link\""
          else "class=\"page-link\" href=\"${uri}?page=${i}\""
      sb.append('\n').append("<a ${attrs}>${i}</a>")
    }
    if (currentPage < pagesCount) {
      sb.append("\n<a href=\"${uri}?page=${currentPage+1}\">下一页</a>")
    }
    return sb.toString()
  }

  @JvmStatic fun userLinkForMidForward(mf: MidForward) = ContentParser.userLinkForMidForward(mf)
}