package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import sage.entity.Tag;
import sage.util.Colls;

public class TagLabel {
  private long id;
  private String name;
  private boolean isCore;
  private String chainStr;

  TagLabel() {}
  
  public TagLabel(Tag tag) {
    id = tag.getId();
    name = tag.getName();
    isCore = tag.isCore();
    chainStr = topDownChainStr(Colls.map(tag.chainUp(), Tag::getName));
  }

  public TagLabel(TagCard tagCard) {
    id = tagCard.getId();
    name = tagCard.getName();
    isCore = tagCard.isCore();
    chainStr = topDownChainStr(Colls.map(tagCard.getChainUp(), TagLabel::getName));
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public boolean getIsCore() {
    return isCore;
  }

  public String getChainStr() {
    return chainStr;
  }

  @Override
  public String toString() {
    return String.format("%s[id=%s, name=%s]", getClass().getSimpleName(), id, name);
  }
  
  public static List<TagLabel> listOf(Collection<Tag> tags) {
    List<TagLabel> labels = new ArrayList<>();
    for (Tag tag : tags) {
      labels.add(new TagLabel(tag));
    }
    return labels;
  }

  public static String topDownChainStr(List<String> namesBottomUp) {
    if (namesBottomUp.isEmpty()) return "";
    StringBuilder sb = new StringBuilder();
    for (int i = namesBottomUp.size() - 1; i >= 0; i--) {
      sb.append(namesBottomUp.get(i));
      if (i > 0)
        sb.append("->");
    }
    return sb.toString();
  }
}
