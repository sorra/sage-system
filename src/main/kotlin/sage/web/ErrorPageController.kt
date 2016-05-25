package sage.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
open class ErrorPageController {
  @RequestMapping("/not-found")
  open fun notFound(): ModelAndView {
    val mv = ModelAndView("error")
    mv.modelMap.addAttribute("errorCode", 404).addAttribute("reason", "找不到页面")
    return mv
  }
}