package sage.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.entity.Comment
import sage.entity.Notif
import sage.entity.Notif.Type
import sage.entity.TopicReply
import sage.entity.UserNotifStatus
import sage.transfer.NotifView

@Service
class NotifService {
  @Autowired
  private val userService: UserService? = null

  fun all(userId: Long): Collection<NotifView> {
    return Notif.byOwner(userId).flatMap { toView(it) }
  }

  fun unread(userId: Long): Collection<NotifView> {
    val readToId = UserNotifStatus.byId(userId)?.readToId
    if (readToId == null) {
      return all(userId)
    } else {
      return Notif.byOwnerAndAfterId(userId, readToId).flatMap { toView(it) }
    }
  }

  fun readTo(userId: Long, notifId: Long) {
    var status = UserNotifStatus.byId(userId)
    if (status == null) {
      status = UserNotifStatus(userId, notifId)
      status.save()
    } else {
      status.readToId = notifId
      status.update()
    }
  }

  fun toView(notif: Notif): Iterable<NotifView> {
    val sourceId = notif.sourceId ?: return emptyList()
    val source: String
    when (notif.type!!.sourceType) {
      Notif.SourceType.USER -> source = "/private/" + sourceId
      Notif.SourceType.TWEET -> source = "/tweet/" + sourceId
      Notif.SourceType.COMMENT -> {
        val tweetId = Comment.byId(sourceId)?.sourceId
        if (tweetId != null) {
          source = "/tweet/$tweetId?comment=$sourceId"
        } else
          return emptyList()
      }
      Notif.SourceType.TOPIC_POST -> source = "/topic/" + sourceId
      Notif.SourceType.TOPIC_REPLY -> {
        val topicPostId = TopicReply.byId(sourceId)?.topicPost?.id
        if (topicPostId != null) {
          source = "/topic/$topicPostId?reply=$sourceId"
        } else
          return emptyList()
      }
      else -> throw IllegalArgumentException("Wrong sourceType: " + notif.type!!.sourceType)
    }
    return listOf(NotifView(notif, userService!!.getUserLabel(notif.senderId!!), source))
  }

  //TODO filter & black-list

  fun forwarded(toUser: Long?, fromUser: Long?, sourceId: Long?) {
    sendNotif(Notif(toUser, fromUser, Type.FORWARDED, sourceId))
  }

  fun commented(toUser: Long?, fromUser: Long?, sourceId: Long?) {
    sendNotif(Notif(toUser, fromUser, Type.COMMENTED, sourceId))
  }

  fun replied(toUser: Long?, fromUser: Long?, sourceId: Long?) {
    sendNotif(Notif(toUser, fromUser, Type.REPLIED, sourceId))
  }

  fun mentionedByTweet(toUser: Long?, fromUser: Long?, sourceId: Long?) {
    sendNotif(Notif(toUser, fromUser, Type.MENTIONED_TWEET, sourceId))
  }

  fun mentionedByComment(toUser: Long?, fromUser: Long?, sourceId: Long?) {
    sendNotif(Notif(toUser, fromUser, Type.MENTIONED_COMMENT, sourceId))
  }

  fun mentionedByTopicPost(toUser: Long?, fromUser: Long?, postId: Long?) {
    sendNotif(Notif(toUser, fromUser, Type.MENTIONED_TOPIC_POST, postId))
  }

  fun mentionedByTopicReply(toUser: Long?, fromUser: Long?, replyId: Long?) {
    sendNotif(Notif(toUser, fromUser, Type.MENTIONED_TOPIC_REPLY, replyId))
  }

  fun repliedInTopic(toUser: Long?, fromUser: Long?, replyId: Long?) {
    sendNotif(Notif(toUser, fromUser, Type.REPIED_IN_TOPIC, replyId))
  }

  fun followed(toUser: Long?, fromUser: Long?) {
    sendNotif(Notif(toUser, fromUser, Type.FOLLOWED, fromUser))
  }

  private fun sendNotif(notif: Notif) {
    // Don't send to oneself
    if (notif.ownerId == notif.senderId) {
      return
    }
    notif.save()
  }

}
