package sage.web.page;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

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
    if (withUser != null) {
      loadConversation(model, cuid, withUser,
          () -> messageService.withSomeone(cuid, withUser));
      return "msgs-with";
    } else {
      loadMessageLists(model, cuid, () -> messageService.all(cuid));
      return "msgs-all";
    }
  }

  @RequestMapping("/more")
  public String more(@RequestParam Long withUser, @RequestParam Long afterId, ModelMap model) {
    Long cuid = Auth.checkCuid();
    loadConversation(model, cuid, withUser,
        () -> messageService.withSomeoneAfterThat(cuid, withUser, afterId));
    return "msgs-more";
  }

  private void loadConversation(ModelMap model, Long cuid, Long withUser, Supplier<List<Message>> messagesSupplier) {
    UserLabel self = userService.getUserLabel(cuid);
    UserLabel withUserLabel = userService.getUserLabel(withUser);
    model.put("messageList", new MessageList(messagesSupplier.get(), self, withUserLabel));
    model.put("users", Colls.mapOfValues(Arrays.asList(self, withUserLabel), UserLabel::getId));
  }

  private void loadMessageLists(ModelMap model, Long cuid, Supplier<List<Message>> flatMessagesSupplier) {
    List<MessageList> messageLists = new ArrayList<>();
    Map<Long, UserLabel> users = new HashMap<>();
    UserLabel self = userService.getUserLabel(cuid);
    users.put(self.getId(), self);

    flatMessagesSupplier.get().stream()
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
  }

  private static Comparator<Message> byTime = Comparator.comparing(Message::getTime);
}
