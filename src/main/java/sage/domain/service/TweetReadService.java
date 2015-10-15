package sage.domain.service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import sage.domain.commons.Comparators;
import sage.domain.commons.Edge;
import sage.domain.repository.*;
import sage.entity.*;
import sage.transfer.FollowListLite;
import sage.transfer.TweetView;
import sage.util.Colls;

import static java.util.stream.Collectors.toSet;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class TweetReadService {

  private static final Logger log = LoggerFactory.getLogger(TweetReadService.class);

  @Autowired
  private TransferService transfers;
  @Autowired
  private TweetRepository tweetRepo;
  @Autowired
  private TagRepository tagRepo;
  @Autowired
  private FollowRepository followRepo;
  @Autowired
  private CommentRepository commentRepo;
  @Autowired
  private UserTagRepository userTagRepo;
  @Autowired
  private TransactionTemplate transactionTemplate;

  private ExecutorService asyncTaskPool = Executors.newFixedThreadPool(4);

  public List<Tweet> byFollowings(long userId, Edge edge) {
    List<Tweet> tweets = Colls.copy(tweetRepo.byAuthor(userId, edge));

    // Find and merge tweets from followings
    List<Follow> followings = new ArrayList<>(followRepo.followings(userId));
    followings.forEach(f -> tweets.addAll(byFollow(f, edge)));
    Collections.sort(tweets, Comparators.tweetNewerFirst);

    // Select the top items, for later's higher sort
    List<Tweet> tops = Colls.limitList(tweets, Edge.FETCH_SIZE);
    // How to optimize the counting inside, by Hibernate L1 cache?
    return tops;
  }

  private List<Tweet> byFollow(Follow follow, Edge edge) {
    long authorId = follow.getTarget().getId();
    List<Tweet> result;
    if (follow.isIncludeAll()) {
      result = tweetRepo.byAuthor(authorId, edge);
    } else if (follow.isIncludeNew()) {
      updateOffsetIfNeeded(follow);
      result = tweetRepo.byAuthorAndTags(authorId, follow.getTags(), edge);
    } else {
      result = tweetRepo.byAuthorAndTags(authorId, follow.getTags(), edge);
    }
    return result;
  }

  private void updateOffsetIfNeeded(Follow follow) {
    try {
      long newOffset = userTagRepo.latestIdByUser(follow.getTarget().getId());
      Long oldOffset = follow.getUserTagOffset();
      if (oldOffset == null || oldOffset < newOffset) {
        Set<Tag> coveredTags = TagRepository.getQueryTags(follow.getTags());
        Set<Tag> newTags = tagRepo.byIds(Colls.map(
            userTagRepo.byUserAndAfterId(follow.getTarget().getId(), oldOffset), UserTag::getTagId));

        Set<Tag> pureNewTags = newTags.stream().filter(t -> !coveredTags.contains(t)).collect(toSet());
        if (pureNewTags.size() > 0) {
          follow.getTags().addAll(pureNewTags);
          follow.setUserTagOffset(newOffset);
          Follow entityCopy = Follow.copy(follow);
          asyncTaskPool.submit(() -> {
            try {
//              Tx.apply(() -> followRepo.update(follow));
              transactionTemplate.execute(status -> {
                followRepo.update(entityCopy);
                return null;
              });
            } catch (Exception e) {
              log.error("updateOffsetIfNeeded tx fails!", e);
            }
          });
        }
      }
    } catch (Exception e) {
      log.error("updateOffsetIfNeeded encounters error, but we skip.", e);
    }
  }

  public List<Tweet> byFollowListLite(FollowListLite list, Edge edge) {
    return Colls.flatMap(list.getList(),
        info -> tweetRepo.byAuthorAndTags(info.getUserId(), tagRepo.byIds(info.getTagIds()), edge));
  }

  public List<Tweet> byTag(long tagId, Edge edge) {
    return Colls.copy(tweetRepo.byTags(Collections.singleton(tagRepo.load(tagId)), edge));
  }
  
  public List<Tweet> byAuthor(long authorId, Edge edge) {
    return Colls.copy(tweetRepo.byAuthor(authorId, edge));
  }

  public TweetView getTweetView(long tweetId) {
    Tweet tweet = tweetRepo.get(tweetId);
    return tweet == null ? null : transfers.toTweetView(tweet);
  }

  public Collection<Tweet> getForwards(long originId) {
    Tweet tweet = tweetRepo.get(originId);
    if (tweet == null || tweet.isDeleted()) {
      return Collections.emptyList();
    }
    return Colls.copy(tweetRepo.byOrigin(originId));
  }

  public Collection<Comment> getComments(long sourceId) {
    Tweet tweet = tweetRepo.get(sourceId);
    if (tweet == null || tweet.isDeleted()) {
      return Collections.emptyList();
    }
    return Colls.copy(commentRepo.bySource(sourceId));
  }

  /**
   * Experimental
   * 
   * @return a sequential list of connected tweets
   */
  public List<TweetView> connectTweets(long blogId) {
    return Colls.map(tweetRepo.connectTweets(blogId), transfers::toTweetView);
  }
}
