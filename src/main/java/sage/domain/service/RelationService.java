package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.commons.IdCommons;
import sage.domain.repository.FollowRepository;
import sage.domain.repository.TagRepository;
import sage.domain.repository.UserRepository;
import sage.domain.repository.UserTagRepository;
import sage.entity.Follow;
import sage.entity.Tag;
import sage.entity.User;
import sage.transfer.UserLabel;
import sage.util.Colls;

@Service
@Transactional
public class RelationService {
  private Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private UserService userService;
  @Autowired
  private NotifService notifService;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private FollowRepository followRepo;
  @Autowired
  private TagRepository tagRepo;
  @Autowired
  private UserTagRepository userTagRepo;

  /**
   * Act as 'follow' or 'editFollow'
   * @param userId The acting user
   * @param targetId The target user to follow
   * @param reason The reason of following
   * @param tagIds The tags to follow
   * @param includeNew If auto-include new tags
   * @param includeAll If include all tags, ignoring selected tags
   * @param userTagOffset The offset for includeNew. Fetch from DB if null
   */
  public void follow(long userId, long targetId, String reason, Collection<Long> tagIds,
                     boolean includeNew, boolean includeAll, Long userTagOffset) {
    if (IdCommons.equal(userId, targetId)) {
      logger.warn("user {} should not follow himself!", userId);
      return;
    }
    Follow follow = followRepo.find(userId, targetId);
    Set<Tag> followedTags = tagRepo.byIds(tagIds);

    if (userTagOffset == null) {
      userTagOffset = userTagRepo.latestIdByUser(targetId);
    }

    if (follow == null) {
      follow = new Follow(userRepo.load(userId), userRepo.load(targetId),
          reason, followedTags, includeNew, includeAll, userTagOffset);
      followRepo.save(follow);
      notifService.followed(targetId, userId);
    } else {
      follow.setTags(followedTags);
      follow.setReason(reason);
      follow.setIncludeNew(includeNew);
      follow.setIncludeAll(includeAll);
      follow.setUserTagOffset(userTagOffset);
      followRepo.update(follow);
    }
  }

  public void follow(long userId, long targetId, Collection<Long> tagIds) {
    follow(userId, targetId, null, tagIds, true, false, 0L);
  }

  public void unfollow(long userId, long targetId) {
    Follow follow = followRepo.find(userId, targetId);
    if (follow != null) {
      followRepo.delete(follow);
    } else {
      logger.warn("user {} should not unfollow duplicately!", userId);
    }
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
    final List<User> followingUsers = Colls.map(followings(userId), Follow::getTarget);
    final List<User> followerUsers = Colls.map(followers(userId), Follow::getSource);

    followingUsers.retainAll(followerUsers);

    return UserLabel.listOf(followingUsers);
  }
}
