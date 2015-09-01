package sage.domain.service;

import java.util.*;
import java.util.stream.Collectors;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.commons.IdCommons;
import sage.domain.concept.Authority;
import sage.domain.repository.*;
import sage.entity.*;
import sage.transfer.TagLabel;
import sage.transfer.UserCard;
import sage.transfer.UserLabel;
import sage.transfer.UserSelf;
import sage.util.Colls;

import static java.util.stream.Collectors.toSet;

@Service
@Transactional
public class UserService {
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private FollowRepository followRepo;
  @Autowired
  private TagRepository tagRepo;
  @Autowired
  private TweetRepository tweetRepo;
  @Autowired
  private BlogRepository blogRepo;
  @Autowired
  private UserTagRepository userTagRepo;

  @Transactional(readOnly = true)
  public UserSelf getSelf(long userId) {
    return new UserSelf(userRepo.nonNull(userId),
        followRepo.followingCount(userId),
        followRepo.followerCount(userId),
        blogRepo.countByAuthor(userId),
        tweetRepo.countByAuthor(userId),
        topTags(userId));
  }

  public Collection<TagLabel> filterUserTags(long userId, Collection<TagLabel> existingTags) {
    Collection<TagLabel> userTags = Colls.copy(getSelf(userId).getTopTags());
    Collection<Long> tagIds = Colls.map(existingTags, TagLabel::getId);
    userTags.removeIf(t -> tagIds.contains(t.getId()));
    return userTags;
  }

  @Transactional(readOnly = true)
  public UserCard getUserCard(long cuid, long userId) {
    return new UserCard(userRepo.nonNull(userId),
        followRepo.followerCount(userId),
        blogRepo.countByAuthor(userId),
        tweetRepo.countByAuthor(userId),
        followRepo.find(cuid, userId),
        followRepo.find(userId, cuid),
        userTags(userId));
  }

  @Transactional(readOnly = true)
  public UserLabel getUserLabel(long userId) {
    return new UserLabel(userRepo.nonNull(userId));
  }

  public void changeInfo(long userId, String name, String intro, String avatar) {
    User user = userRepo.nonNull(userId);
    if (Objects.equals(name, user.getName()) && Objects.equals(intro, user.getIntro()) && avatar == null) {
      return;
    }
    user.setName(name);
    user.setIntro(intro);
    if (avatar != null) {
      user.setAvatar(avatar);
    }
    userRepo.update(user);
  }

  public void changeIntro(long userId, String intro) {
    User user = userRepo.nonNull(userId);
    user.setIntro(intro);
    userRepo.update(user);
  }

  public void changeAvatar(long userId, String avatar) {
    User user = userRepo.nonNull(userId);
    user.setAvatar(avatar);
    userRepo.update(user);
  }

  public void grantAuthority(long userId, Authority authority) {
    User user = userRepo.nonNull(userId);
    user.setAuthority(authority);
    userRepo.update(user);
  }

  public User login(String email, String password) {
    User user = userRepo.findByEmail(email);
    if (user == null) {
      throw new DomainRuntimeException("User[email: %s] does not exist", email);
    }
    if (checkPassword(password, user.getPassword())) {
      return user;
    } else {
      throw new DomainRuntimeException("Login failed, wrong user or password");
    }
  }

  public Long register(User user) {
    if (existsEmail(user)) {
      throw new DomainRuntimeException("Email(%s) already registered", user.getEmail());
    }
    user.setPassword(encryptPassword(user.getPassword()));
    userRepo.save(user);
    user.setName("u" + user.getId());
    userRepo.update(user);
    return user.getId();
  }

  private boolean existsEmail(User user) {
    return userRepo.findByEmail(user.getEmail()) != null;
  }

  public boolean updatePassword(long userId, String oldPassword, String newPassword) {
    User user = userRepo.nonNull(userId);
    if (checkPassword(oldPassword, user.getPassword())) {
      user.setPassword(encryptPassword(newPassword));
      userRepo.update(user);
      return true;
    } else {
      return false;
    }
  }

