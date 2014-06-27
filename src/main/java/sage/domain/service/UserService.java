package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sage.domain.repository.BlogRepository;
import sage.domain.repository.FollowRepository;
import sage.domain.repository.TagRepository;
import sage.domain.repository.TweetRepository;
import sage.domain.repository.UserRepository;
import sage.entity.Blog;
import sage.entity.Tag;
import sage.entity.Tweet;
import sage.entity.User;
import sage.transfer.TagLabel;
import sage.transfer.UserCard;
import sage.transfer.UserSelf;

@Service
@Transactional(readOnly = true)
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

  public UserSelf getSelf(long userId) {
    return new UserSelf(userRepo.get(userId),
        followRepo.followingCount(userId),
        followRepo.followerCount(userId),
        blogRepo.countByAuthor(userId),
        tweetRepo.countByAuthor(userId),
        topTags(userId));
  }

  public UserCard getUserCard(long selfId, long userId) {
    User user = userRepo.get(userId);
    if (user == null) {
      return null;
    }
    return new UserCard(user,
        followRepo.followerCount(userId),
        blogRepo.countByAuthor(userId),
        tweetRepo.countByAuthor(userId),
        followRepo.find(selfId, userId),
        followRepo.find(userId, selfId),
        // TBD
        topTags(userId));
  }

  public User login(String email, String password) {
    User user = userRepo.findByEmail(email);
    if (user == null) {
      return null;
    }
    if (checkPassword(password, user.getPassword())) {
      return user;
    }
    else
      return null;
  }

  @Transactional(readOnly = false)
  public long register(User user) {
    if (existsEmail(user)) {
      return -1;
    }
    // XXX existsName?

    user.setPassword(encryptPassword(user.getPassword()));
    userRepo.save(user);
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

  public List<PersonValue> recommend(long userId) {
    List<PersonValue> list = new ArrayList<>();
    List<TagLabel> selfTags = topTags(userId);
    for (long i = 1;; i++) {
      User person = userRepo.get(i);
      if (person == null) {
        break;
      }
      if (person.getId() == userId) {
        continue;
      }
      int value = 0;
      int j = selfTags.size();
      for (TagLabel st : selfTags) {
        List<TagLabel> personTags = topTags(i);
        int k = personTags.size();
        for (TagLabel pt : personTags) {
          if (st.getId() == pt.getId()) {
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
      if (tag.getId() == Tag.ROOT_ID) {
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
      return tag.getId() == ((TagCounter) obj).tag.getId();
    }
  }

  public static class PersonValue implements Comparable<PersonValue> {
    long id;
    int value;

    PersonValue(long id, int value) {
      this.id = id;
      this.value = value;
    }

    @Override
    public int compareTo(PersonValue o) {
      return -(value - o.value);
    }
  }
}
