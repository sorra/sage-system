package sage.web.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import sage.service.NotifService;
import sage.web.auth.Auth;

@Controller
@RequestMapping("/pages/notif")
public class NotifPageController {
  @Autowired
  private NotifService notifService;

  @RequestMapping("/unread")
  public String unread(ModelMap model) {
    Long cuid = Auth.checkCuid();
    model.put("category", "未读");
    model.put("notifs", notifService.unread(cuid));
    return "notif";
  }

  @RequestMapping("/all")
  public String all(ModelMap model) {
    Long cuid = Auth.checkCuid();
    model.put("category", "全部");
    model.put("notifs", notifService.all(cuid));
    return "notif";
  }
}
