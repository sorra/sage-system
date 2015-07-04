package sage.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.service.NotifService;
import sage.entity.Notif;
import sage.transfer.NotifCounter;
import sage.transfer.NotifView;
import sage.web.auth.Auth;

@RestController
@RequestMapping("/notif")
public class NotifController {

  @Autowired
  private NotifService notifService;

  @RequestMapping("/unread-counts")
  public Map<String, NotifCounter> unreadCounts() {
    Map<String, NotifCounter> counts = new HashMap<>();
    unread().forEach(nv -> {
      NotifCounter notifCounter = counts.get(nv.type);
      if (notifCounter == null) {
        notifCounter = new NotifCounter();
        notifCounter.desc = Notif.Type.valueOf(nv.type).shortDesc;
        counts.put(nv.type, notifCounter);
      }
      notifCounter.count++;
    });
    return counts;
  }

  @RequestMapping("/unread")
  public Collection<NotifView> unread() {
    return notifService.unread(Auth.checkCuid());
  }

  @RequestMapping("/all")
  public Collection<NotifView> all() {
    return notifService.all(Auth.checkCuid());
  }

  @RequestMapping("/read-to")
  public void readTo(@RequestParam Long id) {
    notifService.readTo(Auth.checkCuid(), id);
  }
}
