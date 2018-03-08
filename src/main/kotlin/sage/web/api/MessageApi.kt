package sage.web.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sage.domain.commons.DomainException
import sage.entity.Message
import sage.service.MessageService
import sage.web.auth.Auth

@RestController
@RequestMapping("/api/message")
class MessageApi @Autowired constructor(
    private val messageService: MessageService
) {

  @GetMapping
  fun messages(@RequestParam withUserId: Long?): Collection<Message> {
    val uid = Auth.checkUid()
    return if (withUserId != null) messageService.withSomeone(uid, withUserId)
        else messageService.all(uid)
  }

  @PostMapping("/send")
  fun send(@RequestParam to: Long, @RequestParam content: String) {
    val uid = Auth.checkUid()
    if (content.isEmpty()) {
      throw CONTENT_EMPTY
    }
    messageService.send(uid, to, content)
  }

  companion object {

    private val CONTENT_EMPTY = DomainException("请输入内容!")
  }
}
