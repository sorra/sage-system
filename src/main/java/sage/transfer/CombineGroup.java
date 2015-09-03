package sage.transfer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

public class CombineGroup implements Item {
  private final String type = "CombineGroup";

  private List<TweetView> forwards = new ArrayList<>();
  private TweetView origin;
  /** origin是否被信息流直接收听到了 */
  private boolean containsOrigin = false;

  CombineGroup() {}
  
  public static CombineGroup newByFirst(TweetView first) {
    CombineGroup group = new CombineGroup();
    group.origin = first.getOrigin();
    group.containsOrigin = false;
    group.addForward(first);
    Assert.notNull(group.origin);
    return group;
  }

  public static CombineGroup newByOrigin(TweetView origin) {
    CombineGroup group = new CombineGroup();
    group.addOrigin(origin);
    Assert.notNull(group.origin);
    return group;
  }

  /**
   * clears forward's origin
   * 
   * @param forward
   */
  public void addForward(TweetView forward) {
    forward.clearOrigin();
    forwards.add(forward);
  }

  public void addOrigin(TweetView origin) {
    this.origin = origin;
    containsOrigin = true;
  }

  public TweetView singleMember() {
    if (containsOrigin && forwards.size() == 0) {
      return origin;
    }
    else if (!containsOrigin & forwards.size() == 1) {
      return forwards.get(0);
    }
    else
      return null;
  }

  public List<TweetView> getForwards() {
    return forwards;
  }

  @Override
  public TweetView getOrigin() {
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
    for (TweetView forward : forwards) {
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
