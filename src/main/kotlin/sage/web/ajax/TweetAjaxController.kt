package sage.web.ajax

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sage.web.auth.Auth
import sage.web.context.BaseController

@RestController
@RequestMapping("/tweets")
class TweetAjaxController() : BaseController() {
  @PostMapping("/{id}/delete")
  fun delete(@PathVariable id: Long) {
    tweetPostService.delete(Auth.checkUid(), id)
  }
}