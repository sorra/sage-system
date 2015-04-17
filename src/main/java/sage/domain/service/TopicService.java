package sage.domain.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.repository.TopicRepository;
import sage.entity.Topic;
import sage.transfer.TopicPreview;
import sage.util.Colls;

@Service
@Transactional
public class TopicService {
  @Autowired
  private TopicRepository topicRepo;

  public Collection<TopicPreview> topics() {
    return Colls.map(topicRepo.all(), TopicPreview::new);
  }

  public Topic getTopic(long topicId) {
    return topicRepo.get(topicId);
  }
}
