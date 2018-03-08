package sage.web.page

import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import sage.service.FilesService
import sage.service.UserService
import sage.web.auth.Auth

@Controller
class UserInfoController
@Autowired constructor(
    private val userService: UserService,
    private val filesService: FilesService) {

  @GetMapping("/user-info")
  fun info(@RequestParam(required = false) next: String?, model: ModelMap): String {
    val cuid = Auth.checkUid()
    model.put("user", userService.getUserLabel(cuid))
    var action = "user-info"
    if (StringUtils.isNotBlank(next)) {
      action += "?next=" + next
    }
    model.put("action", action)
    return "user-info"
  }

  @PostMapping("/user-info")
  fun changeInfo(@RequestParam(required = false) name: String?,
                      @RequestParam(required = false) intro: String?,
                      @RequestParam(required = false) avatar: MultipartFile?,
                      @RequestParam(required = false) colorAvatar: String?,
                      @RequestParam(required = false) next: String?): String {
    val cuid = Auth.checkUid()
    val path =
        if (!colorAvatar.isNullOrEmpty()) colorAvatar
        else if (avatar != null && !avatar.isEmpty) filesService.upload(cuid, avatar, FilesService.Folder.AVATAR)
        else null
    userService.changeInfo(cuid, name, intro, path)
    if (StringUtils.isNotBlank(next)) {
      return "redirect:" + Auth.decodeLink(next!!)
    }
    return "redirect:/user-info"
  }

  @GetMapping("/change-password")
  fun pageChangePassword(): String {
    Auth.checkUid()
    return "change-password"
  }

  @PostMapping("/change-password")
  fun changePassword(@RequestParam oldPassword: String, @RequestParam newPassword: String, model: ModelMap): String {
    val cuid = Auth.checkUid()
    try {
      val match = userService.updatePassword(cuid, oldPassword, newPassword)
      if (match) {
        model.put("success", true)
        model.put("serverMsg", "修改成功")
      } else {
        model.put("serverMsg", "旧密码输入不对")
      }
    } catch (e: Exception) {
      log.error("changePassword", e)
      model.put("serverMsg", "未知错误")
    }

    return "change-password"
  }

  companion object {
    private val log = LoggerFactory.getLogger(UserInfoController::class.java)
  }
}
