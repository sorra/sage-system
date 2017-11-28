package sage.web.error

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/errors/")
class ErrorPageController {
  @RequestMapping("/not-found")
  fun notFound(): ModelAndView {
    val mv = ModelAndView("error")
    mv.modelMap.addAttribute("errorCode", 404).addAttribute("reason", "找不到页面")
    return mv
  }
}