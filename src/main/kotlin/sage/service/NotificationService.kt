package sage.service

import com.avaje.ebean.Ebean
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.entity.Comment
import sage.entity.Notification
import sage.entity.Notification.Type
import sage.transfer.NotifCounter
import sage.transfer.NotifView
import java.util.*

@Service
class NotificationService @Autowired constructor(
    private val userService: UserService
) {
  private val log = LoggerFactory.getLogger(javaClass)

  fun all(userId: Long) = Notification.byOwner(userId).mapNotNull { toView(it) }

  fun unread(userId: Long) =
      Notification.byOwnerAndIsRead(userId, false).findList().mapNotNull { toView(it) }

  fun unreadCounts(userId: Long): Map<String, NotifCounter> {
    val counts = HashMap<String, NotifCounter>()
    unread(userId).forEach { nv ->
      var notifCounter: NotifCounter? = counts[nv.type]
      if (notifCounter == null) {
        notifCounter = NotifCounter(desc = Notification.Type.valueOf(nv.type).shortDesc)
        counts.put(nv.type, notifCounter)
      }
      notifCounter.count++
    }
    return counts
  }

  fun markRead(userId: Long, ids: List<Long>) {
    Ebean.createUpdate(Notification::class.java, "update Notification set isRead = true where id in (:ids) and ownerId = :userId")
        .setParameter("ids", ids).setParameter("userId", userId)
        .execute()
  }

  fun toView(notif: Notification): NotifView? {
    val sourceId = notif.sourceId ?: return null
    val source: String
    when (notif.type!!.sourceType) {
      Notification.SourceType.USER -> source = ""
      Notification.SourceType.TWEET -> source = "/tweets/" + sourceId
      Notification.SourceType.COMMENT -> {
        val comment = Comment.byId(sourceId)
        if (comment != null) {
          val sourceName = when (comment.sourceType) {
            Comment.BLOG -> "blogs"
            Comment.TWEET -> "tweets"
            else -> "null"
          }
          source = "/$sourceName/${comment.sourceId}?commentId=$sourceId"
        } else
          return null
      }
      else -> {
        log.error("Wrong sourceType: " + notif.type?.sourceType)
        return null
      }
    }
    return NotifView(notif, userService.getUserLabel(notif.senderId!!), source)
  }

  //TODO filter & black-list

  fun forwarded(toUser: Long, fromUser: Long, sourceId: Long) {
    send(Notification(toUser, fromUser, Type.FORWARDED, sourceId))
  }

  fun commented(toUser: Long, fromUser: Long, sourceId: Long) {
    send(Notification(toUser, fromUser, Type.COMMENTED, sourceId))
  }

  fun replied(toUser: Long, fromUser: Long, sourceId: Long) {
    send(Notification(toUser, fromUser, Type.REPLIED_IN_COMMENT, sourceId))
  }

  fun mentionedByTweet(toUser: Long, fromUser: Long, sourceId: Long) {
    send(Notification(toUser, fromUser, Type.MENTIONED_TWEET, sourceId))
  }

  fun mentionedByComment(toUser: Long, fromUser: Long, sourceId: Long) {
    send(Notification(toUser, fromUser, Type.MENTIONED_COMMENT, sourceId))
  }

  fun mentionedByBlog(toUser: Long, fromUser: Long, blogId: Long) {
    send(Notification(toUser, fromUser, Type.MENTIONED_BLOG, blogId))
  }

  fun followed(toUser: Long, fromUser: Long) {
    send(Notification(toUser, fromUser, Type.FOLLOWED, fromUser))
  }

  private fun send(notif: Notification) {
    // Don't send to oneself
    if (notif.ownerId == notif.senderId) {
      return
    }
    notif.save()
  }

}
