package sage.web.page

import org.springframework.web.servlet.ModelAndView
import sage.web.context.FrontMap

fun ModelAndView.include(frontMap: FrontMap) = addObject(FrontMap.NAME, frontMap)