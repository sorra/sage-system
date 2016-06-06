package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import sage.domain.commons.Edge
import sage.service.StreamService
import sage.web.auth.Auth

@Controller
@RequestMapping("/stream")
open class StreamController @Autowired constructor(
    val streamService: StreamService
) {
  @RequestMapping("/i")
  open fun istream(
      @RequestParam(required = false) tagId: Long?,
      @RequestParam(required = false) before: Long?,
      @RequestParam(required = false) after: Long?): ModelAndView {
    val uid = Auth.checkUid()
    val edge = getEdge(before, after)
    val stream =
      if (tagId != null) streamService.istreamByTag(uid, tagId, edge)
      else streamService.istream(uid, edge)
    return ModelAndView("stream").addObject("stream", stream)
  }

  @RequestMapping("/tag/{id}")
  open fun tagStream(@PathVariable id: Long,
                     @RequestParam(required = false) before: Long?,
                     @RequestParam(required = false) after: Long?): ModelAndView {
    val stream = streamService.tagStream(id, getEdge(before, after))
    return ModelAndView("stream").addObject("stream", stream)
  }

  @RequestMapping("/user/{id}")
  open fun userStream(@PathVariable id: Long,
                          @RequestParam(required = false) before: Long?,
                          @RequestParam(required = false) after: Long?): ModelAndView {
    val stream = streamService.personalStream(id, getEdge(before, after))
    return ModelAndView("stream").addObject("stream", stream)
  }

  private fun getEdge(beforeId: Long?, afterId: Long?): Edge {
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