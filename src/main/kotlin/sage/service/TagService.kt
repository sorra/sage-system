package sage.service

import com.avaje.ebean.EbeanServer
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.DomainException
import sage.entity.Blog
import sage.entity.Tag
import sage.transfer.TagCard
import sage.transfer.TagLabel
import sage.transfer.TagNode
import sage.web.context.Json
import java.util.*
import javax.annotation.PostConstruct

@Service
class TagService {

  fun create(userId: Long, name: String, parentId: Long, isCore: Boolean, intro: String): Tag {
    if (StringUtils.isBlank(name)) {
      throw IllegalArgumentException("name is empty!")
    }
    if (Tag.byId(parentId) == null) {
      throw IllegalArgumentException("parentId $parentId is wrong!")
    }
    if (Tag.where().eq("name", name).eq("parent", Tag.ref(parentId)).findUnique() == null) {
      val tag = Tag(name, Tag.ref(parentId), isCore, if(intro.isNotBlank()) intro else "啊，$name！", userId)
      tag.save()
      return tag
    } else {
      throw DomainException("Tag[name: %s, parentId: %s] already exists", name, parentId)
    }
  }

  fun getTagCard(tagId: Long): TagCard {
    return TagCard(Tag.get(tagId))
  }

  fun getTagLabel(tagId: Long): TagLabel? {
    return Tag.byId(tagId)?.run { TagLabel(this) }
  }

  fun getTagTree() = TagNode(Tag.byId(Tag.ROOT_ID))

  fun getTagTreeJson() = Json.json(getTagTree())

  private val powers = arrayOf(1, 10, 100, 1000, 10000)
  private val maxPower = 10000

  fun hotTags(hotK: Int): List<Tag> {
    val counters = HashMap<Long, Int>()
    fun countTagHeats(tags: Iterable<Tag>, factor: Int) {
      tags.flatMap(Tag::chainUp).forEach { tag ->
        val plus = tag.chainUp().size.let {
          if (it <= 0) 0
          else if (it > powers.size) maxPower
          else powers[it-1]
        }
        val count = counters[tag.id] ?: 0
        counters[tag.id] = count + factor * plus
      }
    }

    Blog.findEach { countTagHeats(it.tags, 1) }

    val topKs = arrayOfNulls<Pair<Long, Int>>(hotK)
    var minOfK = Pair<Int, Int>(0, 0)
    counters.forEach { id, count ->
      if (count > minOfK.second) {
        topKs[minOfK.first] = Pair(id, count)
        topKs.forEachIndexed { idx, p ->
            if (p == null) minOfK = Pair(idx, 0)
            else if (p.second < minOfK.second) minOfK = Pair(idx, p.second)
        }
        return@forEach
      }
    }
    return topKs.mapNotNull { p ->
      p?.let { Tag.get(it.first) }
    }.asReversed()
  }

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
      val root = Tag(Tag.ROOT_NAME, null, isCore = true, creatorId = 1)
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
