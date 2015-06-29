package sage.domain.service;

import java.util.Collection;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.repository.NotifRepository;
import sage.domain.repository.UserNotifStatusRepository;
import sage.entity.Notif;
import sage.entity.Notif.Type;
import sage.entity.UserNotifStatus;

@Service
@Transactional
public class NotifService {

  @Autowired
  private NotifRepository notifRepo;
  @Autowired
  private UserNotifStatusRepository userNotifStatusRepo;

  @Transactional(readOnly=true)
  public Collection<Notif> all(long userId) {
    return notifRepo.byOwner(userId);
  }

  @Transactional(readOnly = true)
  public Collection<Notif> unread(long userId) {
    Long readToId = userNotifStatusRepo.get(userId).getReadToId();
    if (readToId == null) {
      return all(userId);
    } else {
      return notifRepo.byOwnerAndAfterId(userId, readToId);
    }
  }

  public void setReadTo(long userId, long notifId) {
    UserNotifStatus status = userNotifStatusRepo.get(userId);
    if (status == null) {
      status = new UserNotifStatus(userId, notifId);
      userNotifStatusRepo.save(status);
    } else {
      status.setReadToId(notifId);
      userNotifStatusRepo.update(status);
    }
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

  public void followed(Long toUser, Long fromUser) {
    sendNotif(new Notif(toUser, fromUser, Type.FOLLOWED, null));
  }
  
  private void sendNotif(Notif notif) {
    // Don't send to oneself
    if (Objects.equals(notif.getOwnerId(), notif.getSenderId())) {
      return;
    }
    notifRepo.save(notif);
  }
  
}
