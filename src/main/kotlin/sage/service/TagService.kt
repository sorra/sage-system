package sage.service

import org.springframework.stereotype.Service
import sage.entity.Tag
import sage.transfer.TagCard
import sage.transfer.TagLabel
import sage.transfer.TagNode
import sage.web.context.Json

@Service
class TagService {

  fun getTagCard(tagId: Long): TagCard {
    return TagCard(Tag.byId(tagId))
  }

  fun getTagLabel(tagId: Long): TagLabel {
    return TagLabel(Tag.byId(tagId))
  }

  fun getTagTree() = TagNode(Tag.byId(Tag.ROOT_ID))

  // TODO Cache it
  fun getTagTreeJson() = Json.json(getTagTree())

  fun getTagsByName(name: String): MutableCollection<Tag> {
    return Tag.where().eq("name", name).findList()
  }

  fun getSameNameTags(tagId: Long): Collection<Tag> {
    val tag = Tag.get(tagId)
    val tagsByName = getTagsByName(tag.name)
    tagsByName.remove(tag)
    return tagsByName
  }

  @Synchronized fun init() {
    if (!needInitialize) {
      throw RuntimeException()
    }
    val root = Tag(Tag.ROOT_NAME, null)
    root.save()
    assertEqual(root.id, Tag.ROOT_ID)
    needInitialize = false
  }

  companion object {

    @Volatile private var needInitialize = true

    private fun assertEqual(a: Any, b: Any) {
      val equal = a == b
      if (!equal) {
        throw AssertionError("Not equal! a = $a, b = $b")
      }
    }
  }
}
