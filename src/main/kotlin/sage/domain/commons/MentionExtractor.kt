package sage.domain.commons

import sage.entity.User
import sage.util.SemiTemplate
import sage.util.SemiTemplate.Section
import java.util.*


class MentionExtractor(val userFinder: (String) -> User?) {
  fun extract(content: String): Pair<List<Element>, Set<Long>> {
    val semiSegs = SemiTemplate.match(content) { str, range ->
      val idxBegin = str.indexOf('@', range.begin)
      if (idxBegin < 0) {
        return@match null
      }
      val idxEnd = str.indexOf(' ', idxBegin)
      if (idxEnd < 0) {
        return@match null
      }
      val name = str.substring(idxBegin + 1, idxEnd)
      val user = userFinder(name)
      if (user != null) {
        return@match Section.f(Pair(user.id, name), idxBegin, idxEnd + 1)
      } else {
        return@match null
      }
    }

    val segments = semiSegs.mapNotNull { seg ->
      if (seg is Section<*>) {
        val (id, name) = seg.data as Pair<*, *>
        Element("mention", "$id\r$name")
      } else {
        val str = seg as String
        if (str.length > 0) Element("", str) else null
      }
    }
    val mentionedIds = semiSegs.mapNotNullTo(HashSet()) { seg ->
      if (seg is Section<*>) (seg.data as Pair<*, *>).first as Long
      else null
    }
    return Pair(segments, mentionedIds)
  }
}