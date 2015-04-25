package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.repository.CommentRepository;
import sage.domain.repository.TweetRepository;
import sage.domain.repository.UserRepository;
import sage.entity.Tweet;
import sage.transfer.TweetView;

@Service
@Transactional(readOnly = true)
public class TransferService {
  @Autowired
  private UserService userService;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private TweetRepository tweetRepo;
  @Autowired
  private CommentRepository commentRepo;

  private static final int MIN_LIST_SIZE = 20;

  public TweetView toTweetView(Tweet tweet) {
    return new TweetView(tweet, tweetRepo.getOrigin(tweet),
        forwardCount(tweet.getId()),
        commentCount(tweet.getId()));
  }

  public TweetView toTweetViewNoCount(Tweet tweet) {
    return new TweetView(tweet, tweetRepo.getOrigin(tweet), 0, 0);
  }

  public List<TweetView> toTweetViews(Collection<Tweet> tweets, boolean showOrigin, boolean showCounts) {
    List<TweetView> tcs = new ArrayList<>(MIN_LIST_SIZE);
    for (Tweet t : tweets) {
      Tweet origin = showOrigin ? tweetRepo.getOrigin(t) : null;
      long forwardCount = showCounts ? forwardCount(t.getId()) : 0;
      long commentCount = showCounts ? commentCount(t.getId()) : 0;
      tcs.add(new TweetView(t, origin, forwardCount, commentCount));
    }
    return tcs;
  }

  public List<TweetView> toTweetViews(Collection<Tweet> tweets) {
    return toTweetViews(tweets, true, true);
  }

  public long forwardCount(long originId) {
    return tweetRepo.forwardCount(originId);
  }

  public long commentCount(long sourceId) {
    return commentRepo.commentCount(sourceId);
  }
}
