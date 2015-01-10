package sage.web.page;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sage.domain.service.TagChangeService;
import sage.domain.service.TagService;
import sage.transfer.TagChangeRequestCard;
import sage.web.auth.Auth;
import sage.web.context.FrontMap;

@Controller
@RequestMapping("/tag")
public class TagRequestPageController {
  @Autowired
  private TagService tagService;
  @Autowired
  private TagChangeService tagChangeService;

  @RequestMapping("/{id}/page/requests")
  String requests(@PathVariable Long id, ModelMap model) {
    Auth.checkCurrentUid();
    model.put("tag", tagService.getTag(id));
    FrontMap.from(model).put("reqs", tagChangeService.getRequestsOfTag(id));
    return "tag-requests";
  }

  @RequestMapping("/{id}/page/scope-requests")
  String scopeRequests(@PathVariable Long id, ModelMap model) {
    Auth.checkCurrentUid();
    model.put("tag", tagService.getTag(id));
    FrontMap.from(model).put("reqs", tagChangeService.getRequestsOfTagScope(id));
    return "tag-scope-requests";
  }
}