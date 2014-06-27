package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sage.domain.Comparators;
import sage.domain.Edge;
import sage.domain.repository.CommentRepository;
import sage.domain.repository.FollowRepository;
import sage.domain.repository.TagRepository;
import sage.domain.repository.TweetRepository;
import sage.entity.Comment;
import sage.entity.Follow;
import sage.entity.Tag;
import sage.entity.Tweet;
import sage.transfer.TweetCard;

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
    List<Tweet> tweets = new ArrayList<>();

    tweets.addAll(tweetRepo.byAuthor(userId, edge));

    // Find and merge tweets from followings
    List<Follow> followings = new ArrayList<>(followRepo.followings(userId));
    for (Follow follow : followings) {
      List<Tweet> result = tweetRepo.byAuthorAndTags(
          follow.getTarget().getId(), follow.getTags(), edge);
      tweets.addAll(result);
    }
    Collections.sort(tweets, Comparators.tweetOnId);

    // Select the top items, for later's higher sort
    List<Tweet> tops = (FETCH_SIZE < tweets.size())
        ? tweets.subList(0, FETCH_SIZE) : tweets;
    // How to optimize the counting inside, by Hibernate L1 cache?
    return tops;
  }

  public List<Tweet> byTag(long tagId, Edge edge) {
    Tag tag = tagRepo.load(tagId);
    return new ArrayList<>(tweetRepo.byTag(tag, edge));
  }
  
  public List<Tweet> byAuthor(long authorId, Edge edge) {
    return new ArrayList<>(tweetRepo.byAuthor(authorId, edge));
  }

  public TweetCard getTweetCard(long tweetId) {
    Tweet tweet = tweetRepo.get(tweetId);
    return tweet == null ? null : transfers.toTweetCard(tweet);
  }

  public Collection<Tweet> getForwards(long originId) {
    return new ArrayList<>(tweetRepo.byOrigin(originId));
  }

  public Collection<Comment> getComments(long sourceId) {
    return new ArrayList<>(commentRepo.bySource(sourceId));
  }

  /**
   * Experimental
   * 
   * @return a sequential list of connected tweets
   */
  public List<TweetCard> connectTweets(long blogId) {
    List<TweetCard> tcs = new ArrayList<>();
    for (Tweet tweet : tweetRepo.connectTweets(blogId)) {
      tcs.add(transfers.toTweetCard(tweet));
    }
    return tcs;
  }
}
