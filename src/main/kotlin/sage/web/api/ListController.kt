package sage.web.api

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.domain.commons.DomainException
import sage.transfer.*
import sage.web.auth.Auth
import sage.web.context.BaseController
import sage.web.context.Json

@RestController
@RequestMapping("/list")
class ListController : BaseController() {

  @RequestMapping("/resource/{id}", method = arrayOf(GET))
  fun getResourceList(@PathVariable id: Long): ResourceList {
    Auth.checkUid()
    return listService.getResourceList(id)
  }

  @RequestMapping("/resource/{id}", method = arrayOf(POST))
  fun updateResourceList(@PathVariable id: Long, @RequestParam list: String): Boolean {
    val uid = Auth.checkUid()

    val rc = Json.`object`(list, ResourceList::class.java)
    checkBeforeUpdate(id, rc)
    listService.updateResourceList(rc, uid)
    return true
  }

  @RequestMapping("/resource/add", method = arrayOf(POST))
  fun addResourceList(@RequestParam list: String): Long {
    val uid = Auth.checkUid()

    val rc = Json.`object`(list, ResourceList::class.java)
    return listService.addResourceList(rc, uid)
  }

  @RequestMapping("/follow/{id}", method = arrayOf(GET))
  fun getFollowList(@PathVariable id: Long): FollowList {
    Auth.checkUid()
    return listService.getFollowList(id)
  }

  @RequestMapping("/follow/{id}", method = arrayOf(POST))
  fun updateFollowList(@PathVariable id: Long, @RequestParam listLite: String): Boolean {
    val uid = Auth.checkUid()

    val fcLite = Json.`object`(listLite, FollowListLite::class.java)
    checkBeforeUpdate(id, fcLite)
    listService.updateFollowList(fcLite, uid)
    return true
  }

  @RequestMapping("/follow/add", method = arrayOf(POST))
  fun addFollowList(@RequestParam listLite: String): Long {
    val uid = Auth.checkUid()

    val fcLite = Json.`object`(listLite, FollowListLite::class.java)
    fcLite.ownerId = uid
    return listService.addFollowList(fcLite, uid)
  }

  @RequestMapping("/follow/expose-all", method = arrayOf(POST))
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
