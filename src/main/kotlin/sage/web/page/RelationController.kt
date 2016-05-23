package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.service.RelationService
import sage.service.UserService
import sage.web.auth.Auth

@Controller
open class RelationController @Autowired constructor(
    private val userService: UserService,
    private val relationService: RelationService) {

  @RequestMapping("/followings")
  open fun followings() = "forward:/followings/${Auth.checkUid()}"

  @RequestMapping("/followings/{userId}")
  open fun followings(@PathVariable userId: Long): ModelAndView {
    val uid = Auth.checkUid()
    val thisUser = userService.getUserCard(uid, userId)
    val followings = relationService.followings(userId).map { fol ->
      userService.getUserCard(uid, fol.target.id)
    }
    return ModelAndView("followings").addObject("users", followings)
        .addObject("thisUser", thisUser).addObject("isSelf", uid == userId)
}

  @RequestMapping("/followers")
  open fun followers() = "forward:/followers/${Auth.checkUid()}"

  @RequestMapping("/followers/{userId}")
  open fun followers(@PathVariable userId: Long): ModelAndView {
    val uid = Auth.checkUid()
    val thisUser = userService.getUserCard(uid, userId)
    val followers = relationService.followers(userId).map { fol ->
      userService.getUserCard(uid, fol.source.id)
    }
    return ModelAndView("followers").addObject("users", followers)
        .addObject("thisUser", thisUser).addObject("isSelf", uid == userId)
  }
}
