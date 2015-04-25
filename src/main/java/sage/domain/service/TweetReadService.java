package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.commons.Comparators;
import sage.domain.commons.Edge;
import sage.domain.repository.CommentRepository;
import sage.domain.repository.FollowRepository;
import sage.domain.repository.TagRepository;
import sage.domain.repository.TweetRepository;
import sage.entity.Comment;
import sage.entity.Follow;
import sage.entity.Tweet;
import sage.transfer.FollowListLite;
import sage.transfer.TweetView;
import sage.util.Colls;

@Service
@Transactional(readOnly = true)
public class TweetReadService {
  private static final int FETCH_SIZE = 20;

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

  public List<Tweet> byFollowings(long userId, Edge edge) {
    List<Tweet> tweets = Colls.copy(tweetRepo.byAuthor(userId, edge));

    // Find and merge tweets from followings
    List<Follow> followings = new ArrayList<>(followRepo.followings(userId));
    followings.forEach(f -> tweets.addAll(byFollow(f, edge)));
    Collections.sort(tweets, Comparators.tweetOnId);

    // Select the top items, for later's higher sort
    List<Tweet> tops = (FETCH_SIZE < tweets.size())
        ? tweets.subList(0, FETCH_SIZE) : tweets;
    // How to optimize the counting inside, by Hibernate L1 cache?
    return tops;
  }

  private List<Tweet> byFollow(Follow follow, Edge edge) {
    long authorId = follow.getTarget().getId();
    List<Tweet> result;
    if (follow.isIncludeAll()) {
      result = tweetRepo.byAuthor(authorId, edge);
    } else if (follow.isIncludeNew()) {
      result = tweetRepo.byAuthorAndTags(authorId, follow.getTags(), edge);
    } else {
      result = tweetRepo.byAuthorAndTags(authorId, follow.getTags(), edge);
    }
    return result;
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
    Tweet tweet = tweetRepo.nullable(tweetId);
    return tweet == null ? null : transfers.toTweetView(tweet);
  }

  public Collection<Tweet> getForwards(long originId) {
    return Colls.copy(tweetRepo.byOrigin(originId));
  }

  public Collection<Comment> getComments(long sourceId) {
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
