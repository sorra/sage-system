package sage.transfer;

public abstract class AList {
  private Long id;
  private Long ownerId;
  private String name;

  protected AList() {}

  protected AList(Long id, Long ownerId, String name) {
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
  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public String getName() {
    return name;
  }
}
