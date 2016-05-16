package sage.entity

import javax.persistence.Entity

@Entity
class Message(
    var content: String?,
    var fromUser: Long?,
    var toUser: Long?
) : BaseModel() {
  companion object : Find<Long, Message>() {
    fun byFromTo(fromUser: Long, toUser: Long) =
        where().eq("fromUser", fromUser).eq("toUser", toUser).findList()

    fun byFromToAfter(fromUser: Long, toUser: Long, afterId: Long) =
        where().eq("fromUser", fromUser).eq("toUser", toUser).gt("id", afterId).findList()
  }
}