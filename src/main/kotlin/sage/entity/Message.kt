package sage.entity

import javax.persistence.Entity

@Entity
class Message(
    var content: String,
    var fromUser: Long,
    var toUser: Long
) : AutoModel() {
  companion object : BaseFind<Long, Message>(Message::class) {
    fun byFromTo(fromUser: Long, toUser: Long): List<Message> =
        where().eq("fromUser", fromUser).eq("toUser", toUser).findList()

    fun byFromToAfter(fromUser: Long, toUser: Long, afterId: Long): List<Message> =
        where().eq("fromUser", fromUser).eq("toUser", toUser).gt("id", afterId).findList()
  }
}