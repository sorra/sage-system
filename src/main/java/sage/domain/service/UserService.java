package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.commons.IdCommons;
import sage.domain.repository.*;
import sage.entity.Blog;
import sage.entity.Tag;
import sage.entity.Tweet;
import sage.entity.User;
import sage.transfer.TagLabel;
import sage.transfer.UserCard;
import sage.transfer.UserLabel;
import sage.transfer.UserSelf;
import sage.util.Colls;

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

  @Transactional(readOnly = true)
  public UserSelf getSelf(long userId) {
    return new UserSelf(userRepo.get(userId),
        followRepo.followingCount(userId),
        followRepo.followerCount(userId),
        blogRepo.countByAuthor(userId),
        tweetRepo.countByAuthor(userId),
        topTags(userId));
  }

  @Transactional(readOnly = true)
  public UserCard getUserCard(long cuid, long userId) {
    return new UserCard(userRepo.get(userId),
        followRepo.followerCount(userId),
        blogRepo.countByAuthor(userId),
        tweetRepo.countByAuthor(userId),
        followRepo.find(cuid, userId),
        followRepo.find(userId, cuid),
        // TBD
        topTags(userId));
  }

  @Transactional(readOnly = true)
  public UserLabel getUserLabel(long userId) {
    return new UserLabel(userRepo.get(userId));
  }

  public void changeInfo(long userId, String intro, String avatar) {
    User user = userRepo.get(userId);
    if (user.getIntro().equals(intro) && avatar == null) {
      return;
    }
    user.setIntro(intro);
    user.setAvatar(avatar);
    userRepo.update(user);
  }

  public void changeIntro(long userId, String intro) {
    User user = userRepo.get(userId);
    user.setIntro(intro);
    userRepo.update(user);
  }

  public void changeAvatar(long userId, String avatar) {
    User user = userRepo.get(userId);
    user.setAvatar(avatar);
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
    user.setName("u"+user.getId());
    userRepo.update(user);
    return user.getId();
  }

  private boolean existsEmail(User user) {
    return userRepo.findByEmail(user.getEmail()) != null;
  }

  private boolean checkPassword(String plainPassword, String encryptedPassword) {
    return new StrongPasswordEncryptor().checkPassword(plainPassword, encryptedPassword);
  }

  private String encryptPassword(String plainPassword) {
    return new StrongPasswordEncryptor().encryptPassword(plainPassword);
  }

  @Transactional(readOnly = true)
  public Collection<UserCard> people(long selfId) {
    return Colls.map(userRepo.all(), user -> getUserCard(selfId, user.getId()));
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
      User person = userRepo.nullable(i);
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

  private List<TagLabel> topTags(long userId) {
    List<TagCounter> topping = new ArrayList<>();
    for (Tweet tweet : tweetRepo.byAuthor(userId)) {
      countTags(tweet.getTags(), topping);
    }
    for (Blog blog : blogRepo.byAuthor(userId)) {
      countTags(blog.getTags(), topping);
    }
    Collections.sort(topping);
    topping = topping.size() > 5 ? topping.subList(0, 5) : topping;

    List<TagLabel> topTags = new ArrayList<>();
    for (TagCounter topOne : topping) {
      topTags.add(new TagLabel(topOne.tag));
    }
    return topTags;
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
