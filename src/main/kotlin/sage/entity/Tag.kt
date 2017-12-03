package sage.entity

import sage.domain.commons.DomainException
import sage.transfer.TagCard
import sage.transfer.TagLabel
import sage.transfer.TagNode
import java.util.*
import javax.persistence.*

@Entity
class Tag(
    var name: String,
    @ManyToOne
    var parent: Tag?,
    var isCore: Boolean,
    @Column(columnDefinition = "TEXT")
    var intro: String = "",
    val creatorId: Long) : AutoModel() {

  @OneToMany(mappedBy = "parent")
  var children: Set<Tag> = HashSet()

  /**
   * @return a chain from itself to ancestors, excluding root; is empty for root
   */
  fun chainUp(): List<Tag> = listOf(this) + ancestorsUp()

  fun ancestorsUp(): List<Tag> {
    val ancestors = LinkedList<Tag>()
    if (id == ROOT_ID) {
      return ancestors
    }
    var current: Tag = this.parent!!
    while (current.id != ROOT_ID) {
      ancestors.add(current)
      current = current.parent!!
    }
    return ancestors
  }

  @Suppress("unused")
  fun chainDown(): List<Tag> = chainUp().asReversed()

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

  fun getQueryTags(): Set<Tag> = descendants() + this

  @PrePersist @PreUpdate
  fun nameMustBeUnique() {
    siblingsCannotHaveThatName(name)
  }

  fun siblingsCannotHaveThatName(name: String) {
    parent?.let { parent ->
      if (parent.descendants().find { it.id != id && it.name == name } != null) {
        throw DomainException("标签\"${parent.name}\"之下有同名标签: \"$name\"")
      }
    }
  }

  fun toTagNode(): TagNode {
    val t = TagNode()
    t.id = id
    t.name = name
    t.isCore = isCore
    t.children = children
        .filter { it.isCore }
        .map { it.toTagNode() }
    return t
  }

  fun toTagCard(): TagCard {
    val t = TagCard()
    t.id = id
    t.name = name
    t.intro = intro
    t.isCore = isCore

    for (node in chainUp()) {
      t.chainUp.add(node.toTagLabel())
    }

    for (child in children) {
      t.children.add(child.toTagLabel())
    }

    t.chainStr = produceChainStr()

    return t
  }

  fun toTagLabel(): TagLabel {
    val t = TagLabel()
    t.id = id
    t.name = name
    t.isCore = isCore
    t.chainStr = produceChainStr()
    return t
  }

  private fun produceChainStr() = chainUp().asReversed().map(Tag::name).joinToString("->")

  companion object : BaseFind<Long, Tag>(Tag::class) {
    val ROOT_ID: Long = 1
    val ROOT_NAME = "无"

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