package sage.web.page;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sage.domain.service.TopicService;
import sage.domain.service.Transactor;
import sage.domain.service.UserService;
import sage.entity.Topic;
import sage.transfer.ReplyDetail;
import sage.transfer.TopicView;
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
      TopicView topic = new TopicView(_topic, author -> userService.getUserCard(Auth.cuid(), author.getId()));
      Collection<ReplyDetail> replies = Colls.map(_topic.getReplies(), ReplyDetail::new);
      model.put("topic", topic);
      model.put("replies", replies);
    });
    return "topic";
  }

  @RequestMapping("/topic/new") @ResponseBody
  Long newTopic(@RequestParam String title, @RequestParam String content) {
    return topicService.newTopic(Auth.checkCuid(), title, content).getId();
  }

  @RequestMapping("/topic/{id}/update") @ResponseBody
  void updateTopic(@PathVariable Long id, @RequestParam String title, @RequestParam String content) {
    topicService.updateTopic(Auth.checkCuid(), id, title, content);
  }
}
