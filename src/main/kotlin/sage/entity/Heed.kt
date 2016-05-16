package sage.entity

import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class FollowListHeed(
    @ManyToOne
    var user: User?,
    @ManyToOne
    var list: FollowListEntity?) : BaseModel() {
  companion object : Find<Long, FollowListHeed>() {
    fun find(userId: Long, listId: Long) =
        where().eq("user", User.ref(userId)).eq("list", FollowListEntity.ref(listId)).findUnique()
    fun byUser(userId: Long) = where().eq("user", User.ref(userId)).findList()
  }
}

@Entity
class TagHeed(
    @ManyToOne
    var user: User?,
    @ManyToOne
    var tag: Tag?) : BaseModel() {
  companion object : Find<Long, TagHeed>() {
    fun find(userId: Long, tagId: Long) =
        where().eq("user", User.ref(userId)).eq("tag", Tag.ref(tagId)).findUnique()
    fun byUser(userId: Long) = where().eq("user", User.ref(userId)).findList()
  }
}