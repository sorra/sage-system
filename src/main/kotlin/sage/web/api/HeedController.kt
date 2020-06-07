package sage.web.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import sage.service.HeedService
import sage.service.TagService
import sage.transfer.TagCard
import sage.web.auth.Auth

@RestController
class HeedController @Autowired constructor(
  private val heedService: HeedService,
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

  @GetMapping("/heeding/tag/{id}")
  fun heedingTag(@PathVariable id: Long) = heedService.existsTagHeed(Auth.checkUid(), id)

  @GetMapping("/heeds/tag")
  fun tagHeeds(): List<TagCard> {
    val cuid = Auth.checkUid()
    return heedService.tagHeeds(cuid).map {
      tagHeed -> tagService.getTagCard(tagHeed.tag.id)
    }
  }
}
