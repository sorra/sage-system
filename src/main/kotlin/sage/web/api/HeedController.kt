package sage.web.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
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

  @PostMapping("/heed/tag/{id}")
  fun heedTag(@PathVariable id: Long) {
    val uid = Auth.checkUid()
    heedService.heedTag(uid, id)
  }

  @PostMapping("/unheed/tag/{id}")
  fun unheedTag(@PathVariable id: Long) {
    val uid = Auth.checkUid()
    heedService.unheedTag(uid, id)
  }

  @PostMapping("/heed/follow-list/{id}")
  fun heedFollowList(@PathVariable id: Long) {
    val cuid = Auth.checkUid()
    heedService.heedFollowList(cuid, id)
  }

  @PostMapping("/unheed/follow-list/{id}")
  fun unheedFollowList(@PathVariable id: Long) {
    val cuid = Auth.checkUid()
    heedService.unheedFollowList(cuid, id)
  }

  @GetMapping("/heeding/tag/{id}")
  fun heedingTag(@PathVariable id: Long) = heedService.existsTagHeed(Auth.checkUid(), id)

  @GetMapping("/heeding/follow-list/{id}")
  fun heedingFollowList(@PathVariable id: Long) = heedService.existsFollowListHeed(Auth.checkUid(), id)

  @GetMapping("/heeds/tag")
  fun tagHeeds(): List<TagCard> {
    val cuid = Auth.checkUid()
    return heedService.tagHeeds(cuid).map({ tagHeed -> tagService.getTagCard(tagHeed.tag.id) })
  }

  @GetMapping("/heeds/follow-list")
  fun followListHeeds(): List<FollowList> {
    val cuid = Auth.checkUid()
    return heedService.followListHeeds(cuid).map { flHeed ->
      FollowListLite.fromEntity(flHeed.list).toFull({ userService.getUserLabel(it) }, { tagService.getTagLabel(it) })
    }
  }
}
