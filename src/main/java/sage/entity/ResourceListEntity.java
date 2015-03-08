package sage.entity;

import javax.persistence.*;

@Entity
public class ResourceListEntity implements ListEntity {
  private Long id;
  private Long ownerId;
  private String name;
  private String listJson;
  
  ResourceListEntity() {}
  
  public ResourceListEntity(Long ownerId, String name, String listJson) {
    this.ownerId = ownerId;
    this.name = name;
    this.listJson = listJson;
  }

  @Id
  @GeneratedValue
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
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

  @Lob @Column(columnDefinition = "TEXT")
  public String getListJson() {
    return listJson;
  }
  public void setListJson(String listJson) {
    this.listJson = listJson;
  }
}
