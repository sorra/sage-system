package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import sage.service.NotificationService
import sage.web.auth.Auth

@Controller
@RequestMapping("/notifications")
open class NotificationController @Autowired constructor(
    private val notificationService: NotificationService
) {

  @RequestMapping("/unread")
  open fun unread(): ModelAndView {
    val uid = Auth.checkUid()
    val ns = notificationService.unread(uid)
    return ModelAndView("notifications").addObject("category", "未读")
        .addObject("notifications", ns)
  }

  @RequestMapping("/unread-counts")
  @ResponseBody
  open fun unreadCounts() = notificationService.unreadCounts(Auth.checkUid())

  @RequestMapping("/all")
  open fun all(model: ModelMap): ModelAndView {
    val uid = Auth.checkUid()
    val ns = notificationService.all(uid)
    return ModelAndView("notifications").addObject("category", "全部")
        .addObject("notifications", ns)
  }

  @RequestMapping("/confirm-read")
  @ResponseBody
  open fun confirmRead(@RequestParam("id[]") ids: List<Long>) {
    val uid = Auth.checkUid()
    notificationService.confirmRead(uid, ids)
  }
}
