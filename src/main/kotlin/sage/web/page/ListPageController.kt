package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import sage.service.HeedService
import sage.service.ListService
import sage.service.UserService
import sage.web.auth.Auth
import java.util.*

@Controller
@RequestMapping("/lists")
open class ListPageController {
  @Autowired
  private val listService: ListService? = null
  @Autowired
  private val userService: UserService? = null
  @Autowired
  private val heedService: HeedService? = null

  @RequestMapping
  open fun lists(@RequestParam(required = false) type: String?,
                  @RequestParam(required = false) uid: Long?, model: ModelMap): String {
    val cuid = Auth.checkUid()
    val ownerId = uid ?: cuid
    model.put("self", userService!!.getSelf(cuid))
    model.put("owner", userService.getUserLabel(ownerId))

    if (type == null || type == "follow") {
      val followLists = listService!!.followListsOfOwner(ownerId)
      model.put("followLists", followLists)

      val followListHeedStatuses = HashMap<Long, Boolean>()
      if (ownerId != cuid) {
        for (list in followLists) {
          followListHeedStatuses.put(list.id, heedService!!.existsFollowListHeed(cuid, list.id!!))
        }
      }
      model.put("followListHeedStatuses", followListHeedStatuses)
    }

    if (type == null || type == "resource") {
      model.put("resourceLists", listService!!.resourceListsOfOwner(ownerId))
    }

    return "lists"
  }
}
