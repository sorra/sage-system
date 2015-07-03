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

  TagCard() {}
  
  public TagCard(Tag tag) {
    id = tag.getId();
    name = tag.getName();
    intro = tag.getIntro();
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

  public String getIntro() {
    return intro;
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
    return ToStringBuilder.reflectionToString(this);
  }
}
