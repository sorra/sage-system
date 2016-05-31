package sage.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import sage.entity.*
import sage.service.ServiceInitializer
import sage.web.context.DataInitializer

@Controller
open class ZOperationController {
  @Autowired
  private val si: ServiceInitializer? = null
  @Autowired
  private val di: DataInitializer? = null

  @RequestMapping("/z-init")
  @ResponseBody
  open fun initData(): String {
    si!!.init()
    di!!.init()
    return "Done."
  }

  @RequestMapping("z-reload")
  open fun reloadHttl(@RequestParam name: String) = name

  @RequestMapping("z-genstats")
  @ResponseBody
  open fun genstats(): String {
    val blogIds = arrayListOf<Long>()
    val topicIds = arrayListOf<Long>()

    Blog.findEachWhile {
      if (BlogStat.byId(it.id) == null) {
        BlogStat(id = it.id, whenCreated = it.whenCreated).save()
        blogIds += it.id
        return@findEachWhile true
      } else return@findEachWhile false
    }

    TopicPost.findEachWhile {
      if (TopicStat.byId(it.id) == null) {
        val replies = TopicReply.byPostId(it.id)
        replies.sortByDescending { it.whenCreated }
        TopicStat(id = it.id, whenCreated = it.whenCreated,
            whenLastReplied = replies.firstOrNull()?.whenCreated, replies = replies.size).save()
        topicIds += it.id
        return@findEachWhile true
      } else return@findEachWhile false
    }

    return "Done:\nblogs:$blogIds topics:$topicIds"
  }
}
