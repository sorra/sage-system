package sage.domain.service;

import java.util.Collection;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.commons.IdCommons;
import sage.domain.repository.BlogRepository;
import sage.domain.repository.GroupRepository;
import sage.domain.repository.TopicPostRepository;
import sage.entity.Blog;
import sage.entity.TopicPost;

@Service
@Transactional
public class TopicService {
  @Autowired
  private GroupRepository groupRepo;
  @Autowired
  private TopicPostRepository topicPostRepo;
  @Autowired
  private BlogRepository blogRepo;

  public TopicPost post(long userId, Blog blog, long groupId) {
    if (!IdCommons.equal(userId, blog.getAuthor().getId())) {
      throw new DomainRuntimeException("Cannot post topic. User[%d] is not the author of Blog[%d]", userId, blog.getId());
    }
    return topicPostRepo.save(new TopicPost(blog, groupRepo.load(groupId)));
  }

  public TopicPost post(long userId, long blogId, long groupId) {
    return post(userId, blogRepo.nonNull(blogId), groupId);
  }
  
  public TopicPost getTopic(long id) {
    return topicPostRepo.nonNull(id);
  }

  public Collection<TopicPost> groupTopics(long groupId) {
    return topicPostRepo.byGroup(groupId);
  }
}
