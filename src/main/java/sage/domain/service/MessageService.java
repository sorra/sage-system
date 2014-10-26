package sage.domain.service;

import java.util.Collection;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sage.domain.repository.MessageRepository;
import sage.entity.Message;

@Service
@Transactional
public class MessageService {
  @Autowired
  private MessageRepository messageRepo;

  public void send(Long userId, Long toUser, String content) {
    messageRepo.save(new Message(content, userId, toUser));
  }

  public Collection<Message> all(Long userId) {
    return messageRepo.byTo(userId);
  }

  public Collection<Message> fromSomeone(Long userId, Long fromUser) {
    return messageRepo.byFromTo(fromUser, userId);
  }
}
