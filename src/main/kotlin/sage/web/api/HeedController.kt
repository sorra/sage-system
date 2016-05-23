package sage.web.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RestController
import sage.service.HeedService
import sage.service.TagService
import sage.service.UserService
import sage.transfer.FollowList
import sage.transfer.FollowListLite
import sage.transfer.TagCard
import sage.web.auth.Auth

@RestController
@RequestMapping(method = arrayOf(POST))
open class HeedController {
  @Autowired
  private val heedService: HeedService? = null
  @Autowired
  private val userService: UserService? = null
  @Autowired
  private val tagService: TagService? = null

  @RequestMapping("/heed/tag/{id}")
  open fun heedTag(@PathVariable tagId: Long) {
    val uid = Auth.checkUid()
    heedService!!.heedTag(uid, tagId)
  }

  @RequestMapping("/unheed/tag/{id}")
  open fun unheedTag(@PathVariable tagId: Long) {
    val uid = Auth.checkUid()
    heedService!!.unheedTag(uid, tagId)
  }

  @RequestMapping("/heed/follow-list/{id}")
  open fun heedFollowList(@PathVariable id: Long) {
    val cuid = Auth.checkUid()
    heedService!!.heedFollowList(cuid, id)
  }

  @RequestMapping("/unheed/follow-list/{id}")
  open fun unheedFollowList(@PathVariable id: Long) {
    val cuid = Auth.checkUid()
    heedService!!.unheedFollowList(cuid, id)
  }

  @RequestMapping("/heeds/tag", method = arrayOf(GET))
  open fun tagHeeds(): List<TagCard> {
    val cuid = Auth.checkUid()
    return heedService!!.tagHeeds(cuid).map({ tagHeed -> tagService!!.getTagCard(tagHeed.tag.id) })
  }

  @RequestMapping("/heeds/follow-list", method = arrayOf(GET))
  open fun followListHeeds(): List<FollowList> {
    val cuid = Auth.checkUid()
    return heedService!!.followListHeeds(cuid).map { flHeed ->
      FollowListLite.fromEntity(flHeed.list).toFull({ userService!!.getUserLabel(it) }, { tagService!!.getTagLabel(it) })
    }
  }
}
