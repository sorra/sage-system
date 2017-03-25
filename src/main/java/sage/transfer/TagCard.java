package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import sage.entity.Tag;

public class TagCard {
  private long id;
  private String name;
  private String intro;
  private boolean isCore;
  private List<TagLabel> chainUp = new ArrayList<>();
  private Collection<TagLabel> children = new HashSet<>();

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIntro() {
    return intro;
  }

  public void setIntro(String intro) {
    this.intro = intro;
  }

  public boolean isCore() {
    return isCore;
  }

  public void setCore(boolean core) {
    isCore = core;
  }

  /**
   * @see Tag#chainUp()
   */
  public List<TagLabel> getChainUp() {
    return chainUp;
  }

  public void setChainUp(List<TagLabel> chainUp) {
    this.chainUp = chainUp;
  }

  public Collection<TagLabel> getChildren() {
    return children;
  }

  public void setChildren(Collection<TagLabel> children) {
    this.children = children;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
