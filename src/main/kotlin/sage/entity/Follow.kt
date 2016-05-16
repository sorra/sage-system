package sage.entity

import sage.domain.commons.IdCommons
import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne

@Entity
class Follow : BaseModel {

  @ManyToOne(optional = false)
  var source: User

  @ManyToOne(optional = false)
  var target: User

  var reason: String? = null

  @ManyToMany(fetch = FetchType.EAGER)
  var tags: MutableSet<Tag> = HashSet()

  /** If auto-include new tags  */
  var isIncludeNew: Boolean = false

  /** If include all tags, ignoring selected tags  */
  var isIncludeAll: Boolean = false

  /** Used by includeNew  */
  var userTagOffset: Long = 0

  constructor(source: User, target: User, reason: String, tags: Set<Tag>, includeNew: Boolean, includeAll: Boolean, userTagOffset: Long) {
    if (IdCommons.equal(source.id, target.id)) {
      throw IllegalArgumentException("source should not equal to target!")
    }
    this.source = source
    this.target = target
    this.reason = reason
    this.tags = HashSet(tags)
    this.isIncludeNew = includeNew
    this.isIncludeAll = includeAll
    this.userTagOffset = userTagOffset
  }

  companion object : Find<Long, Follow>() {

    fun find(sourceId: Long, targetId: Long) =
        where().eq("source", User.ref(sourceId)).eq("target", User.ref(targetId)).findUnique()

    fun followings(userId: Long) = where().eq("source", User.ref(userId)).findList()
    fun followers(userId: Long) = where().eq("target", User.ref(userId)).findList()

    fun followingsCount(userId: Long) = where().eq("source", User.ref(userId)).findRowCount()
    fun followersCount(userId: Long) = where().eq("target", User.ref(userId)).findRowCount()
  }
}