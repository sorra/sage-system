package sage.web.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sage.service.TagChangeService;
import sage.service.TagService;
import sage.web.auth.Auth;

@Controller
@RequestMapping("/pages/tag")
public class TagRequestPageController {
  @Autowired
  private TagService tagService;
  @Autowired
  private TagChangeService tagChangeService;

  @RequestMapping("/{id}/requests")
  String requests(@PathVariable Long id, ModelMap model) {
    long cuid = Auth.checkCuid();
    model.put("tag", tagService.getTagCard(id));
    model.put("reqs", tagChangeService.getRequestsOfTag(id));
    model.put("userCanTransact", tagChangeService.userCanTransact(cuid));
    model.put("currentUserId", cuid);
    return "tag-requests";
  }

  @RequestMapping("/{id}/scope-requests")
  String scopeRequests(@PathVariable Long id, ModelMap model) {
    long cuid = Auth.checkCuid();
    model.put("tag", tagService.getTagCard(id));
    model.put("reqs", tagChangeService.getRequestsOfTagScope(id));
    model.put("userCanTransact", tagChangeService.userCanTransact(cuid));
    model.put("currentUserId", cuid);
    return "tag-scope-requests";
  }

  @RequestMapping("{id}/do-change")
  String doChange(@PathVariable Long id, ModelMap model) {
    Auth.checkCuid();
    model.put("tag", tagService.getTagCard(id));
    return "tag-do-change";
  }
}