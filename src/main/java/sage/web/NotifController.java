package sage.web;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.service.NotifService;
import sage.entity.Notif;
import sage.web.auth.Auth;

@RestController
@RequestMapping("/notif")
public class NotifController {

  @Autowired
  private NotifService notifSvc;

  @RequestMapping("/unread")
  public Collection<Notif> unread() {
    return notifSvc.unread(Auth.checkCuid());
  }

  @RequestMapping("/all")
  public Collection<Notif> all() {
    return notifSvc.all(Auth.checkCuid());
  }
}
