package sage.entity;

public interface ListEntity {

  Long getId();
  void setId(Long id);
  Long getOwnerId();
  void setOwnerId(Long ownerId);
  String getName();
  void setName(String name);

}