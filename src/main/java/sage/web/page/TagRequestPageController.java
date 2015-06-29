package sage.web.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sage.domain.service.TagChangeService;
import sage.domain.service.TagService;
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
    Auth.checkCuid();
    model.put("tag", tagService.optTagCard(id).get());
    model.put("reqs", tagChangeService.getRequestsOfTag(id));
    return "tag-requests";
  }

  @RequestMapping("/{id}/scope-requests")
  String scopeRequests(@PathVariable Long id, ModelMap model) {
    Auth.checkCuid();
    model.put("tag", tagService.optTag(id));
    model.put("reqs", tagChangeService.getRequestsOfTagScope(id));
    return "tag-scope-requests";
  }
}