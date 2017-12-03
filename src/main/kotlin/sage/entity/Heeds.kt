package sage.entity

import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class FollowListHeed(
    @ManyToOne(optional = false)
    var user: User,
    @ManyToOne
    var list: FollowListEntity
) : AutoModel() {

  companion object : BaseFind<Long, FollowListHeed>(FollowListHeed::class) {
    fun find(userId: Long, listId: Long) =
        where().eq("user", User.ref(userId)).eq("list", FollowListEntity.ref(listId)).findUnique()
    fun byUser(userId: Long): List<FollowListHeed> = where().eq("user", User.ref(userId)).findList()
  }
}

@Entity
class TagHeed(
    @ManyToOne(optional = false)
    var user: User,
    @ManyToOne
    var tag: Tag
) : AutoModel() {

  companion object : BaseFind<Long, TagHeed>(TagHeed::class) {
    fun find(userId: Long, tagId: Long) =
        where().eq("user", User.ref(userId)).eq("tag", Tag.ref(tagId)).findUnique()
    fun byUser(userId: Long): List<TagHeed> = where().eq("user", User.ref(userId)).findList()
  }
}