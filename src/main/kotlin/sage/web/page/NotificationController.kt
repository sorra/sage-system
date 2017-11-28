package sage.web.page

import org.slf4j.LoggerFactory
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
class NotificationController @Autowired constructor(
    private val notificationService: NotificationService
) {

  @RequestMapping("/unread")
  fun unread(): ModelAndView {
    val uid = Auth.checkUid()
    val ns = notificationService.unread(uid)
    return ModelAndView("notifications").addObject("category", "未读")
        .addObject("notifications", ns)
  }

  @RequestMapping("/unread-counts")
  @ResponseBody
  fun unreadCounts() = notificationService.unreadCounts(Auth.checkUid())

  @RequestMapping
  fun all(model: ModelMap): ModelAndView {
    val uid = Auth.checkUid()
    val ns = notificationService.all(uid)
    return ModelAndView("notifications").addObject("category", "全部")
        .addObject("notifications", ns)
  }

  @RequestMapping("/mark-read")
  @ResponseBody
  fun confirmRead(@RequestParam("id[]", required = false) ids: List<Long>?) {
    val uid = Auth.checkUid()
    if (ids == null) {
      log.warn("User[{}] /notifications/mark-read is requested with no id!")
    } else {
      notificationService.markRead(uid, ids)
    }
  }

  private val log = LoggerFactory.getLogger(javaClass)
}
