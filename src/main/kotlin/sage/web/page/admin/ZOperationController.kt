package sage.web.page.admin

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import sage.entity.*
import sage.service.SearchService
import sage.service.ServiceInitializer
import sage.service.UserService
import sage.transfer.BlogView
import sage.transfer.TopicReplyView
import sage.transfer.TopicView
import sage.transfer.TweetView
import sage.web.context.DataInitializer

@Controller
open class ZOperationController @Autowired constructor(
    private val si: ServiceInitializer, private val di: DataInitializer,
    private val userService: UserService, private val searchService: SearchService) {

  @RequestMapping("/z-init")
  @ResponseBody
  open fun initData(): String {
    si.init()
    di.init()
    return "Done."
  }

  @RequestMapping("/z-reindex")
  @ResponseBody
  open fun reindex(): String {
    searchService.setupMappings()
    Blog.findEach {
      searchService.index(it.id, BlogView(it))
    }
    TopicPost.findEach {
      searchService.index(it.id, TopicView(it))
    }
    TopicReply.findEach {
      searchService.index(it.id, TopicReplyView(it, it.toUserId?.run { userService.getUserLabel(this) }))
    }
    Tweet.findEach {
      searchService.index(it.id, TweetView(it, Tweet.getOrigin(it), 0, 0))
    }
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
