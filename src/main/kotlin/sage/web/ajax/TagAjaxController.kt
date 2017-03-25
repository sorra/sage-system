package sage.web.ajax

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.entity.Tag
import sage.transfer.TagLabel
import sage.web.context.BaseController

@RestController
@RequestMapping("/tag")
class TagAjaxController : BaseController() {

  @RequestMapping("/card/{id}")
  fun tagCard(@PathVariable id: Long) = tagService.getTagCard(id)

  @RequestMapping("/tree")
  fun tagTree() = tagService.getTagTree()

  @RequestMapping("/suggestions")
  fun suggestions(@RequestParam q: String): List<TagLabel> {
    return Tag.query().where("instr(name, :q)").setParameter("q", q).findList().map { it.toTagLabel() }
  }
}
