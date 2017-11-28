package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;

public class TagNode {
  private Long id;
  private String name;
  private boolean isCore;
  private Collection<TagNode> children = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isCore() {
    return isCore;
  }

  public void setCore(boolean core) {
    isCore = core;
  }

  public Collection<TagNode> getChildren() {
    return children;
  }

  public void setChildren(Collection<TagNode> children) {
    this.children = children;
  }
}
