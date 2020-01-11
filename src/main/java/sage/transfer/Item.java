package sage.transfer;

import java.util.Collection;

public interface Item {
  String getType();

  Collection<TagLabel> getTags();

  TweetView getOrigin();
}
