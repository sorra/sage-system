package sage.domain.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.commons.IdCommons;
import sage.domain.repository.*;
import sage.entity.Blog;
import sage.entity.TopicPost;
import sage.entity.TopicReply;

@Service
@Transactional
public class TopicService {
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private GroupRepository groupRepo;
  @Autowired
  private TopicPostRepository topicPostRepo;
  @Autowired
  private BlogRepository blogRepo;
  @Autowired
  private TopicReplyRepository topicReplyRepo;

  public TopicPost post(long userId, Blog blog, long groupId) {
    if (!IdCommons.equal(userId, blog.getAuthor().getId())) {
      throw new DomainRuntimeException("Cannot post topic. User[%d] is not the author of Blog[%d]", userId, blog.getId());
    }
    return topicPostRepo.save(new TopicPost(blog, groupRepo.load(groupId)));
  }

  public TopicPost post(long userId, long blogId, long groupId) {
    return post(userId, blogRepo.nonNull(blogId), groupId);
  }

  public TopicReply reply(long userId, String content, long topicPostId) {
    return topicReplyRepo.save(new TopicReply(topicPostRepo.load(topicPostId), userRepo.load(userId), new Date(), content));
  }

  public void setHiddenOfTopicPost(long id, boolean hidden) {
    TopicPost post = topicPostRepo.get(id);
    boolean origHidden = post.isHidden();
    if (origHidden != hidden) {
      post.setHidden(hidden);
      topicPostRepo.update(post);
    }
  }

  public TopicPost getTopicPost(long id) {
    return topicPostRepo.nonNull(id);
  }

  public List<TopicReply> getTopicReplies(long topicPostId) {
    return topicReplyRepo.byTopicPost(topicPostId);
  }

  public Collection<TopicPost> groupTopics(long groupId) {
    return topicPostRepo.byGroup(groupId);
  }
}
