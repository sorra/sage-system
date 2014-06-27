package sage.transfer;

import java.util.List;

public interface Item {
  String getType();

  List<TagLabel> getTags();

  TweetCard getOrigin();
}
