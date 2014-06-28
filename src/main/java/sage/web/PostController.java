package sage.web;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import sage.domain.service.BlogPostService;
import sage.domain.service.TweetPostService;
import sage.entity.Blog;
import sage.entity.Tweet;
import sage.web.auth.AuthUtil;

@RestController
@RequestMapping(value = "/post", method = RequestMethod.POST)
public class PostController {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private TweetPostService tweetPostService;
  @Autowired
  private BlogPostService blogService;

  @RequestMapping("/tweet")
  public boolean tweet(
      @RequestParam String content,
      @RequestParam(value = "attachmentRefs[]", defaultValue = "") Collection<String> attachmentRefs,
      @RequestParam(value = "tagIds[]", defaultValue = "") Collection<Long> tagIds) {
    Long uid = AuthUtil.checkCurrentUid();
    if (content.isEmpty()) {
      return false;
    }
    if (content.length() > 2000) {
      return false;
    }

    //TODO Process attachments
    Tweet tweet = tweetPostService.newTweet(uid, content, tagIds);
    logger.info("post tweet {} success", tweet.getId());
    return true;
  }

  @RequestMapping("/forward")
  public boolean forward(
      @RequestParam String content,
      @RequestParam Long originId) {
    Long uid = AuthUtil.checkCurrentUid();

    Tweet tweet = tweetPostService.forward(uid, content, originId);
    logger.info("forward tweet {} success", tweet.getId());
    return true;
  }

  @RequestMapping("/blog")
  public Long blog(
      @RequestParam String title,
      @RequestParam String content,
      @RequestParam(value = "tagIds[]", defaultValue = "") Collection<Long> tagIds) {
    Long uid = AuthUtil.checkCurrentUid();
    if (title.isEmpty() || content.isEmpty()) {
      return null;
    }

    Blog blog = blogService.newBlog(uid, title, content, tagIds);
    tweetPostService.share(uid, blog);
    if (true) {
      logger.info("post blog {} success", blog.getId());
    }
    return blog.getId();
  }

  @RequestMapping("/edit-blog/{blogId}")
  public Long editBlog(
      @PathVariable Long blogId,
      @RequestParam String title,
      @RequestParam String content,
      @RequestParam(value = "tagIds[]", defaultValue = "") Collection<Long> tagIds) {
    Long uid = AuthUtil.checkCurrentUid();
    if (title.isEmpty() || content.isEmpty()) {
      return null;
    }

    Blog blog = blogService.edit(uid, blogId, title, content, tagIds);

    return blog.getId();
  }

  @RequestMapping("/comment")
  public boolean comment(
      @RequestParam String content,
      @RequestParam Long sourceId) {
    Long uid = AuthUtil.checkCurrentUid();
    if (content.isEmpty()) {
      return false;
    }

    tweetPostService.comment(uid, content, sourceId);
    return true;
  }
}
