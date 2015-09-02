package sage.web;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.service.*;
import sage.entity.Blog;
import sage.entity.Tweet;
import sage.util.Colls;
import sage.web.auth.Auth;

@RestController
@RequestMapping(value = "/post", method = RequestMethod.POST)
public class PostController {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private TweetPostService tweetPostService;
  @Autowired
  private BlogPostService blogService;
  @Autowired
  private TopicService topicService;
  @Autowired
  private GroupService groupService;

  @RequestMapping("/tweet")
  public boolean tweet(
      @RequestParam String content,
      @RequestParam(value = "pictureRef[]", defaultValue = "") Collection<String> pictureRefs,
      @RequestParam(value = "tagIds[]", defaultValue = "") Collection<Long> tagIds) {
    Long uid = Auth.checkCuid();
    logger.info("Got picture: " + pictureRefs);
    String tail = String.join(" ", Colls.map(pictureRefs, ref -> "img://" + ref));
    content = content+" "+tail;
    Tweet tweet = tweetPostService.post(uid, content, tagIds);
    logger.info("post tweet {} success", tweet.getId());
    return true;
  }

  @RequestMapping("/forward")
  public boolean forward(
      @RequestParam String content,
      @RequestParam Long originId,
      @RequestParam(value = "removedIds[]", defaultValue = "") Collection<Long> removedIds) {
    Long uid = Auth.checkCuid();
    Tweet tweet = tweetPostService.forward(uid, content, originId, removedIds);
    logger.info("forward tweet {} success", tweet.getId());
    return true;
  }

  @RequestMapping("/blog")
  public String blog(
      @RequestParam String title,
      @RequestParam String content,
      @RequestParam(value = "tagIds[]", defaultValue = "") Collection<Long> tagIds,
      @RequestParam(defaultValue = "true") Boolean sharable,
      @RequestParam(required = false) Long groupId) {
    Long uid = Auth.checkCuid();
    Blog blog = blogService.post(uid, title, content, tagIds);
    if (sharable) {
      tweetPostService.share(uid, blog);
    }
    if (groupId != null) {
      long topicId = topicService.post(uid, blog, groupId).getId();
      return String.format("/topic/%d", topicId);
    }
    logger.info("post blog {} success", blog.getId());
    return "/blog/" + blog.getId();
  }

  @RequestMapping("/edit-blog/{blogId}")
  public String editBlog(
      @PathVariable Long blogId,
      @RequestParam String title,
      @RequestParam String content,
      @RequestParam(value = "tagIds[]", defaultValue = "") Collection<Long> tagIds) {
    Long uid = Auth.checkCuid();
    Blog blog = blogService.edit(uid, blogId, title, content, tagIds);
    return "/blog/" + blog.getId();
  }

  @RequestMapping("/comment")
  public boolean comment(
      @RequestParam String content,
      @RequestParam Long sourceId,
      @RequestParam(required = false) Long replyUserId) {
    Long uid = Auth.checkCuid();
    //TODO save reply info in the comment
    tweetPostService.comment(uid, content, sourceId, replyUserId);
    return true;
  }
}
