package sage.web.api

import org.springframework.web.bind.annotation.*
import sage.domain.commons.DomainException
import sage.transfer.*
import sage.web.auth.Auth
import sage.web.context.BaseController
import sage.util.Json

@RestController
@RequestMapping("/list")
class ListController : BaseController() {

  @GetMapping("/resource/{id}")
  fun getResourceList(@PathVariable id: Long): ResourceList {
    Auth.checkUid()
    return listService.getResourceList(id)
  }

  @PostMapping("/resource/{id}")
  fun updateResourceList(@PathVariable id: Long, @RequestParam list: String): Boolean {
    val uid = Auth.checkUid()

    val rc = Json.`object`(list, ResourceList::class.java)
    checkBeforeUpdate(id, rc)
    listService.updateResourceList(rc, uid)
    return true
  }

  @PostMapping("/resource/add")
  fun addResourceList(@RequestParam list: String): Long {
    val uid = Auth.checkUid()

    val rc = Json.`object`(list, ResourceList::class.java)
    return listService.addResourceList(rc, uid)
  }

  @GetMapping("/follow/{id}")
  fun getFollowList(@PathVariable id: Long): FollowList {
    Auth.checkUid()
    return listService.getFollowList(id)
  }

  @PostMapping("/follow/{id}")
  fun updateFollowList(@PathVariable id: Long, @RequestParam listLite: String): Boolean {
    val uid = Auth.checkUid()

    val fcLite = Json.`object`(listLite, FollowListLite::class.java)
    checkBeforeUpdate(id, fcLite)
    listService.updateFollowList(fcLite, uid)
    return true
  }

  @PostMapping("/follow/add")
  fun addFollowList(@RequestParam listLite: String): Long {
    val uid = Auth.checkUid()

    val fcLite = Json.`object`(listLite, FollowListLite::class.java)
    fcLite.ownerId = uid
    return listService.addFollowList(fcLite, uid)
  }

  @PostMapping("/follow/expose-all")
  fun exposeAllOfFollow(): Long {
    val uid = Auth.checkUid()

    val follows = relationService.followings(uid).map { f -> FollowInfoLite(f.target.id, f.tags.map { it.id }) }

    val existingIdOrNull = listService.followListsOfOwner(uid).find { it.name == "所有关注" }?.id
    val list = FollowListLite(existingIdOrNull, uid, "所有关注", follows)

    return if (existingIdOrNull == null) {
      listService.addFollowList(list, uid)
    } else {
      listService.updateFollowList(list, uid)
    }
  }

  private fun checkBeforeUpdate(id: Long, obj: AList) {
    if (obj.id != id) {
      throw DomainException("id不匹配：链接中id=${id}，对象中id=${obj.id}")
    }
    val uid = Auth.uid()
    if (obj.ownerId != uid) {
      throw DomainException("User[$uid] is not allowed to edit ${obj.javaClass}[$id]")
    }
  }
}
