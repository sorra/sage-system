package sage.web.page;

import java.util.*;
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
import sage.transfer.ConversationPreview;
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
      loadConversationPreviews(model, cuid, () -> messageService.all(cuid));
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

  private void loadConversationPreviews(ModelMap model, Long cuid, Supplier<List<Message>> flatMessagesSupplier) {
    List<ConversationPreview> conversations = new ArrayList<>();
    UserLabel self = userService.getUserLabel(cuid);

    flatMessagesSupplier.get().stream()
        .collect(groupingBy(msg -> {
          // 以对方id作grouping
          if (cuid.equals(msg.getFromUser())) {
            return msg.getToUser();
          } else if (cuid.equals(msg.getToUser())) {
            return msg.getFromUser();
          } else {
            log.error("Message from or to is neither cuid! msg = {}", msg);
            return 0L;
          }
        }))
        .forEach((withUserId, list) -> {
          if (withUserId == 0L || list.isEmpty()) {
            return;
          }
          list.sort(byTimeDesc);
          UserLabel withUserLabel = userService.getUserLabel(withUserId);
          conversations.add(new ConversationPreview(self, withUserLabel, list.get(0)));
        });
    model.put("conversations", conversations);
  }

  private static Comparator<Message> byTimeDesc = Comparator.comparing(Message::getTime, Comparator.reverseOrder());
}
