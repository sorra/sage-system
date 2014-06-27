package sage.domain;

import java.util.Comparator;

import sage.entity.Fav;
import sage.entity.Tweet;
import sage.transfer.TweetCard;

public abstract class Comparators {
  
  /**
   * Inverse order (larger on front)
   */
  public static final Comparator<Tweet> tweetOnId = new Comparator<Tweet>() {
    @Override
    public int compare(Tweet o1, Tweet o2) {
      return compareId(o1.getId(), o2.getId());
    }
  };
  
  /**
   * Inverse order (larger on front)
   */
  public static final Comparator<TweetCard> tweetCardOnId = new Comparator<TweetCard>() {
    @Override
    public int compare(TweetCard o1, TweetCard o2) {
      return compareId(o1.getId(), o2.getId());
    }
  };
  
  /**
   * Inverse order (larger on front)
   */
  public static final Comparator<Fav> favOnId = new Comparator<Fav>() {
    @Override
    public int compare(Fav o1, Fav o2) {
      return compareId(o1.getId(), o2.getId());
    }
  };

  /**
   * Inverse order (larger on front)
   */
  private static int compareId(long id1, long id2) {
    if (id1 > id2) return -1;
    else if (id1 < id2) return 1;
    else return 0;
  }
}
