package sage.web.ajax

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import sage.domain.commons.Edge
import sage.web.auth.Auth
import sage.web.context.BaseController

@Controller
@RequestMapping("/stream")
class StreamAjaxController : BaseController() {
  @RequestMapping("/i")
  fun istream(
      @RequestParam(required = false) tagId: Long?): ModelAndView {
    val uid = Auth.checkUid()
    val edge = getEdge()
    val stream =
      if (tagId != null) streamService.istreamByTag(uid, tagId, edge)
      else streamService.istream(uid, edge)
    return ModelAndView("stream").addObject("stream", stream)
  }

  @RequestMapping("/tag/{id}")
  fun tagStream(@PathVariable id: Long): ModelAndView {
    val edge = getEdge()
    val stream = streamService.tagStream(id, edge)
    return ModelAndView("stream").addObject("stream", stream)
  }

  @RequestMapping("/user/{id}")
  fun userStream(@PathVariable id: Long): ModelAndView {
    val edge = getEdge()
    val stream = streamService.personalStream(id, edge)
    return ModelAndView("stream").addObject("stream", stream)
  }

  fun before() = param("before")?.toLong()
  fun after() = param("after")?.toLong()

  private fun getEdge(): Edge {
    val beforeId = before()
    val afterId = after()
    if (beforeId == null && afterId == null) {
      return Edge.none()
    } else if (beforeId != null && afterId != null) {
      throw UnsupportedOperationException()
    } else if (beforeId != null) {
      return Edge.before(beforeId)
    } else if (afterId != null) {
      return Edge.after(afterId)
    }
    throw UnsupportedOperationException()
  }
}