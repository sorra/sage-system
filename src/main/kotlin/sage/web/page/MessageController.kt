package sage.web.page

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import sage.entity.Message
import sage.transfer.ConversationPreview
import sage.transfer.MessageList
import sage.web.auth.Auth
import sage.web.context.BaseController
import java.util.*

@Controller
@RequestMapping("/messages")
class MessageController : BaseController() {

  @RequestMapping
  fun messages(@RequestParam(required = false) withUserId: Long?, model: ModelMap): String {
    val uid = Auth.checkUid()
    if (withUserId != null) {
      loadConversation(model, uid, withUserId,
          { messageService.withSomeone(uid, withUserId) })
      return "msgs-with"
    } else {
      loadConversationPreviews(model, uid, { messageService.all(uid) })
      model.put("friends", relationService.friends(uid))
      return "msgs-all"
    }
  }

  @RequestMapping("/more")
  fun more(@RequestParam withUser: Long, @RequestParam afterId: Long, model: ModelMap): String {
    val uid = Auth.checkUid()
    loadConversation(model, uid, withUser,
        { messageService.withSomeoneAfterThat(uid, withUser, afterId) })
    return "msgs-more"
  }

  private fun loadConversation(model: ModelMap, cuid: Long, withUser: Long, messagesSupplier: () -> List<Message>) {
    val self = userService.getUserLabel(cuid)
    val withUserLabel = userService.getUserLabel(withUser)
    model.put("messageList", MessageList(messagesSupplier.invoke(), self, withUserLabel))
    model.put("users", Arrays.asList(self, withUserLabel).associateBy { it.id })
  }

  private fun loadConversationPreviews(model: ModelMap, uid: Long, flatMessagesSupplier: () -> List<Message>) {
    val conversations = ArrayList<ConversationPreview>()
    val self = userService.getUserLabel(uid)

    flatMessagesSupplier.invoke().groupBy { msg ->
      // 以对方id作grouping
      when (uid) {
        msg.fromUser -> msg.toUser
        msg.toUser -> msg.fromUser
        else -> {
          log.error("Message from or to is neither cuid! msg = {}", msg)
          0L
        }
      }
    }.forEach { entry -> val (withUserId, list) = entry
      if (withUserId > 0 && list.isNotEmpty()) {
        val withUserLabel = userService.getUserLabel(withUserId)
        conversations.add(ConversationPreview(self, withUserLabel, list.sortedByDescending { it.whenCreated }[0]))
      }
    }
    model.put("conversations", conversations)
  }

  companion object {
    private val log = LoggerFactory.getLogger(MessageController::class.java)
  }
}
