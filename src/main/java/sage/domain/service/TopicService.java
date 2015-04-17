package sage.domain.service;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.repository.TopicRepository;
import sage.domain.repository.UserRepository;
import sage.entity.Topic;
import sage.transfer.TopicPreview;
import sage.transfer.TopicView;
import sage.util.Colls;

@Service
@Transactional
public class TopicService {
  @Autowired
  private TopicRepository topicRepo;
  @Autowired
  private UserRepository userRepo;

  public Collection<TopicPreview> topics() {
    return Colls.map(topicRepo.all(), TopicPreview::new);
  }

  public Topic getTopic(long topicId) {
    return topicRepo.get(topicId);
  }

  public Topic newTopic(long userId, String title, String content) {
    return topicRepo.save(new Topic(title, content, userRepo.load(userId), new Date()));
  }

  public void updateTopic(long userId, long id, String title, String content) {
    Topic topic = topicRepo.get(id);
    if (userId != topic.getAuthor().getId()) {
      throw new DomainRuntimeException("User[%d] is not the author of Topic[%d]", userId, topic.getAuthor().getId());
    }
    topic.setTitle(title);
    topic.setContent(content);
    topic.setModifiedTime(new Date());
    topicRepo.update(topic);
  }
}
