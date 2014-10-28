package sage.domain.service;

import java.util.Collection;
import java.util.Comparator;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sage.domain.repository.MessageRepository;
import sage.entity.Message;
import sage.util.Colls;

@Service
@Transactional
public class MessageService {
  @Autowired
  private MessageRepository messageRepo;

  public void send(Long userId, Long toUser, String content) {
    messageRepo.save(new Message(content, userId, toUser));
  }

  public Collection<Message> all(Long userId) {
    return Colls.copySort(byTime, messageRepo.byTo(userId));
  }

  public Collection<Message> withSomeone(Long userId, Long someone) {
    return Colls.copySort(byTime, messageRepo.byFromTo(userId, someone), messageRepo.byFromTo(someone, userId));
  }

  private static Comparator<Message> byTime = (m1, m2) -> (int) (m1.getTime().getTime() - m2.getTime().getTime());
}
