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
    Long uid = Auth.checkCurrentUid();
    return ms.all(uid);
  }

  @RequestMapping(value = "/from/{fromUser}", method = RequestMethod.GET)
  public Collection<Message> messagesFrom(@PathVariable Long fromUser) {
    Long uid = Auth.checkCurrentUid();
    return ms.fromSomeone(uid, fromUser);
  }

  @RequestMapping(value = "/send", method = RequestMethod.POST)
  public void send(@RequestParam Long to, @RequestParam String content) {
    Long uid = Auth.checkCurrentUid();
    ms.send(uid, to, content);
  }

}
