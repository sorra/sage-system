package sage.domain.intelligence

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sage.entity.Tag
import java.util.*

class TagAnalyzer(val text: String, val presentTagIds: Collection<Long>, val topTagIds: Collection<Long>) {
  // If presentTagIds are not enough, fill with more by analysis
  fun analyze(): Collection<Long> {
    try {
      val numLimit = 3 - presentTagIds.size
      return if (numLimit > 0) {
        (presentTagIds + pickTags(numLimit).map(Tag::id)).toSet()
      } else {
        presentTagIds
      }
    } catch (e: Throwable) {
      log.error("Fail to analyze tags: ", e)
      return presentTagIds
    }
  }

  // Pick tags no more than the numLimit
  private fun pickTags(numLimit: Int): List<Tag> {
    // Find matches and score them
    val matches = Tag.all().asSequence().filter { tag ->
      tag.id != Tag.ROOT_ID
    }.mapNotNull { tag ->
      val occurCount = StringUtils.countMatches(text, tag.name)
      if (occurCount == 0) {
        null
      } else {
        val depth = tag.chainUp().size
        val bonus = if (topTagIds.contains(tag.id)) 1 else 0
        TagWithScore(tag, occurCount + depth + bonus)
      }
    }.toList()

    // Find out the ancestors of matches
    val idsOfAncestorsOfMatches = matches.flatMap {
      it.tag.ancestorsUp()
    }.mapTo(HashSet(), Tag::id)

    // Filter out the ancestors of matches
    val (ancestorPacks, leafLikePacks) = matches.partition { pair ->
      val tagId = pair.tag.id
      idsOfAncestorsOfMatches.contains(tagId)
    }

    // The leafLikes first, if need more, append ancestors
    val leafLikeChosens = leafLikePacks.sortedByDescending {
      it.score
    }.take(numLimit).map { it.tag }

    val numLack = numLimit - leafLikeChosens.size
    return if (numLack > 0) {
      leafLikeChosens + ancestorPacks.sortedByDescending { it.score }.take(numLack).map { it.tag }
    } else {
      leafLikeChosens
    }
  }

  class TagWithScore(val tag: Tag, val score: Int)

  companion object {
    val log: Logger = LoggerFactory.getLogger(TagAnalyzer::class.java)
  }
}