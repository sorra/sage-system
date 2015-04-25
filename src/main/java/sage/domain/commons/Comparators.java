package sage.domain.commons;

import java.util.Comparator;

import sage.entity.Fav;
import sage.entity.Tweet;
import sage.transfer.TweetView;

public abstract class Comparators {
  
  /**
   * Inverse order (larger on front)
   */
  public static final Comparator<Tweet> tweetOnId = (o1, o2) -> compareId(o1.getId(), o2.getId());
  
  /**
   * Inverse order (larger on front)
   */
  public static final Comparator<TweetView> tweetViewOnId = (o1, o2) -> compareId(o1.getId(), o2.getId());
  
  /**
   * Inverse order (larger on front)
   */
  public static final Comparator<Fav> favOnId = (o1, o2) -> compareId(o1.getId(), o2.getId());

  /**
   * Inverse order (larger on front)
   */
  private static int compareId(long id1, long id2) {
    if (id1 > id2) return -1;
    else if (id1 < id2) return 1;
    else return 0;
  }
}
