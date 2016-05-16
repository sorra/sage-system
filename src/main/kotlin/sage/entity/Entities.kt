package sage.entity

import com.avaje.ebean.Model
import javax.persistence.*

@Entity
class Fav(
    var link: String?,
    @ManyToOne
    var owner: User?) : BaseModel() {
  companion object : Find<Long, Fav>() {
    fun ofOwner(ownerId: Long) = where().eq("owner", User.ref(ownerId)).findList()
  }
}

@Entity
class FileItem(
    var name: String?,
    var webPath: String?,
    var storePath: String?,
    var ownerId: Long?) : BaseModel() {
  companion object : Find<Long, FileItem>()
}

@Entity
class UserNotifStatus(@Id var userId: Long = 0, var readToId: Long = 0) : Model() {
  companion object : Find<Long, UserNotifStatus>()
}

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