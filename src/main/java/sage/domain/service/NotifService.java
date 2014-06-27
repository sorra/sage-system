package sage.domain.service;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.common.base.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sage.domain.repository.nosql.NotifRepository;
import sage.entity.nosql.Notif;
import sage.entity.nosql.Notif.Type;

@Service
public class NotifService {

  @Autowired
  private NotifRepository notifRepo;
  
  public Collection<Notif> getNotifs(Long userId) {
    return notifRepo.findAll(userId);
  }
  
  //TODO filter & black-list
  
  public void forwarded(Long toUser, Long fromUser, Long sourceId) {
    sendNotif(new Notif(toUser, fromUser, Type.FORWARDED, sourceId));
  }
  
  public void commented(Long toUser, Long fromUser, Long sourceId) {
    sendNotif(new Notif(toUser, fromUser, Type.COMMENTED, sourceId));
  }
  
  public void mentionedByTweet(Long toUser, Long fromUser, Long sourceId) {
    sendNotif(new Notif(toUser, fromUser, Type.MENTIONED_TWEET, sourceId));
  }
  
  public void mentionedByComment(Long toUser, Long fromUser, Long sourceId) {
    sendNotif(new Notif(toUser, fromUser, Type.MENTIONED_COMMENT, sourceId));
  }
  
  private Boolean sendNotif(Notif notif) {
    // Don't send to oneself
    if (Objects.equal(notif.getOwnerId(), notif.getSenderId())) {
      return false;
    }
    
    long time = System.currentTimeMillis();
    String id = generateId(notif, time);
    try {
      Boolean success = notifRepo.add(id, notif).get();
      if (success) {
        return success;
      } else {
        // Retry once
        id = generateId(notif, time+1);
        return notifRepo.add(id, notif).get();
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Format: senderId_timeHexString
   * @param notif The notif
   * @param time Current system time
   * @return generated doc id
   */
  private String generateId(Notif notif, long time) {
    String id = notif.getSenderId() + "_" + Long.toHexString(time);
    notif.setId(id);
    return id;
  }
}
