package sage.web;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.service.TagChangeService;
import sage.domain.service.TagService;
import sage.transfer.TagCard;
import sage.transfer.TagChangeRequestCard;
import sage.transfer.TagNode;
import sage.util.Colls;
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
    Auth.checkCurrentUid();
    return tagChangeService.newTag(name, parentId, intro);
  }

  @RequestMapping("/{id}/move")
  public void move(@PathVariable Long id, @RequestParam Long parentId) {
    tagChangeService.requestMove(Auth.checkCurrentUid(), id, parentId);
  }

  @RequestMapping("/{id}/setIntro")
  public void setIntro(@PathVariable Long id, @RequestParam String intro) {
    tagChangeService.requestSetIntro(Auth.checkCurrentUid(), id, intro);
  }

  @RequestMapping("/{id}/requests")
  public Collection<TagChangeRequestCard> requests(@PathVariable Long id) {
    Auth.checkCurrentUid();
    return tagChangeService.getRequestsOfTag(id);
  }

  @RequestMapping("/{id}/scope-requests")
  public Collection<TagChangeRequestCard> scopeRequests(@PathVariable Long id) {
    Auth.checkCurrentUid();
    return tagChangeService.getRequestsOfTagScope(id);
  }
}
