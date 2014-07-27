package sage.transfer;

import sage.entity.Tweet;
import sage.web.context.Json;

import java.util.LinkedList;
import java.util.List;

public class MidForwards {
  private List<Long> forwardIds = new LinkedList<>();
  private List<String> forwardMsgs = new LinkedList<>();

  MidForwards() {}

  public MidForwards(Tweet directForward) {
    addForward(directForward);
    MidForwards formerMidForwards = directForward.midForwards();
    if (formerMidForwards != null) {
      forwardIds.addAll(formerMidForwards.getForwardIds());
      forwardMsgs.addAll(formerMidForwards.getForwardMsgs());
    }
  }

  MidForwards addForward(Tweet forward) {
    Tweet t = forward;
    forwardIds.add(t.getId());
    String asString = " ||@" + t.getAuthor().getName() + "#" + t.getAuthor().getId() + " : " + t.getContent();
    forwardMsgs.add(asString);
    return this;
  }

  public MidForwards removeById(Long idToRemove) {
    int idx = getForwardIds().indexOf(idToRemove);
    getForwardIds().remove(idx);
    getForwardMsgs().remove(idx);
    return this;
  }

  public static MidForwards from(Tweet tweet) {
    return Json.object(tweet.getMidForwardsJson(), MidForwards.class);
  }

  public static MidForwards fromJson(String json) {
    return Json.object(json, MidForwards.class);
  }

  public String toJson() {
    return Json.json(this);
  }

  public List<Long> getForwardIds() {
    return forwardIds;
  }
  public List<String> getForwardMsgs() {
    return forwardMsgs;
  }
}
