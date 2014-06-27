package sage.web;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sage.domain.service.NotifService;
import sage.entity.nosql.Notif;
import sage.web.auth.AuthUtil;

@Controller
@RequestMapping("/notifs")
public class NotifController {

  @Autowired
  private NotifService notifSvc;
  
  @RequestMapping("/get")
  @ResponseBody
  public Collection<Notif> notifs() {
    Long uid = AuthUtil.checkCurrentUid();
    return notifSvc.getNotifs(uid);
  }
}
