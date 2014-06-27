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
import sage.transfer.TweetCard;

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

  public TweetCard toTweetCard(Tweet tweet) {
    return new TweetCard(tweet,
        forwardCount(tweet.getId()),
        commentCount(tweet.getId()));
  }

  public List<TweetCard> toTweetCards(Collection<Tweet> tweets) {
    List<TweetCard> tcs = new ArrayList<>(MIN_LIST_SIZE);
    for (Tweet t : tweets) {
      tcs.add(toTweetCard(t));
    }
    return tcs;
  }

  public TweetCard toTweetCardNoCount(Tweet tweet) {
    return new TweetCard(tweet, 0, 0);
  }

  public long forwardCount(long originId) {
    return tweetRepo.forwardCount(originId);
  }

  public long commentCount(long sourceId) {
    return commentRepo.commentCount(sourceId);
  }
}
