package sage.web.page

import org.springframework.web.servlet.ModelAndView
import sage.web.context.FrontMap

fun ModelAndView.include(frontMap: FrontMap): ModelAndView = addObject(FrontMap.NAME, frontMap)