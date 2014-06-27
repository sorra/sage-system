package sage.entity.nosql;

public class ResourceInfo {
  private String link;
  private String desc;
  
  ResourceInfo() {}
  
  public ResourceInfo(String link, String desc) {
    this.link = link;
    this.desc = desc;
  }
  
  public String getLink() {
    return link;
  }
  public String getDesc() {
    return desc;
  }
}
