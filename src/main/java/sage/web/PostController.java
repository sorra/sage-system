package sage.web;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.BlogPostService;
import sage.domain.service.TweetPostService;
import sage.entity.Blog;
import sage.entity.Tweet;
import sage.web.auth.Auth;

@RestController
@RequestMapping(value = "/post", method = RequestMethod.POST)
public class PostController {
  private static final int TWEET__MAX_LEN = 2000, COMMENT_MAX_LEN = 200,
      BLOG_TITLE_MAX_LEN = 100, BLOG_CONTENT_MAX_LEN = 10000;

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
    Long uid = Auth.checkCurrentUid();
    if (content.isEmpty() || content.length() > TWEET__MAX_LEN) {
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
      @RequestParam Long originId,
      @RequestParam(value = "removedIds[]", defaultValue = "") Collection<Long> removedIds) {
    Long uid = Auth.checkCurrentUid();
    if (content.length() > TWEET__MAX_LEN) {
      return false;
    }

    Tweet tweet = tweetPostService.forward(uid, content, originId, removedIds);
    logger.info("forward tweet {} success", tweet.getId());
    return true;
  }

  @RequestMapping("/blog")
  public Long blog(
      @RequestParam String title,
      @RequestParam String content,
      @RequestParam(value = "tagIds[]", defaultValue = "") Collection<Long> tagIds) {
    Long uid = Auth.checkCurrentUid();
    if (title.isEmpty() || title.length() > BLOG_TITLE_MAX_LEN
        || content.isEmpty() || content.length() > BLOG_CONTENT_MAX_LEN) {
      return null;
    }

    Blog blog = blogService.newBlog(uid, title, content, tagIds);
    tweetPostService.share(uid, blog);
    logger.info("post blog {} success", blog.getId());
    return blog.getId();
  }

  @RequestMapping("/edit-blog/{blogId}")
  public Long editBlog(
      @PathVariable Long blogId,
      @RequestParam String title,
      @RequestParam String content,
      @RequestParam(value = "tagIds[]", defaultValue = "") Collection<Long> tagIds) {
    Long uid = Auth.checkCurrentUid();
    if (title.isEmpty() || title.length() > BLOG_TITLE_MAX_LEN
        || content.isEmpty() || content.length() > BLOG_CONTENT_MAX_LEN) {
      return null;
    }

    Blog blog = blogService.edit(uid, blogId, title, content, tagIds);

    return blog.getId();
  }

  @RequestMapping("/comment")
  public boolean comment(
      @RequestParam String content,
      @RequestParam Long sourceId,
      @RequestParam(required = false) Long replyUserId) {
    Long uid = Auth.checkCurrentUid();
    if (content.isEmpty() || content.length() > COMMENT_MAX_LEN) {
      return false;
    }

    //TODO save reply info in the comment
    tweetPostService.comment(uid, content, sourceId, replyUserId);
    return true;
  }
}
