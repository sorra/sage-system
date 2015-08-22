package sage.web.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sage.domain.service.ListService;
import sage.domain.service.UserService;
import sage.web.auth.Auth;

@Controller
@RequestMapping("/pages")
public class ListPageController {
  @Autowired
  private ListService listService;
  @Autowired
  private UserService userService;

  @RequestMapping("/lists")
  public String followLists(@RequestParam(required = false) String type,
                            @RequestParam(required = false) Long uid, ModelMap model) {
    Long cuid = Auth.checkCuid();
    Long ownerId = uid != null ? uid : cuid;
    model.put("self", userService.getSelf(cuid));
    model.put("owner", userService.getUserLabel(ownerId));
    if (type == null || type.equals("follow")) {
      model.put("followLists", listService.followListsOfOwner(ownerId));
    }
    if (type == null || type.equals("resource")) {
      model.put("resourceLists", listService.resourceListsOfOwner(ownerId));
    }
    return "lists";
  }
}
