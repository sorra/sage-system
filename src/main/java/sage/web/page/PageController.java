package sage.web.page;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sage.domain.service.BlogReadService;
import sage.domain.service.GroupService;
import sage.domain.service.TweetReadService;
import sage.domain.service.UserService;
import sage.transfer.BlogView;
import sage.transfer.TagLabel;
import sage.transfer.TweetView;
import sage.util.Colls;
import sage.web.auth.Auth;
import sage.web.context.FrontMap;

import java.util.Collection;

@Controller
public class PageController {
  private final static Logger logger = LoggerFactory.getLogger(PageController.class);
  @Autowired
  private UserService userService;
  @Autowired
  private BlogReadService blogReadService;
  @Autowired
  private TweetReadService tweetReadService;
  @Autowired
  private GroupService groupService;

  @RequestMapping("/people")
  String people(ModelMap model) {
    FrontMap.from(model)
        .attr("people", userService.people(Auth.checkCuid()))
        .attr("recomms", userService.recommendByTag(Auth.checkCuid()));
    return "people";
  }

  @RequestMapping("/tweet/{id}")
  public String tweetPage(@PathVariable long id, ModelMap model) {
    TweetView tc = tweetReadService.getTweetView(id);
    FrontMap.from(model).attr("tc", tc);
    return "tweet";
  }

  @RequestMapping("/blog/{id}")
  public String blogPage(@PathVariable long id, ModelMap model) {
    BlogView blog = blogReadService.getBlogData(id);
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
    Auth.checkCuid();
    return "fav";
  }

  @RequestMapping("/write-blog")
  public String writeBlog(@RequestParam(required = false) Long groupId, ModelMap model) {
    Long cuid = Auth.checkCuid();
    if (groupId != null) {
      Collection<TagLabel> groupTags = Colls.map(
          groupService.getGroup(groupId).getTags(), TagLabel::new);
      model.put("existingTags", groupTags);
      model.put("topTags", userService.filterUserTags(cuid, groupTags));
      FrontMap.from(model).attr("groupId", groupId);
    }
    return "write-blog";
  }

  @RequestMapping("/blog/{blogId}/edit")
  public String blogEdit(@PathVariable Long blogId, ModelMap model) {
    Long cuid = Auth.checkCuid();

    BlogView blog = blogReadService.getBlogData(blogId);
    if (blog.getAuthorId().equals(cuid)) {
      model.put("blog", blog);
      model.put("existingTags", blog.getTags());
      model.put("topTags", userService.filterUserTags(cuid, blog.getTags()));
      FrontMap.from(model).attr("blogId", blog.getId());
      return "write-blog";
    } else
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
