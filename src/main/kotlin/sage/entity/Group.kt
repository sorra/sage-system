package sage.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "groupp")
class Group : BaseModel {

  var name: String = ""
  var introduction: String = ""

  @ManyToMany
  var tags: Set<Tag> = HashSet()

  @ManyToOne(optional = false)
  var creator: User?

  @ManyToMany
  var members: MutableSet<User> = HashSet()

  constructor(name: String, introduction: String, tags: Set<Tag>, creator: User) {
    this.name = name
    this.introduction = introduction
    this.tags = tags
    this.creator = creator
    this.members.add(creator)
  }

  companion object : Find<Long, Group>() {
    fun get(id: Long) = getNonNull(Group::class, id)
  }
}