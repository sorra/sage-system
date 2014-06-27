package sage.entity.nosql;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Catalog implements IdAble {
  @JsonIgnore
  private String id;
  private Long ownerId;
  private String name;

  protected Catalog() {
  }
  
  public Catalog(Long ownerId, String name) {
    this.ownerId = ownerId;
    this.name = name;
  }

  @Override
  public String getId() {
    return id;
  }
  @Override
  public void setId(String id) {
    this.id = id;
  }

  public Long getOwnerId() {
    return ownerId;
  }
  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

}