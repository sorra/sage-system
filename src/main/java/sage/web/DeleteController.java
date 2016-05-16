package sage.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sage.service.BlogPostService;
import sage.service.TweetPostService;
import sage.web.auth.Auth;

@RestController
@RequestMapping(method = RequestMethod.POST)
public class DeleteController {
  private static final Logger logger = LoggerFactory.getLogger(DeleteController.class);
  @Autowired
  private TweetPostService tweetPostService;
  @Autowired
  private BlogPostService blogPostService;

  @RequestMapping("/tweet/{id}/delete")
  public boolean deleteTweet(@PathVariable Long id) {
    long uid = Auth.checkCuid();
    tweetPostService.delete(uid, id);
    logger.debug("delete tweet {}", id);
    return true;
  }

  @RequestMapping("/blog/{id}/delete")
  public boolean deleteBlog(@PathVariable Long id) {
    long uid = Auth.checkCuid();
    blogPostService.delete(uid, id);
    logger.debug("delete blog {}", id);
    return true;
  }
}
