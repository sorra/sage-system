package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.service.TweetReadService
import sage.service.UserService
import sage.web.auth.Auth
import sage.web.context.FrontMap

@Controller
open class PageController @Autowired constructor(
    private val userService: UserService,
    private val tweetReadService: TweetReadService
) {

  @RequestMapping("/people")
  open fun people(): ModelAndView {
    val recomms = userService.recommendByTag(Auth.checkUid())
    val people = userService.people(Auth.checkUid())
    return ModelAndView("people").addObject("recomms", recomms).addObject("people", people)
  }

  @RequestMapping("/tweet/{id}")
  open fun tweetPage(@PathVariable id: Long, model: ModelMap): String {
    val tc = tweetReadService.getTweetView(id)!!
    FrontMap.from(model).attr("tc", tc)
    return "tweet"
  }

  @RequestMapping("/test")
  open fun test(): String {
    return "test"
  }
}
