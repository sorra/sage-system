package sage.web;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.MessageService;
import sage.entity.Message;
import sage.web.auth.Auth;

@RestController
@RequestMapping("/message")
public class MessageController {
  @Autowired
  private MessageService ms;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public Collection<Message> messages() {
    Long uid = Auth.checkCuid();
    return ms.all(uid);
  }

  @RequestMapping(value = "/with/{withUser}", method = RequestMethod.GET)
  public Collection<Message> messagesWith(@PathVariable Long withUser) {
    Long uid = Auth.checkCuid();
    return ms.withSomeone(uid, withUser);
  }

  @RequestMapping(value = "/send", method = RequestMethod.POST)
  public void send(@RequestParam Long to, @RequestParam String content) {
    Long uid = Auth.checkCuid();
    ms.send(uid, to, content);
  }

}
