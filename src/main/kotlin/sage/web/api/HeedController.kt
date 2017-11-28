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
class HeedController @Autowired constructor(
  private val heedService: HeedService,
  private val userService: UserService,
  private val tagService: TagService) {

  @RequestMapping("/heed/tag/{id}", method = arrayOf(POST))
  fun heedTag(@PathVariable id: Long) {
    val uid = Auth.checkUid()
    heedService.heedTag(uid, id)
  }

  @RequestMapping("/unheed/tag/{id}", method = arrayOf(POST))
  fun unheedTag(@PathVariable id: Long) {
    val uid = Auth.checkUid()
    heedService.unheedTag(uid, id)
  }

  @RequestMapping("/heed/follow-list/{id}", method = arrayOf(POST))
  fun heedFollowList(@PathVariable id: Long) {
    val cuid = Auth.checkUid()
    heedService.heedFollowList(cuid, id)
  }

  @RequestMapping("/unheed/follow-list/{id}", method = arrayOf(POST))
  fun unheedFollowList(@PathVariable id: Long) {
    val cuid = Auth.checkUid()
    heedService.unheedFollowList(cuid, id)
  }

  @RequestMapping("/heeding/tag/{id}")
  fun heedingTag(@PathVariable id: Long) = heedService.existsTagHeed(Auth.checkUid(), id)

  @RequestMapping("/heeding/follow-list/{id}")
  fun heedingFollowList(@PathVariable id: Long) = heedService.existsFollowListHeed(Auth.checkUid(), id)

  @RequestMapping("/heeds/tag", method = arrayOf(GET))
  fun tagHeeds(): List<TagCard> {
    val cuid = Auth.checkUid()
    return heedService.tagHeeds(cuid).map({ tagHeed -> tagService.getTagCard(tagHeed.tag.id) })
  }

  @RequestMapping("/heeds/follow-list", method = arrayOf(GET))
  fun followListHeeds(): List<FollowList> {
    val cuid = Auth.checkUid()
    return heedService.followListHeeds(cuid).map { flHeed ->
      FollowListLite.fromEntity(flHeed.list).toFull({ userService.getUserLabel(it) }, { tagService.getTagLabel(it) })
    }
  }
}
