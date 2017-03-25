package sage.transfer;

public class TagLabel {
  private long id;
  private String name;
  private boolean isCore;
  private String chainStr;

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

  public boolean isCore() {
    return isCore;
  }

  public void setCore(boolean core) {
    isCore = core;
  }

  public String getChainStr() {
    return chainStr;
  }

  public void setChainStr(String chainStr) {
    this.chainStr = chainStr;
  }

  @Override
  public String toString() {
    return "TagLabel{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", isCore=" + isCore +
        ", chainStr='" + chainStr + '\'' +
        '}';
  }
}