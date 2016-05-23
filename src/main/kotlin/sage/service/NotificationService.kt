package sage.service

import com.avaje.ebean.Ebean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.entity.Comment
import sage.entity.Notification
import sage.entity.Notification.Type
import sage.entity.TopicReply
import sage.transfer.NotifCounter
import sage.transfer.NotifView
import java.util.*

@Service
class NotificationService @Autowired constructor(
    private val userService: UserService
) {

  fun all(userId: Long) = Notification.byOwner(userId).mapNotNull { toView(it) }

  fun unread(userId: Long) =
      Notification.byOwnerAndRead(userId, false).findList().mapNotNull { toView(it) }

  fun unreadCounts(userId: Long): Map<String, NotifCounter> {
    val counts = HashMap<String, NotifCounter>()
    unread(userId).forEach { nv ->
      var notifCounter: NotifCounter? = counts[nv.type]
      if (notifCounter == null) {
        notifCounter = NotifCounter()
        notifCounter.desc = Notification.Type.valueOf(nv.type).shortDesc
        counts.put(nv.type, notifCounter)
      }
      notifCounter.count++
    }
    return counts
  }

  fun confirmRead(userId: Long, ids: List<Long>) {
    Ebean.createUpdate(Notification::class.java, "read = true where id in :ids and ownerId = :userId")
        .setParameter("ids", ids).setParameter("userId", userId).execute()
  }

  fun toView(notif: Notification): NotifView? {
    val sourceId = notif.sourceId ?: return null
    val source: String
    when (notif.type!!.sourceType) {
      Notification.SourceType.USER -> source = "/users/" + sourceId
      Notification.SourceType.TWEET -> source = "/tweets/" + sourceId
      Notification.SourceType.COMMENT -> {
        val tweetId = Comment.byId(sourceId)?.sourceId
        if (tweetId != null) {
          source = "/tweets/$tweetId?commentId=$sourceId"
        } else
          return null
      }
      Notification.SourceType.TOPIC_POST -> source = "/topic/" + sourceId
      Notification.SourceType.TOPIC_REPLY -> {
        val topicPostId = TopicReply.byId(sourceId)?.topicPost()?.id
        if (topicPostId != null) {
          source = "/topics/$topicPostId?replyId=$sourceId"
        } else
          return null
      }
      else -> throw IllegalArgumentException("Wrong sourceType: " + notif.type!!.sourceType)
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
    send(Notification(toUser, fromUser, Type.REPLIED, sourceId))
  }

  fun mentionedByTweet(toUser: Long, fromUser: Long, sourceId: Long) {
    send(Notification(toUser, fromUser, Type.MENTIONED_TWEET, sourceId))
  }

  fun mentionedByComment(toUser: Long, fromUser: Long, sourceId: Long) {
    send(Notification(toUser, fromUser, Type.MENTIONED_COMMENT, sourceId))
  }

  fun mentionedByTopicPost(toUser: Long, fromUser: Long, postId: Long) {
    send(Notification(toUser, fromUser, Type.MENTIONED_TOPIC_POST, postId))
  }

  fun mentionedByTopicReply(toUser: Long, fromUser: Long, replyId: Long) {
    send(Notification(toUser, fromUser, Type.MENTIONED_TOPIC_REPLY, replyId))
  }

  fun repliedInTopic(toUser: Long, fromUser: Long, replyId: Long) {
    send(Notification(toUser, fromUser, Type.REPIED_IN_TOPIC, replyId))
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
