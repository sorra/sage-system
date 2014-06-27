package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import sage.domain.service.BlogPostService;
import sage.domain.service.TweetPostService;
import sage.web.auth.AuthUtil;

@Controller
@RequestMapping(value = "/", method = RequestMethod.POST)
public class DeleteController {
  @Autowired
  private TweetPostService tweetPostService;
  @Autowired
  private BlogPostService blogPostService;

  @RequestMapping("/tweet/{id}/delete")
  @ResponseBody
  public void deleteTweet(@PathVariable("id") Long id) {
    long uid = AuthUtil.checkCurrentUid();
    tweetPostService.delete(uid, id);
  }

  @RequestMapping("/blog/{id}/delete")
  @ResponseBody
  public void deleteBlog(@PathVariable("id") Long id) {
    long uid = AuthUtil.checkCurrentUid();
    blogPostService.delete(uid, id);
  }
}
