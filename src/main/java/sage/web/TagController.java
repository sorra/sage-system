package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.service.TagService;
import sage.transfer.TagCard;
import sage.transfer.TagNode;

@RestController
@RequestMapping("/tag")
public class TagController {
  @Autowired
  TagService tagService;

  @RequestMapping("/card/{id}")
  public TagCard tagCard(@PathVariable Long id) {
    return tagService.getTagCard(id).orElse(null);
  }

  @RequestMapping("/tree")
  public TagNode tagTree() {
    return tagService.getTagTree();
  }

  @RequestMapping("/new")
  public long newTag(@RequestParam String name, @RequestParam Long parentId) {
    return tagService.newTag(name, parentId);
  }

  @RequestMapping("/change-parent")
  public void changeParent(@RequestParam long id, @RequestParam Long parentId) {
    tagService.changeParent(id, parentId);
  }
}
