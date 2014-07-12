package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import sage.entity.Fav;
import sage.entity.Tweet;

public class FavInfo {
  public static final String TWEET_PR = "tweet:";

  private Long id;
  private String link;
  private TweetCard tweet = null;
  private Long ownerId;
  private Date time;
  
  FavInfo() {}

  /**
   *  Constructor. Transaction required
   * @param fav the fav entity
   * @param fetchTweet a function to get tweetCard by id. Transactional
   */
  public FavInfo(Fav fav, Function<Long, TweetCard> fetchTweet) {
    id = fav.getId();
    link = fav.getLink();
    if (link.startsWith(TWEET_PR)) {
      Long tweetId = Long.parseLong(link.replace(TWEET_PR, ""));
      tweet = fetchTweet.apply(tweetId);
    }
    ownerId = fav.getOwner().getId();
    time = fav.getTime();
  }
  
  public Long getId() {
    return id;
  }
  public String getLink() {
    return link;
  }
  public TweetCard getTweet() {
    return tweet;
  }
  public Long getOwnerId() {
    return ownerId;
  }
  public Date getTime() {
    return time;
  }

  /**
   * Transform the collection of Fav to FavInfo.
   * @param favs the fav entities
   * @param fetchTweet a function to get tweetCard by id. Transactional
   * @return transformed list
   */
  public static List<FavInfo> listOf(Collection<Fav> favs, Function<Long, TweetCard> fetchTweet) {
    List<FavInfo> infos = new ArrayList<>();
    for (Fav fav : favs) {
      infos.add(new FavInfo(fav, fetchTweet));
    }
    return infos;
  }
}
