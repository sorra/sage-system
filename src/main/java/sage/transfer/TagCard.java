package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import sage.entity.Tag;

public class TagCard {
  private long id;
  private String name;
  private boolean isCore;
  private List<TagLabel> chainUp = new ArrayList<>();
  private Collection<TagLabel> children = new HashSet<>();

  TagCard() {}
  
  public TagCard(Tag tag) {
    id = tag.getId();
    name = tag.getName();
    isCore = tag.isCore();
    
    for (Tag node : tag.chainUp()) {
      chainUp.add(new TagLabel(node));
    }
    
    for (Tag child : tag.getChildren()) {
      children.add(new TagLabel(child));
    }
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public boolean isCore() {
    return isCore;
  }

  /**
   * @see Tag#chainUp()
   */
  public List<TagLabel> getChainUp() {
    return chainUp;
  }
  
  public Collection<TagLabel> getChildren() {
    return children;
  }

  @Override
  public String toString() {
    if (chainUp.isEmpty()) {
      return name;
    }
    else {
      StringBuilder sb = new StringBuilder();
      for (int i = chainUp.size() - 1; i >= 0; i--) {
        sb.append(chainUp.get(i));
        if (i > 0)
          sb.append("->");
      }
      return sb.toString();
    }
  }
}
