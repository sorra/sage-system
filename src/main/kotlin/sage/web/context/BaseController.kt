package sage.web.context

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.ModelAndView
import sage.service.HasServices
import sage.web.model.FrontMap
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val SCOPE_REQUEST = WebApplicationContext.SCOPE_REQUEST

@Scope(SCOPE_REQUEST)
abstract class BaseController : HasServices() {
  @Autowired
  protected lateinit var request: HttpServletRequest
  @Autowired
  protected lateinit var response: HttpServletResponse

  fun frontMap(): FrontMap = FrontMap.from(request)

  fun param(name: String): String? = request.getParameter(name)

  fun param(name: String, defaultValue: String): String = param(name) ?: defaultValue

  fun fetchParam(name: String): String = param(name) ?: throw MissingServletRequestParameterException(name, "")

  fun paramArray(name: String): Array<String> = request.getParameterValues(name) ?: emptyArray()

  fun paramArray(name: String, defaultValue: Array<String>): Array<String> = paramArray(name).let {
    if (it.isEmpty()) defaultValue
    else it
  }

  fun fetchParamArray(name: String): Array<String> =
      request.getParameterValues(name) ?: throw MissingServletRequestParameterException(name, "Array")

  fun tagIds() = paramArray("tagIds[]").map(String::toLong).toSet()

  fun pageIndex() = param("pageIndex", "1").toInt()

  fun pageSize() = param("pageSize", "20").toInt()

  fun pagedModelAndView(listName: String, list: List<*>, pagesCount: Int, pageIndex: Int): ModelAndView =
      ModelAndView(listName)
          .addObject(listName, list)
          .addObject("paginationLinks", RenderUtil.paginationLinks("/$listName", pagesCount, pageIndex))
}