package sage.web.page;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.*;
import sage.entity.Blog;
import sage.entity.Group;
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

  @RequestMapping(value = "/group/new", method = RequestMethod.GET)
  String newGroup() {
    Auth.checkCuid();
    return "new-group";
  }

  @RequestMapping(value = "/group/new", method = RequestMethod.POST)
  @ResponseBody
  String groupNew(@RequestParam String name, @RequestParam String introduction,
                  @RequestParam(value = "tagIds[]", defaultValue = "") Collection<Long> tagIds) {
    long id = groupService.newGroup(Auth.checkCuid(), name, introduction, tagIds).getId();
    return "/group/" + id;
  }

  @RequestMapping(value = "/group/{id}/edit", method = RequestMethod.GET)
  String editGroup(@PathVariable Long id, ModelMap model) {
    Auth.checkCuid();
    Group group = groupService.getGroup(id);
    model.put("group", group);
    model.put("existingTags", userService.filterUserTags(Auth.cuid(),
        Colls.map(group.getTags(), TagLabel::new)));
    return "edit-group";
  }

  @RequestMapping(value = "/group/{id}/edit", method = RequestMethod.POST)
  @ResponseBody
  String groupEdit(@PathVariable Long id,
                   @RequestParam String name, @RequestParam String introduction,
                   @RequestParam(value = "tagIds[]", defaultValue = "") Collection<Long> tagIds) {
    groupService.editGroup(Auth.checkCuid(), id, name, introduction, tagIds);
    return "/group/" + id;
  }

  @RequestMapping("/group/{id}")
  String group(@PathVariable Long id, ModelMap model) {
    Auth.checkCuid();
    Group group = groupService.getGroup(id);
    Collection<BlogPreview> items = Colls.map(groupService.topics(id),
        topic -> new BlogPreview(topic.getBlog()));
    model.put("group", group);
    model.put("topics", items);
    return "group";
  }

  @RequestMapping("/topic/{id}")
  String topic(@PathVariable Long id, ModelMap model) {
    Auth.checkCuid();
    Transactor.get().run(() -> {
      BlogView item = new BlogView(groupService.getTopic(id).getBlog());
      UserCard author = userService.getUserCard(Auth.cuid(), item.getAuthorId());
      model.put("topic", item);
      model.put("author", author);
    });
    return "topic";
  }

  @RequestMapping(value = "group/{groupId}/post", method = RequestMethod.POST)
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
