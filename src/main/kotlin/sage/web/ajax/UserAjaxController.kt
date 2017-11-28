package sage.web.ajax

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sage.transfer.UserCard
import sage.transfer.UserSelf
import sage.web.auth.Auth
import sage.web.context.BaseController

@RestController
@RequestMapping("/user")
class UserAjaxController : BaseController() {

  @RequestMapping("/self")
  fun self(): UserSelf {
    val uid = Auth.checkUid()
    return userService.getSelf(uid)
  }

  @RequestMapping("/card/{id}")
  fun userCard(@PathVariable id: Long?): UserCard {
    val uid = Auth.checkUid()
    return userService.getUserCard(uid, id!!)
  }

  @RequestMapping("/info/{id}")
  fun userInfo(@PathVariable id: Long?): Any {
    throw UnsupportedOperationException()
  }

  @RequestMapping("/change-intro", method = arrayOf(RequestMethod.POST))
  fun changeIntro(@RequestParam intro: String) {
    userService.changeIntro(Auth.checkUid(), intro)
  }

  @RequestMapping("/change-avatar", method = arrayOf(RequestMethod.POST))
  fun changeAvatar(@RequestParam photo: MultipartFile) {
    //TODO
    userService.changeAvatar(Auth.checkUid(), "")
  }
}
