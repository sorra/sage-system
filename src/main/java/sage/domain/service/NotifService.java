package sage.domain.service;

import java.util.Collection;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.repository.*;
import sage.entity.Notif;
import sage.entity.Notif.Type;
import sage.entity.UserNotifStatus;
import sage.transfer.NotifView;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class NotifService {
  @Autowired
  private UserService userService;
  @Autowired
  private CommentRepository commentRepo;
  @Autowired
  private NotifRepository notifRepo;
  @Autowired
  private UserNotifStatusRepository userNotifStatusRepo;
  @Autowired
  private TopicPostRepository topicPostRepo;
  @Autowired
  private TopicReplyRepository topicReplyRepo;

  @Transactional(readOnly=true)
  public Collection<NotifView> all(long userId) {
    return notifRepo.byOwner(userId).stream().map(this::toView).collect(toList());
  }

  @Transactional(readOnly = true)
  public Collection<NotifView> unread(long userId) {
    Long readToId = userNotifStatusRepo.optional(userId).map(s->s.getReadToId()).orElse(null);
    if (readToId == null) {
      return all(userId);
    } else {
      return notifRepo.byOwnerAndAfterId(userId, readToId).stream().map(this::toView).collect(toList());
    }
  }

  public void readTo(long userId, long notifId) {
    UserNotifStatus status = userNotifStatusRepo.get(userId);
    if (status == null) {
      status = new UserNotifStatus(userId, notifId);
      userNotifStatusRepo.save(status);
    } else {
      status.setReadToId(notifId);
      userNotifStatusRepo.update(status);
    }
  }

  public NotifView toView(Notif notif) {
    String source;
    switch (notif.getType().sourceType) {
      case USER:
        source = "/private/"+notif.getSourceId();
        break;
      case TWEET:
        source = "/tweet/"+notif.getSourceId();
        break;
      case COMMENT:
        long tweetId = commentRepo.nonNull(notif.getSourceId()).getSource().getId();
        source = "/tweet/"+tweetId+"?comment="+notif.getSourceId();
        break;
      case TOPIC_POST:
        source = "/topic/"+notif.getSourceId();
        break;
      case TOPIC_REPLY:
        long topicPostId = topicReplyRepo.nonNull(notif.getSourceId()).getTopicPost().getId();
        source = "/topic/" + topicPostId + "?reply="+notif.getSourceId();
        break;
      default:
        throw new IllegalArgumentException("Wrong sourceType: " + notif.getType().sourceType);
    }
    return new NotifView(notif, userService.getUserLabel(notif.getSenderId()), source);
  }
  
  //TODO filter & black-list
  
  public void forwarded(Long toUser, Long fromUser, Long sourceId) {
    sendNotif(new Notif(toUser, fromUser, Type.FORWARDED, sourceId));
  }
  
  public void commented(Long toUser, Long fromUser, Long sourceId) {
    sendNotif(new Notif(toUser, fromUser, Type.COMMENTED, sourceId));
  }

  public void replied(Long toUser, Long fromUser, Long sourceId) {
    sendNotif(new Notif(toUser, fromUser, Type.REPLIED, sourceId));
  }
  
  public void mentionedByTweet(Long toUser, Long fromUser, Long sourceId) {
    sendNotif(new Notif(toUser, fromUser, Type.MENTIONED_TWEET, sourceId));
  }
  
  public void mentionedByComment(Long toUser, Long fromUser, Long sourceId) {
    sendNotif(new Notif(toUser, fromUser, Type.MENTIONED_COMMENT, sourceId));
  }

  public void mentionedByTopicPost(Long toUser, Long fromUser, Long postId) {
    sendNotif(new Notif(toUser, fromUser, Type.MENTIONED_TOPIC_POST, postId));
  }

  public void mentionedByTopicReply(Long toUser, Long fromUser, Long replyId) {
    sendNotif(new Notif(toUser, fromUser, Type.MENTIONED_TOPIC_REPLY, replyId));
  }

  public void repliedInTopic(Long toUser, Long fromUser, Long replyId) {
    sendNotif(new Notif(toUser, fromUser, Type.REPIED_IN_TOPIC, replyId));
  }

  public void followed(Long toUser, Long fromUser) {
    sendNotif(new Notif(toUser, fromUser, Type.FOLLOWED, fromUser));
  }
  
  private void sendNotif(Notif notif) {
    // Don't send to oneself
    if (Objects.equals(notif.getOwnerId(), notif.getSenderId())) {
      return;
    }
    notifRepo.save(notif);
  }
  
}
