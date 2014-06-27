package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import sage.entity.Fav;

public class FavInfo {
  private Long id;
  private String link;
  private Long ownerId;
  private Date time;
  
  FavInfo() {}
  
  public FavInfo(Fav fav) {
    id = fav.getId();
    link = fav.getLink();
    ownerId = fav.getOwner().getId();
    time = fav.getTime();
  }
  
  public Long getId() {
    return id;
  }
  public String getLink() {
    return link;
  }
  public Long getOwnerId() {
    return ownerId;
  }
  public Date getTime() {
    return time;
  }
  
  public static List<FavInfo> listOf(Collection<Fav> favs) {
    List<FavInfo> infos = new ArrayList<>();
    for (Fav fav : favs) {
      infos.add(new FavInfo(fav));
    }
    return infos;
  }
}
