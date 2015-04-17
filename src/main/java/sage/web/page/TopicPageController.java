package sage.web.page;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sage.domain.service.TopicService;
import sage.domain.service.Transactor;
import sage.domain.service.TransferService;
import sage.domain.service.UserService;
import sage.entity.Topic;
import sage.transfer.ReplyDetail;
import sage.transfer.TopicDetail;
import sage.util.Colls;
import sage.web.auth.Auth;

@Controller
public class TopicPageController {
  @Autowired
  private TopicService topicService;
  @Autowired
  private UserService userService;

  @RequestMapping("/topics")
  String topics(ModelMap model) {
    model.put("topics", topicService.topics());
    return "topics";
  }

  @RequestMapping("/topic/{id}")
  String topic(@PathVariable Long id, ModelMap model) {
    Auth.checkCuid();
    Transactor.get().run(() -> {
      Topic _topic = topicService.getTopic(id);
      TopicDetail topic = new TopicDetail(_topic, author -> userService.getUserCard(Auth.cuid(), author.getId()));
      Collection<ReplyDetail> replies = Colls.map(_topic.getReplies(), ReplyDetail::new);
      model.put("topic", topic);
      model.put("replies", replies);
    });
    return "topic";
  }
}
