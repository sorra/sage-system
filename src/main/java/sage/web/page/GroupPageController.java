package sage.web.page;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sage.domain.service.*;
import sage.entity.Blog;
import sage.entity.Tag;
import sage.transfer.*;
import sage.util.Colls;
import sage.web.auth.Auth;

@Controller
public class GroupPageController {
  @Autowired
  private GroupService groupService;
  @Autowired
  private UserService userService;
  @Autowired
  private TagService tagService;
  @Autowired
  private BlogPostService blogPostService;

  @RequestMapping("/group/{id}/topics")
  String topics(@PathVariable Long id, ModelMap model) {
    String area = groupService.getGroup(id).getName();
    Collection<BlogPreview> items = Colls.map(groupService.topics(id),
        topic -> new BlogPreview(topic.getBlog()));
    model.put("area", area);
    model.put("topics", items);
    return "topics";
  }

  @RequestMapping("/topic/{id}")
  String topic(@PathVariable Long id, ModelMap model) {
    Auth.checkCuid();
    Transactor.get().run(() -> {
      BlogData item = new BlogData(groupService.getTopic(id).getBlog());
      UserCard author = userService.getUserCard(Auth.cuid(), item.getAuthorId());
      model.put("topic", item);
      model.put("author", author);
    });
    return "topic";
  }

  @RequestMapping(value = "group/{groupId}/topics/post", method = RequestMethod.POST)
  String topicPost(@PathVariable Long groupId, @RequestParam String title, @RequestParam String content) {
    Auth.checkCuid();
    long id = Transactor.get().apply(() -> {
      Collection<Long> tagIds = Colls.map(groupService.getGroup(groupId).getTags(), Tag::getId);
      Blog blog = blogPostService.post(Auth.cuid(), title, content, tagIds);
      return groupService.post(Auth.cuid(), blog).getId();
    });
    return "redirect:/topic/" + id;
  }
}
