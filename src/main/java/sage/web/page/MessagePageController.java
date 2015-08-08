package sage.web.page;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sage.domain.service.MessageService;
import sage.domain.service.UserService;
import sage.entity.Message;
import sage.transfer.MessageList;
import sage.transfer.UserLabel;
import sage.util.Colls;
import sage.web.auth.Auth;

import static java.util.stream.Collectors.groupingBy;

@Controller
@RequestMapping("/pages/message")
public class MessagePageController {
  private static final Logger log = LoggerFactory.getLogger(MessagePageController.class);
  @Autowired
  private MessageService messageService;
  @Autowired
  private UserService userService;

  @RequestMapping
  public String messages(@RequestParam(required = false) Long withUser, ModelMap model) {
    Long cuid = Auth.checkCuid();
    UserLabel self = userService.getUserLabel(cuid);
    if (withUser != null) {
      UserLabel withUserLabel = userService.getUserLabel(withUser);
      MessageList messageList = new MessageList(
          messageService.withSomeone(cuid, withUser), self, withUserLabel);

      model.put("messageList", messageList);
      model.put("users", Colls.mapOfValues(
          Arrays.asList(userService.getUserLabel(cuid), withUserLabel), UserLabel::getId));
      return "msgs-with";
    } else {
      List<MessageList> messageLists = new ArrayList<>();
      Map<Long, UserLabel> users = new HashMap<>();
      users.put(self.getId(), self);

      messageService.all(cuid).stream()
          .collect(groupingBy(msg -> {
            if (cuid.equals(msg.getFromUser())) {
              return msg.getFromUser();
            } else if (cuid.equals(msg.getToUser())) {
              return msg.getToUser();
            } else {
              log.error("Message from or to is neither cuid! msg = {}", msg);
              return 0L;
            }
          }))
          .forEach((userId, list) -> {
            if (userId == 0L) {
              return;
            }
            list.sort(byTime);
            UserLabel userLabel = userService.getUserLabel(userId);
            messageLists.add(new MessageList(list, self, userLabel));
            users.put(userId, userLabel);
          });
      model.put("messageLists", messageLists);
      model.put("users", users);
      return "msgs-all";
    }
  }

  private static Comparator<Message> byTime = Comparator.comparing(Message::getTime);
}
