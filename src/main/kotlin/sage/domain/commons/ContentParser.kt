package sage.domain.commons

import sage.entity.User
import sage.transfer.MidForward
import sage.util.SemiTemplate
import sage.util.Strings
import java.util.*


object ContentParser {
  fun tweet(content: String, userFinder: (String) -> User?): Pair<String, Set<Long>> {
    val contentEscaped = Strings.escapeHtmlTag(content)

    val (elements, mentionedIds) = MentionExtractor(userFinder).extract(contentEscaped)

    val allElements = elements.flatMap { elem ->
      if (elem.type == "") {
        SemiTemplate.match(elem.value, Links::match).flatMap { seg ->
          if (seg is SemiTemplate.Section<*>) listOf(Element("link", seg.data as String))
          else (seg as String).run {
            val idx1 = indexOf('#')
            if (idx1 < 0) {
              return@run listOf(Element("", this))
            }
            val idx2 = indexOf('#', idx1 + 1)
            if (idx2 < 0) {
              return@run listOf(Element("", this))
            }
            val subSegs = ArrayList<Element>(3)
            substring(0, idx1).apply {
              if (length > 0) subSegs.add(Element("", this))
            }
            subSegs.add(Element("emphasis", substring(idx1 + 1, idx2)))
            substring(idx2 + 1).apply {
              if (length > 0) subSegs.add(Element("", this))
            }
            return@run subSegs
          }
        }
      }
      else listOf(elem)
    }

    val rewritten = allElements.map { elem ->
      when (elem.type) {
        "mention" -> {
          val id = elem.value.substringBefore('\r')
          val name = elem.value.substringAfter('\r')
          "<a class=\"mention\" uid=\"$id\" href=\"/user/$id\">@$name</a> "
        }
        "link" -> "<a class=\"link\" href=\"${elem.value}\">${Strings.omit(elem.value, 50)}</a>"
        "emphasis" -> "<strong>${elem.value}</strong>"
        else -> elem.value
      }
    }.joinToString("")

    return Pair(rewritten, mentionedIds)
  }

  fun userLinkForMidForward(mf: MidForward) = "<a uid=\"${mf.authorId}\" href=\"/user/${mf.authorId}\">@${mf.authorName}</a> "
}