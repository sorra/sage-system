package sage.transfer;

public abstract class Catalog {
  private Long id;
  private Long ownerId;
  private String name;

  protected Catalog() {}

  protected Catalog(Long id, Long ownerId, String name) {
    this.id = id;
    this.ownerId = ownerId;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public String getName() {
    return name;
  }
}
