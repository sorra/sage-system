package sage.entity

import java.util.*
import javax.persistence.*

@Entity
class Tag : BaseModel {

  var name: String

  var isCore: Boolean = false

  @Column(columnDefinition = "TEXT")
  @Lob @Basic
  var intro: String

  @ManyToOne
  var parent: Tag?

  @OneToMany(mappedBy = "parent")
  var children: Set<Tag> = HashSet()

  @JvmOverloads constructor(name: String, parent: Tag?, intro: String = "") {
    this.name = name
    this.parent = parent
    this.intro = intro
  }

  /**
   * @return a chain from itself to ancestors, excluding root; is empty for root
   */
  fun chainUp(): List<Tag> {
    val chain = LinkedList<Tag>()
    if (id == ROOT_ID)
      return chain

    var current: Tag = this
    while (current.id != ROOT_ID) {
      chain.add(current)
      current = current.parent!!
    }
    return chain
  }

  /**
   * @return all of its descendant tags
   */
  fun descendants(): Set<Tag> {
    val descendants = HashSet<Tag>()
    for (child in children) {
      descendants.add(child)
      descendants.addAll(child.descendants())
    }
    return descendants
  }

  fun getQueryTags(): Set<Tag> {
    val queryTags = HashSet(descendants())
    queryTags.add(this)
    return queryTags
  }

  companion object : Find<Long, Tag>() {
    val ROOT_ID: Long = 1
    val ROOT_NAME = "无"

    fun get(id: Long) = getNonNull(Tag::class, id)

    fun multiGet(ids: Collection<Long>) = ids.mapNotNull { byId(it) }.toMutableSet()

    fun getQueryTags(tags: Collection<Tag>): Set<Tag> {
      val queryTags = HashSet<Tag>()
      for (node in tags) {
        queryTags.add(node)
        queryTags.addAll(node.descendants())
      }
      return queryTags
    }
  }
}