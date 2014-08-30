package sage.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public interface CatalogEntity {

  Long getId();
  void setId(Long id);
  Long getOwnerId();
  void setOwnerId(Long ownerId);
  String getName();
  void setName(String name);

}