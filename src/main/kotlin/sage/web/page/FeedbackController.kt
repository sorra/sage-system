package sage.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import sage.domain.commons.BadArgumentException
import sage.entity.Feedback
import sage.web.auth.Auth
import sage.web.context.BaseController

@Controller
@RequestMapping("/feedbacks")
class FeedbackController : BaseController() {

  @GetMapping
  fun show(): ModelAndView {
    val uid = Auth.uid()
    val feedbacks = Feedback.allDescending()
    return ModelAndView("feedbacks").addObject("feedbacks", feedbacks).addObject("uid", uid)
  }
  
  @PostMapping("/new")
  fun create(@RequestParam content: String,
             @RequestParam(defaultValue = "") name: String,
             @RequestParam(defaultValue = "") email: String): String {
    if (content.isEmpty()) throw BadArgumentException("请输入反馈内容")
    val ip = request.getHeader("X-Real-IP").let {
      if (it.isNullOrEmpty()) request.remoteAddr
      else it
    }
    Feedback(content = content, name = name.trim(), email = email.trim(), ip = ip).save()
    return "redirect:/feedbacks"
  }
}