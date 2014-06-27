package sage.transfer;

import java.util.ArrayList;
import java.util.List;

public class CombineGroup implements Item {
  private final String type = "CombineGroup";

  private List<TweetCard> forwards = new ArrayList<>();
  private TweetCard origin;
  private boolean containsOrigin = false;

  CombineGroup() {}
  
  public static CombineGroup newByFirst(TweetCard first) {
    CombineGroup group = new CombineGroup();
    group.origin = first.getOrigin();
    group.containsOrigin = false;

    group.addForward(first);
    return group;
  }

  public static CombineGroup newByOrigin(TweetCard origin) {
    CombineGroup group = new CombineGroup();
    group.addOrigin(origin);
    return group;
  }

  /**
   * clears forward's origin
   * 
   * @param forward
   */
  public void addForward(TweetCard forward) {
    forward.clearOrigin();
    forwards.add(forward);
  }

  public void addOrigin(TweetCard origin) {
    this.origin = origin;
    containsOrigin = true;
  }

  public TweetCard singleMember() {
    if (containsOrigin && forwards.size() == 0) {
      return origin;
    }
    else if (!containsOrigin & forwards.size() == 1) {
      return forwards.get(0);
    }
    else
      return null;
  }

  public List<TweetCard> getForwards() {
    return forwards;
  }

  @Override
  public TweetCard getOrigin() {
    return origin;
  }

  public boolean isContainsOrigin() {
    return containsOrigin;
  }

  @Override
  public List<TagLabel> getTags() {
    return origin.getTags();
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("CombineGroup {\n");
    for (TweetCard forward : forwards) {
      sb.append(forward).append('\n');
    }
    sb.append("<origin>").append(origin).append("\n");

    if (containsOrigin)
      sb.append("\"contains origin\"\n");
    else
      sb.append("\"not contains origin\"\n");

    sb.append('}');
    return sb.toString();
  }
}
