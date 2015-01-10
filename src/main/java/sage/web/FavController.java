package sage.web;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.FavService;
import sage.transfer.FavInfo;
import sage.web.auth.Auth;

@RestController
@RequestMapping("/favs")
public class FavController {
  
  @Autowired
  private FavService favService;
  
  @RequestMapping(value="/add", method=RequestMethod.POST)
  public void addFav(@RequestParam(required = false) String link, @RequestParam(required = false) Long tweetId) {
    Long uid = Auth.checkCuid();

    if (link != null && tweetId == null) {
      favService.addFav(uid, link);
    } else if (tweetId != null && link == null) {
      favService.addFav(uid, FavInfo.TWEET_PR + tweetId);
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  @RequestMapping(value="/{favId}/delete", method=RequestMethod.POST)
  public boolean deleteFav(@PathVariable Long favId) {
    Long uid = Auth.checkCuid();
    
    favService.deleteFav(uid, favId);
    return true;
  }
  
  @RequestMapping(value="/get")
  public Collection<FavInfo> favs() {
    Long uid = Auth.checkCuid();
    
    return favService.favs(uid);
  }
}
