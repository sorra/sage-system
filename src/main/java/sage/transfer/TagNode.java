package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;

import sage.entity.Tag;

public class TagNode {
  private Long id;
  private String name;
  private boolean isCore;
  private Collection<TagNode> children = new ArrayList<>();

  TagNode() {}
  
  public TagNode(Tag tag) {
    id = tag.getId();
    name = tag.getName();
    isCore = tag.isCore();
    for (Tag child : tag.getChildren()) {
      children.add(new TagNode(child));
    }
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public boolean isCore() {
    return isCore;
  }

  public Collection<TagNode> getChildren() {
    return children;
  }
}
