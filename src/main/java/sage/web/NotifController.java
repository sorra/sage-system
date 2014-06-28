package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.service.NotifService;
import sage.entity.nosql.Notif;
import sage.web.auth.Auth;

import java.util.Collection;

@RestController
@RequestMapping("/notifs")
public class NotifController {

  @Autowired
  private NotifService notifSvc;
  
  @RequestMapping("/get")
  public Collection<Notif> notifs() {
    Long uid = Auth.checkCurrentUid();
    return notifSvc.getNotifs(uid);
  }
}
