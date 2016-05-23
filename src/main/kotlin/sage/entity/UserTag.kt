package sage.entity

import javax.persistence.*

@Entity
@Table(uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("user_id", "tag_id"))))
class UserTag(var userId: Long = 0, var tagId: Long = 0) : BaseModel() {

  companion object : Find<Long, UserTag>() {
    fun lastIdByUser(userId: Long) = select("id").where().eq("userId", userId)
        .orderBy("id desc").setMaxRows(1).findUnique()?.id ?: 0

    fun byUser(userId: Long) = where().eq("userId", userId).findList()

    fun byUserAndAfterId(userId: Long, afterId: Long) =
        where().eq("userId", userId).gt("id", afterId).findList()
  }
}