package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sage.domain.repository.FollowRepository;
import sage.domain.repository.TagRepository;
import sage.domain.repository.UserRepository;
import sage.entity.Follow;
import sage.entity.User;
import sage.entity.nosql.FollowCatalogLite;
import sage.entity.nosql.FollowInfoLite;
import sage.transfer.UserLabel;

@Service
@Transactional
public class RelationService {
  private Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  UserRepository userRepo;
  @Autowired
  FollowRepository followRepo;
  @Autowired
  TagRepository tagRepo;

  /**
   * Act as 'follow' or 'editFollow'
   * @param userId The acting user
   * @param targetId The target user to follow
   * @param tagIds The tags to follow
   */
  public void follow(long userId, long targetId, Collection<Long> tagIds) {
    if (userId == targetId) {
      logger.warn("user {} should not follow himself!", userId);
      return;
    }
    Follow follow = followRepo.find(userId, targetId);
    if (follow == null) {
      follow = new Follow(userRepo.load(userId), userRepo.load(targetId), tagRepo.byIds(tagIds));
      followRepo.save(follow);
    } else {
      follow.setTags(tagRepo.byIds(tagIds));
      followRepo.merge(follow);
    }
  }

  public void unfollow(long userId, long targetId) {
    Follow follow = followRepo.find(userId, targetId);
    if (follow != null) {
      followRepo.delete(follow);
    } else {
      logger.warn("user {} should not unfollow duplicately!", userId);
    }
  }
  
  public void applyFollows(Long userId, FollowCatalogLite fcl) {
    for (FollowInfoLite info : fcl.getList()) {
      follow(userId, info.getUserId(), info.getTagIds());
    }
  }

  /*
   * Optional method for 'editFollow'
   */
  @Deprecated
  public void editFollow(long followId, Collection<Long> tagIds) {
    Follow follow = followRepo.get(followId);
    follow.setTags(tagRepo.byIds(tagIds));
    followRepo.merge(follow);
  }

  @Transactional(readOnly = true)
  public Follow getFollow(long sourceId, long targetId) {
    return followRepo.find(sourceId, targetId);
  }

  @Transactional(readOnly = true)
  public Collection<Follow> followings(long userId) {
    return new ArrayList<>(followRepo.followings(userId));
  }

  @Transactional(readOnly = true)
  public Collection<Follow> followers(long userId) {
    return new ArrayList<>(followRepo.followers(userId));
  }

  @Transactional(readOnly = true)
  public Collection<UserLabel> friends(long userId) {
    final List<User> followingUsers = new ArrayList<>();
    for (Follow f : followings(userId)) {
      followingUsers.add(f.getTarget());
    }

    List<User> followerUsers = new ArrayList<>();
    for (Follow f : followers(userId)) {
      followerUsers.add(f.getSource());
    }

    followingUsers.retainAll(followerUsers);
    final List<User> friends = followingUsers;

    return UserLabel.listOf(friends);
  }
}
