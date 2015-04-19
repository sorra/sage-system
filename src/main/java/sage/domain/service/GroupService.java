package sage.domain.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.commons.IdCommons;
import sage.domain.repository.GroupRepository;
import sage.domain.repository.GroupTopicRepository;
import sage.domain.repository.TagRepository;
import sage.entity.Blog;
import sage.entity.Group;
import sage.entity.GroupTopic;

@Service
public class GroupService {
  @Autowired
  private GroupRepository groupRepo;
  @Autowired
  private GroupTopicRepository groupTopicRepo;
  @Autowired
  private TagRepository tagRepo;

  public Group getGroup(long id) {
    return groupRepo.get(id);
  }

  public GroupTopic getTopic(long id) {
    return groupTopicRepo.get(id);
  }

  public GroupTopic post(long userId, Blog blog) {
    if (!IdCommons.equal(userId, blog.getAuthor().getId())) {
      throw new DomainRuntimeException("Cannot post topic. User[%d] is not the author of Blog[%d]", userId, blog.getId());
    }
    return groupTopicRepo.save(new GroupTopic(blog));
  }

  public Collection<Group> byTags(Collection<Long> tagIds) {
    return groupRepo.byTags(tagRepo.byIds(tagIds));
  }

  public Collection<GroupTopic> topics(long groupId) {
    return groupTopicRepo.byGroup(groupId);
  }
}
