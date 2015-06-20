package sage.web.page;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import sage.domain.commons.Tx;
import sage.domain.service.*;
import sage.entity.Group;
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
  @Autowired
  private BlogReadService blogReadService;

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
    Tx.apply(() -> {
      Collection<GroupTopicPreview> topics = Colls.map(groupService.topics(id), GroupTopicPreview::new);
      model.put("group", new GroupPreview(groupService.getGroup(id)));
      model.put("topics", topics);
    });
    return "group";
  }

  @RequestMapping("/groups")
  String groups(ModelMap model) {
    model.put("groups", groupService.all());
    return "groups";
  }

  @RequestMapping("/topic/{id}")
  String topic(@PathVariable Long id, ModelMap model) {
    Auth.checkCuid();
    Tx.apply(() -> {
      BlogView item = new BlogView(groupService.getTopic(id).getBlog());
      UserCard author = userService.getUserCard(Auth.cuid(), item.getAuthorId());
      model.put("topic", item);
      model.put("author", author);
    });
    return "topic";
  }

  @RequestMapping(value = "group/{groupId}/submit", method = RequestMethod.POST)
  @ResponseBody
  String topicSubmit(@PathVariable Long groupId, @RequestParam Long blogId) {
    Long cuid = Auth.checkCuid();
    long topicId = groupService. post(cuid, blogId, groupId).getId();
    return "/topic/" + topicId;
  }
}
