package sage.service

import com.avaje.ebean.EbeanServer
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.DomainException
import sage.entity.Tag
import sage.transfer.TagCard
import sage.transfer.TagLabel
import sage.transfer.TagNode
import sage.web.context.Json
import javax.annotation.PostConstruct

@Service
class TagService {

  fun create(name: String, parentId: Long, intro: String): Long {
    var intro = intro
    if (StringUtils.isBlank(name)) {
      throw IllegalArgumentException("name is empty!")
    }
    if (Tag.byId(parentId) == null) {
      throw IllegalArgumentException("parentId $parentId is wrong!")
    }
    if (StringUtils.isBlank(intro)) {
      intro = "啊，$name！"
    }
    if (Tag.where().eq("name", name).eq("parent", Tag.ref(parentId)).findUnique() == null) {
      val tag = Tag(name, Tag.ref(parentId), intro)
      tag.save()
      return tag.id
    } else {
      throw DomainException("Tag[name: %s, parentId: %s] already exists", name, parentId)
    }
  }

  fun getTagCard(tagId: Long): TagCard {
    return TagCard(Tag.byId(tagId))
  }

  fun getTagLabel(tagId: Long): TagLabel {
    return TagLabel(Tag.byId(tagId))
  }

  fun getTagTree() = TagNode(Tag.byId(Tag.ROOT_ID))

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

  @Autowired // ensure Ebean is available before data setup
  private lateinit var ebeanServer: EbeanServer

  @PostConstruct
  fun setup() {
    if (Tag.byId(Tag.ROOT_ID) == null) {
      val root = Tag(Tag.ROOT_NAME, null)
      root.id = Tag.ROOT_ID
      root.save()
      assertEqual(root.id, Tag.ROOT_ID)
    }
  }

  private fun assertEqual(a: Any, b: Any) {
    if (a != b) {
      throw AssertionError("Not equal! a = $a, b = $b")
    }
  }
}
