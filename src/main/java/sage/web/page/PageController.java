package sage.web.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import sage.domain.service.BlogReadService;
import sage.domain.service.TweetReadService;
import sage.transfer.BlogData;
import sage.transfer.TweetCard;
import sage.web.auth.AuthUtil;
import sage.web.context.JsonUtil;

@Controller
@RequestMapping
public class PageController {
  private final static Logger logger = LoggerFactory.getLogger(PageController.class);
  @Autowired
  private BlogReadService blogReadService;
  @Autowired
  private TweetReadService tweetReadService;

  @RequestMapping("/tweet/{id}")
  public String tweetPage(@PathVariable("id") long id, ModelMap model) {
    TweetCard tc = tweetReadService.getTweetCard(id);
    String tcJson = JsonUtil.json(tc);
    model.addAttribute("tcJson", tcJson);
    return "tweet";
  }

  @RequestMapping("/blog/{id}")
  public String blogPage(@PathVariable("id") long id, ModelMap model) {
    BlogData blog = blogReadService.getBlogData(id);
    if (blog == null) {
      logger.info("blog {} is null!", id);
      return "redirect:/";
    }

    model.addAttribute("blog", blog);
    return "blog";
  }

  @RequestMapping("/blogs")
  public String blogs(ModelMap model) {
    model.addAttribute("blogs", blogReadService.getAllBlogDatas());
    return "blogs";
  }
  
  @RequestMapping("/fav")
  public String fav(ModelMap model) {
    return "fav";
  }

  @RequestMapping("/write-blog")
  public String writeBlog() {
    AuthUtil.checkCurrentUid();
    return "write-blog";
  }

  @RequestMapping("/blog/{blogId}/edit")
  public String blogEdit(@PathVariable("blogId") Long blogId, ModelMap model) {
    Long currentUid = AuthUtil.checkCurrentUid();

    BlogData blog = blogReadService.getBlogData(blogId);
    if (blog.getAuthorId().equals(currentUid)) {
      model.addAttribute("blog", blog);
      return "write-blog";
    }
    else
      return "error";
  }

  @RequestMapping("/manip-tag")
  public void manipulateTag() {

  }

  @RequestMapping("/test")
  public String test() {
    return "test";
  }
}
