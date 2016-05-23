package sage.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import sage.service.ServiceInitializer
import sage.web.context.DataInitializer

@Controller
open class ZOperationController {
  @Autowired
  private val si: ServiceInitializer? = null
  @Autowired
  private val di: DataInitializer? = null

  @RequestMapping("/z-init")
  @ResponseBody
  open fun initData(): String {
    si!!.init()
    di!!.init()
    return "Done."
  }

  @RequestMapping("z-reload")
  open fun reloadHttl(@RequestParam name: String) = name
}
