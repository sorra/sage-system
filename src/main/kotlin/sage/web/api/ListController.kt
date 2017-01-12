package sage.web.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.service.ListService
import sage.service.RelationService
import sage.transfer.FollowInfoLite
import sage.transfer.FollowList
import sage.transfer.FollowListLite
import sage.transfer.ResourceList
import sage.web.auth.Auth
import sage.web.context.Json

@RestController
@RequestMapping("/list")
open class ListController @Autowired constructor(
  private val listService: ListService,
  private val relationService: RelationService
  ) {

  @RequestMapping("/resource/{id}", method = arrayOf(GET))
  open fun getResourceList(@PathVariable id: Long?): ResourceList {
    Auth.checkUid()
    return listService.getResourceList(id!!)
  }

  @RequestMapping("/resource/{id}", method = arrayOf(POST))
  open fun updateResourceList(@PathVariable id: Long?, @RequestParam list: String): Boolean? {
    val uid = Auth.checkUid()

    val rc = Json.`object`(list, ResourceList::class.java)
    Assert.isTrue(rc.ownerId == uid)
    Assert.isTrue(rc.id == id)
    listService.updateResourceList(rc, uid)
    return true
  }

  @RequestMapping("/resource/add", method = arrayOf(POST))
  open fun addResourceList(@RequestParam list: String): Long? {
    val uid = Auth.checkUid()

    val rc = Json.`object`(list, ResourceList::class.java)
    return listService.addResourceList(rc, uid)
  }

  @RequestMapping("/follow/{id}", method = arrayOf(GET))
  open fun getFollowList(@PathVariable id: Long?): FollowList {
    Auth.checkUid()
    return listService.getFollowList(id!!)
  }

  @RequestMapping("/follow/{id}", method = arrayOf(POST))
  open fun updateFollowList(@PathVariable id: Long?, @RequestParam listLite: String): Boolean? {
    val uid = Auth.checkUid()

    val fcLite = Json.`object`(listLite, FollowListLite::class.java)
    Assert.isTrue(fcLite.ownerId == uid)
    Assert.isTrue(fcLite.id == id)
    listService.updateFollowList(fcLite, uid)
    return true
  }

  @RequestMapping("/follow/add", method = arrayOf(POST))
  open fun addFollowList(@RequestParam listLite: String): Long? {
    val uid = Auth.checkUid()

    val fcLite = Json.`object`(listLite, FollowListLite::class.java)
    fcLite.ownerId = uid
    return listService.addFollowList(fcLite, uid)
  }

  @RequestMapping("/follow/expose-all", method = arrayOf(POST))
  open fun exposeAllOfFollow(): Long? {
    val cuid = Auth.checkUid()

    val existingIdOrNull = listService.followListsOfOwner(cuid).find { it.name == "所有关注" }?.id

    val follows = relationService.followings(cuid).map { f -> FollowInfoLite(f.target.id, f.tags.map { it.id }) }
    val list = FollowListLite(existingIdOrNull, cuid, "所有关注", follows)
    if (existingIdOrNull == null) {
      return listService.addFollowList(list, cuid)
    } else {
      listService.updateFollowList(list, cuid)
      return existingIdOrNull
    }
  }
}