  private boolean checkPassword(String plainPassword, String encryptedPassword) {
    return new StrongPasswordEncryptor().checkPassword(plainPassword, encryptedPassword);
  }

  private String encryptPassword(String plainPassword) {
    return new StrongPasswordEncryptor().encryptPassword(plainPassword);
  }

  public void updateUserTag(long userId, Collection<Long> tagIds) {
    Set<Long> newTagIds = new HashSet<>(tagIds);
    Set<Long> usedTagIds = userTagRepo.byUser(userId)
        .stream().map(UserTag::getTagId).collect(toSet());
    newTagIds.removeAll(usedTagIds);
    for (Long tagId : newTagIds) {
      userTagRepo.save(new UserTag(userId, tagId));
    }
  }

  @Transactional(readOnly = true)
  public Collection<UserCard> people(long selfId) {
    Collection<User> all = userRepo.all();
    all.removeIf(u -> u.getId() == selfId);
    return Colls.map(all, user -> getUserCard(selfId, user.getId()));
  }

  @Transactional(readOnly = true)
  public Collection<UserCard> recommendByTag(long selfId) {
    return recommend(selfId).stream().map(PersonValue::getId).map(personId -> getUserCard(selfId, personId))
        .collect(Collectors.toList());
  }

  List<PersonValue> recommend(long userId) {
    List<PersonValue> list = new ArrayList<>();
    List<TagLabel> selfTags = topTags(userId);
    for (long i = 1;; i++) {
      User person = userRepo.get(i);
      if (person == null) {
        break;
      }
      if (IdCommons.equal(person.getId(), userId)) {
        continue;
      }
      int value = 0;
      int j = selfTags.size();
      for (TagLabel st : selfTags) {
        List<TagLabel> personTags = topTags(i);
        int k = personTags.size();
        for (TagLabel pt : personTags) {
          if (IdCommons.equal(st.getId(), pt.getId())) {
            value += j * k;
          }
          k--;
        }
        j--;
      }
      if (value > 0) {
        list.add(new PersonValue(i, value));
      }
    }
    Collections.sort(list);
    return list;
  }

  private List<TagLabel> userTags(long userId) {
    List<TagCounter> topping = new ArrayList<>();
    for (Tweet tweet : tweetRepo.byAuthor(userId)) {
      countTags(tweet.getTags(), topping);
    }
    for (Blog blog : blogRepo.byAuthor(userId)) {
      countTags(blog.getTags(), topping);
    }
    Collections.sort(topping);

    List<TagLabel> topTags = new ArrayList<>();
    for (TagCounter topOne : topping) {
      topTags.add(new TagLabel(topOne.tag));
    }
    return topTags;
  }

  private List<TagLabel> topTags(long userId) {
    List<TagLabel> tags = userTags(userId);
    return tags.size() > 5 ? tags.subList(0, 5) : tags;
  }

  private void countTags(Collection<Tag> tags, List<TagCounter> topping) {
    for (Tag tag : tags) {
      if (tag.getId().equals(Tag.ROOT_ID)) {
        continue;
      }
      TagCounter counter = new TagCounter(tag);
      if (topping.contains(counter)) {
        topping.get(topping.indexOf(counter)).count++;
      }
      else {
        topping.add(counter);
      }
    }
  }

  private static class TagCounter implements Comparable<TagCounter> {
    Tag tag;
    int count;

    TagCounter(Tag tag) {
      this.tag = tag;
      count = 1;
    }

    @Override
    public int compareTo(TagCounter o) {
      return -(count - o.count);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof TagCounter == false) {
        return false;
      }
      return IdCommons.equal(tag.getId(), ((TagCounter) obj).tag.getId());
    }
  }

  public static class PersonValue implements Comparable<PersonValue> {
    long id;
    int value;

    PersonValue(long id, int value) {
      this.id = id;
      this.value = value;
    }

    public long getId() {return id;}
    public int getValue() {return value;}

    @Override
    public int compareTo(PersonValue o) {
      return -(value - o.value);
    }
  }
}
