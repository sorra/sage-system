package sage.domain.service;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.commons.IdCommons;
import sage.domain.repository.*;
import sage.entity.*;
import sage.transfer.GroupPreview;
import sage.transfer.UserLabel;
import sage.util.Colls;

@Service
@Transactional
public class GroupService {
  @Autowired
  private GroupRepository groupRepo;
  @Autowired
  private GroupTopicRepository groupTopicRepo;
  @Autowired
  private TagRepository tagRepo;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private BlogRepository blogRepo;

  public GroupPreview getGroupPreview(long id) {
    return new GroupPreview(groupRepo.get(id));
  }

  public Collection<UserLabel> members(long groupId) {
    return UserLabel.listOf(groupRepo.get(groupId).getMembers());
  }

  public GroupPreview create(long userId, String name, String introduction, Collection<Long> tagIds) {
    if (name == null || name.isEmpty()) {
      throw new DomainRuntimeException("Must enter a group name!");
    }
    if (introduction == null || introduction.isEmpty()) {
      throw new DomainRuntimeException("Must enter a group introduction!");
    }

    Set<Tag> tags = tagRepo.byIds(tagIds);
    Group group = groupRepo.save(new Group(name, introduction, tags, userRepo.load(userId), new Date()));
    return new GroupPreview(group);
  }

  public Group edit(long userId, long groupId,
                    String name, String introduction, Collection<Long> tagIds) {
    if (name == null || name.isEmpty()) {
      throw new DomainRuntimeException("Must enter a group name!");
    }
    if (introduction == null || introduction.isEmpty()) {
      throw new DomainRuntimeException("Must enter a group introduction!");
    }
    Group group = groupRepo.get(groupId);
    if (group == null) {
      throw new DomainRuntimeException("Group[%d] does not exist!", groupId);
    }
    if (!group.getCreator().getId().equals(userId)) {
      throw new DomainRuntimeException("User[%d] is not the owner of Group[%d]", userId, groupId);
    }

    group.setName(name);
    group.setIntroduction(introduction);
    group.setTags(tagRepo.byIds(tagIds));
    groupRepo.update(group);

    return group;
  }

  public void join(long userId, long groupId) {
    User user = userRepo.load(userId);
    Group group = groupRepo.get(groupId);
    if (group.getMembers().add(user)) {
      groupRepo.save(group);
    }
  }

  public void leave(long userId, long groupId) {
    User user = userRepo.load(userId);
    Group group = groupRepo.get(groupId);
    if (group.getMembers().remove(user)) {
      groupRepo.save(group);
    }
  }

  public GroupTopic getTopic(long id) {
    return groupTopicRepo.get(id);
  }

  public GroupTopic post(long userId, Blog blog, long groupId) {
    if (!IdCommons.equal(userId, blog.getAuthor().getId())) {
      throw new DomainRuntimeException("Cannot post topic. User[%d] is not the author of Blog[%d]", userId, blog.getId());
    }
    return groupTopicRepo.save(new GroupTopic(blog, groupRepo.load(groupId)));
  }

  public GroupTopic post(long userId, long blogId, long groupId) {
    return post(userId, blogRepo.get(blogId), groupId);
  }

  public Collection<GroupPreview> byTags(Collection<Long> tagIds) {
    return Colls.map(groupRepo.byTags(tagRepo.byIds(tagIds)), GroupPreview::new);
  }

  public Collection<GroupPreview> all() {
    return Colls.map(groupRepo.all(), GroupPreview::new);
  }

  public Collection<GroupTopic> topics(long groupId) {
    return groupTopicRepo.byGroup(groupId);
  }
}
