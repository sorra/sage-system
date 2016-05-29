package sage.web.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.entity.Tag
import sage.service.TagService
import sage.transfer.TagLabel

@RestController
@RequestMapping("/tag")
open class TagApi
@Autowired constructor(
    private val tagService: TagService
) {

  @RequestMapping("/card/{id}")
  open fun tagCard(@PathVariable id: Long) = tagService.getTagCard(id)

  @RequestMapping("/tree")
  open fun tagTree() = tagService.getTagTree()

  @RequestMapping("/suggestions")
  open fun suggestions(@RequestParam q: String): List<TagLabel> {
    return Tag.query().where("instr(name, :q)").setParameter("q", q).findList().map { TagLabel(it) }
  }
}
