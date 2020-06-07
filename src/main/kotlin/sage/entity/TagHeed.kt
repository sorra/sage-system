package sage.entity

import javax.persistence.Entity
import javax.persistence.ManyToOne

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
