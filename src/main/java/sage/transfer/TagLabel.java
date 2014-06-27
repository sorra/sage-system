package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sage.entity.Tag;

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

    List<Tag> chainUp = tag.chainUp();
    if (chainUp.isEmpty()) {
      chainStr = "";
    }
    else {
      StringBuilder sb = new StringBuilder();
      for (int i = chainUp.size() - 1; i >= 0; i--) {
        sb.append(chainUp.get(i));
        if (i > 0)
          sb.append("->");
      }
      chainStr = sb.toString();
    }
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
    return name;
  }
  
  public static List<TagLabel> listOf(Collection<Tag> tags) {
    List<TagLabel> labels = new ArrayList<>();
    for (Tag tag : tags) {
      labels.add(new TagLabel(tag));
    }
    return labels;
  }
}
