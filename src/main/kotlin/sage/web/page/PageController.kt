package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.service.TweetReadService
import sage.service.UserService
import sage.transfer.CommentView
import sage.web.auth.Auth
import sage.web.context.FrontMap

@Controller
open class PageController @Autowired constructor(
    private val userService: UserService,
    private val tweetReadService: TweetReadService
) {

  @RequestMapping("/people")
  open fun people(): ModelAndView {
    val uid = Auth.uid()
    val recomms = if (uid != null) userService.recommendByTag(uid) else emptyList()
    val people = userService.people(uid)
    return ModelAndView("people").addObject("recomms", recomms).addObject("people", people)
  }

  @RequestMapping("/tweets/{id}")
  open fun tweetPage(@PathVariable id: Long): ModelAndView {
    val tweet = tweetReadService.getTweetView(id)
        ?: return ModelAndView("forward:/not-found")
    return ModelAndView("tweet-page").addObject("tweet", tweet)
  }

  @RequestMapping("/test")
  open fun test(): String {
    return "test"
  }
}
