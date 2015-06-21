package sage.domain.commons;

import java.util.Comparator;

import sage.entity.Fav;
import sage.entity.Tweet;
import sage.transfer.TweetView;

public abstract class Comparators {

  public static final Comparator<Tweet> tweetNewerFirst =
      (o1, o2) -> -(o1.getTimeMillis().compareTo(o2.getTimeMillis()));

  public static final Comparator<TweetView> tweetViewNewerFirst =
      (o1, o2) -> -(o1.getTimeMillis().compareTo(o2.getTimeMillis()));

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
