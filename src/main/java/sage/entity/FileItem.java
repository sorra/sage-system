package sage.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import sage.domain.commons.IdCommons;

@Entity
public class FileItem {
  private Long id;
  private String name;
  private String webPath;
  private String storePath;
  private Long ownerId;

  FileItem() {}

  public FileItem(String name, String webPath, String storePath, Long ownerId) {
    this.name = name;
    this.webPath = webPath;
    this.storePath = storePath;
    this.ownerId = ownerId;
  }

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getWebPath() {
    return webPath;
  }
  public void setWebPath(String webPath) {
    this.webPath = webPath;
  }

  public String getStorePath() {
    return storePath;
  }
  public void setStorePath(String storePath) {
    this.storePath = storePath;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FileItem fileItem = (FileItem) o;
    return IdCommons.equal(getId(), fileItem.getId());

  }

  @Override
  public int hashCode() {
    return getId() != null ? getId().hashCode() : 0;
  }
}
