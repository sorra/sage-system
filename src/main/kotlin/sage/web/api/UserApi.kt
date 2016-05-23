package sage.web.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sage.service.UserService
import sage.transfer.UserCard
import sage.transfer.UserSelf
import sage.web.auth.Auth

@RestController
@RequestMapping("/user")
open class UserApi {
  @Autowired
  internal var userService: UserService? = null

  @RequestMapping("/self")
  open fun self(): UserSelf {
    val uid = Auth.checkUid()
    return userService!!.getSelf(uid)
  }

  @RequestMapping("/card/{id}")
  open fun userCard(@PathVariable id: Long?): UserCard {
    val uid = Auth.checkUid()
    return userService!!.getUserCard(uid, id!!)
  }

  @RequestMapping("/info/{id}")
  open fun userInfo(@PathVariable id: Long?): Any {
    throw UnsupportedOperationException()
  }

  @RequestMapping("/change-intro", method = arrayOf(RequestMethod.POST))
  open fun changeIntro(@RequestParam intro: String) {
    userService!!.changeIntro(Auth.checkUid(), intro)
  }

  @RequestMapping("/change-avatar", method = arrayOf(RequestMethod.POST))
  open fun changeAvatar(@RequestParam photo: MultipartFile) {
    //TODO
    userService!!.changeAvatar(Auth.checkUid(), "")
  }

}
