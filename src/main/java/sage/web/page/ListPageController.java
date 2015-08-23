package sage.web.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sage.domain.service.HeedService;
import sage.domain.service.ListService;
import sage.domain.service.UserService;
import sage.transfer.FollowList;
import sage.web.auth.Auth;

@Controller
@RequestMapping("/pages")
public class ListPageController {
  @Autowired
  private ListService listService;
  @Autowired
  private UserService userService;
  @Autowired
  private HeedService heedService;

  @RequestMapping("/lists")
  public String followLists(@RequestParam(required = false) String type,
                            @RequestParam(required = false) Long uid, ModelMap model) {
    Long cuid = Auth.checkCuid();
    Long ownerId = uid != null ? uid : cuid;
    model.put("self", userService.getSelf(cuid));
    model.put("owner", userService.getUserLabel(ownerId));

    if (type == null || type.equals("follow")) {
      List<FollowList> followLists = listService.followListsOfOwner(ownerId);
      model.put("followLists", followLists);

      Map<Long, Boolean> followListHeedStatuses = new HashMap<>();
      if (!ownerId.equals(cuid)) {
        for (FollowList list : followLists) {
          followListHeedStatuses.put(list.getId(), heedService.followListHeedStatus(cuid, list.getId()));
        }
      }
      model.put("followListHeedStatuses", followListHeedStatuses);
    }

    if (type == null || type.equals("resource")) {
      model.put("resourceLists", listService.resourceListsOfOwner(ownerId));
    }

    return "lists";
  }
}
