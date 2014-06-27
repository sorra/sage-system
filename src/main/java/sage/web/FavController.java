package sage.web;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sage.domain.service.FavService;
import sage.transfer.FavInfo;
import sage.web.auth.AuthUtil;

@Controller
@RequestMapping("/favs")
public class FavController {
  public final String TWEET_PR = "tweet:";
  
  @Autowired
  private FavService favService;
  
  @RequestMapping(value="/add", method=RequestMethod.POST)
  public void addFav(@RequestParam(value = "link", required = false) String link,
      @RequestParam(value = "tweetId", required = false) Long tweetId) {
    Long uid = AuthUtil.checkCurrentUid();

    if (link != null && tweetId == null) {
      favService.addFav(uid, link);
    } else if (tweetId != null && link == null) {
      favService.addFav(uid, TWEET_PR + tweetId);
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  @RequestMapping(value="/{favId}/delete", method=RequestMethod.POST)
  public boolean deleteFav(@PathVariable("favId") Long favId) {
    Long uid =AuthUtil.checkCurrentUid();
    
    return favService.deleteFav(uid, favId);
  }
  
  @RequestMapping(value="/get")
  public Collection<FavInfo> favs() {
    Long uid =AuthUtil.checkCurrentUid();
    
    return FavInfo.listOf(favService.favs(uid));
  }
}
