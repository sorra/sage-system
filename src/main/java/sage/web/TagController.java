package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sage.domain.service.TagService;
import sage.transfer.TagCard;
import sage.transfer.TagNode;

@Controller
@RequestMapping("/tag")
public class TagController {
  @Autowired
  TagService tagService;

  @RequestMapping("/card/{id}")
  @ResponseBody
  public TagCard tagCard(@PathVariable("id") Long id) {
    return tagService.getTagCard(id);
  }

  @RequestMapping("/tree")
  @ResponseBody
  public TagNode tagTree() {
    return tagService.getTagTree();
  }

  @RequestMapping("/new")
  @ResponseBody
  public long newTag(@RequestParam("name") String name, @RequestParam("parentId") Long parentId) {
    return tagService.newTag(name, parentId);
  }

  @RequestMapping("/change-parent")
  public void changeParent(@RequestParam("id") long id, @RequestParam("parentId") Long parentId) {
    tagService.changeParent(id, parentId);
  }
}
