package sage.web.ajax

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sage.entity.TweetStat
import sage.web.auth.Auth

@RestController
@RequestMapping("/tweets")
open class TweetLikesController {
  @RequestMapping("/{id}/like")
  open fun like(@PathVariable id: Long) {
    val uid = Auth.checkUid()
    TweetStat.like(id, uid)
  }

  @RequestMapping("/{id}/unlike")
  open fun unlike(@PathVariable id: Long) {
    val uid = Auth.checkUid()
    TweetStat.unlike(id, uid)
  }

  @RequestMapping("/{id}/likes")
  open fun likes(@PathVariable id: Long) = TweetStat.get(id).likes
}