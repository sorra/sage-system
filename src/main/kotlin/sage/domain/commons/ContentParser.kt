package sage.domain.commons

import sage.entity.User
import sage.transfer.MidForward
import sage.util.SemiTemplate
import sage.util.StringUtil
import java.util.*


object ContentParser {
  fun tweet(content: String, userFinder: (String) -> User?): Pair<String, Set<Long>> {
    val contentEscaped = StringUtil.escapeHtmlTag(content)

    val (elements, mentionedIds) = MentionExtractor(userFinder).extract(contentEscaped)

    val allElements = elements.flatMap { elem ->
      if (elem.type == "") {
        SemiTemplate.match(elem.value, Links::match).flatMap { piece ->
          if (piece is SemiTemplate.Section<*>) {
            parseLink(piece)
          } else {
            parseText(piece as String)
          }
        }
      } else listOf(elem)
    }

    val rewritten = allElements.map { elem ->
      when (elem.type) {
        "mention" -> {
          val id = elem.value.substringBefore('\r')
          val name = elem.value.substringAfter('\r')
          "<a class=\"mention\" uid=\"$id\" href=\"/users/$id\">@$name</a> "
        }
        "link" -> "<a class=\"link\" href=\"${elem.value}\" target=\"_blank\" rel=\"noopener noreferrer\">" +
            StringUtil.omit(elem.value, 50) +
            "</a>"
        "emphasis" -> "<strong>${elem.value}</strong>"
        else -> elem.value
      }
    }.joinToString("")

    return Pair(rewritten, mentionedIds)
  }

  private fun parseLink(section: SemiTemplate.Section<*>): List<Element> {
    return listOf(Element("link", (section.data as String)))
  }

  private fun parseText(segment: String): List<Element> {
      val idx1 = segment.indexOf('#')
      if (idx1 < 0) {
        return listOf(segment.toTextElem())
      }

      val idx2 = segment.indexOf('#', idx1 + 1)
      if (idx2 < 0) {
        return listOf(segment.toTextElem())
      }

      val subSegs = ArrayList<Element>(3)
      segment.substring(0, idx1).let {
        if (it.isNotEmpty()) subSegs.add(Element("", it))
      }
      subSegs.add(Element("emphasis", segment.substring(idx1, idx2 + 1)))
      segment.substring(idx2 + 1).let {
        if (it.isNotEmpty()) subSegs.add(Element("", it))
      }

      return subSegs
  }

  fun comment(content: String, userFinder: (String) -> User?): Pair<String, Set<Long>> = tweet(content, userFinder)

  fun userLinkForMidForward(mf: MidForward) = "<a uid=\"${mf.authorId}\" href=\"/user/${mf.authorId}\">@${mf.authorName}</a> "

  private const val BR = "<br/>"

  private fun String.toTextElem() = Element("", toMultiLineText())

  private fun String.toMultiLineText(): String {
    var idx = indexOf('\n')
    if (idx < 0) {
      return this;
    }

    val sb = StringBuilder(substring(0, idx)).append(BR)
    idx++
    var hasBR = true

    while (idx < this.length) {
      val current = this[idx]

      if (current == '\n') {
        if (!hasBR) {
          sb.append(BR)
          hasBR = true
        }
      } else if (current.isWhitespace() && hasBR) {
        // Skip
      } else {
        sb.append(current)
        hasBR = false
      }

      idx++
    }

    return sb.toString()
  }
}
