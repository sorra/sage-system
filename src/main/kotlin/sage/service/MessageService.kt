package sage.service

import org.springframework.stereotype.Service
import sage.entity.Message

@Service
class MessageService {

  fun send(userId: Long?, toUser: Long?, content: String) {
    Message(content, userId, toUser).save()
  }

  fun all(userId: Long) =
      Message.where().eq("to", userId).findList() + Message.where().eq("from", userId).findList()

  fun withSomeone(userId: Long, someone: Long) =
      (Message.byFromTo(userId, someone) + Message.byFromTo(someone, userId))
          .sortedBy { it.whenCreated?.time ?: 0 }

  fun withSomeoneAfterThat(userId: Long, someone: Long, afterId: Long) =
      (Message.byFromToAfter(userId, someone, afterId) + Message.byFromToAfter(someone, userId, afterId))
          .sortedBy { it.whenCreated?.time ?: 0 }
}
