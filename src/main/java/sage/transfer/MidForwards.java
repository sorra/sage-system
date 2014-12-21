package sage.transfer;

import java.util.LinkedList;
import java.util.List;

import sage.entity.Tweet;
import sage.web.context.Json;

public class MidForwards {
  private List<Long> ids = new LinkedList<>();
  private List<String> msgs = new LinkedList<>();

  MidForwards() {}

  public MidForwards(Tweet directForward) {
    addForward(directForward);
    MidForwards formerMidForwards = directForward.midForwards();
    if (formerMidForwards != null) {
      ids.addAll(formerMidForwards.getIds());
      msgs.addAll(formerMidForwards.getMsgs());
    }
  }

  MidForwards addForward(Tweet forward) {
    Tweet t = forward;
    ids.add(t.getId());
    String asString = "@" + t.getAuthor().getName() + "#" + t.getAuthor().getId() + " : " + t.getContent();
    msgs.add(asString);
    return this;
  }

  public MidForwards removeById(Long idToRemove) {
    int idx = getIds().indexOf(idToRemove);
    getIds().remove(idx);
    getMsgs().remove(idx);
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

  public List<Long> getIds() {
    return ids;
  }
  public List<String> getMsgs() {
    return msgs;
  }
}
