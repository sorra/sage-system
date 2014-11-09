package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.service.TagChangeService;
import sage.domain.service.TagService;
import sage.transfer.TagCard;
import sage.transfer.TagNode;
import sage.web.auth.Auth;

@RestController
@RequestMapping("/tag")
public class TagController {
  @Autowired
  private TagService tagService;
  @Autowired
  private TagChangeService tagChangeService;

  @RequestMapping("/card/{id}")
  public TagCard tagCard(@PathVariable Long id) {
    return tagService.getTagCard(id).orElse(null);
  }

  @RequestMapping("/tree")
  public TagNode tagTree() {
    return tagService.getTagTree();
  }

  @RequestMapping("/new")
  public long newTag(@RequestParam String name, @RequestParam Long parentId, @RequestParam(required = false) String intro) {
    return tagChangeService.newTag(name, parentId, intro);
  }

  @RequestMapping("/move")
  public void changeParent(@RequestParam Long id, @RequestParam Long parentId) {
    tagChangeService.requestMove(Auth.checkCurrentUid(), id, parentId);
  }
}
