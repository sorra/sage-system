package sage.web.page.admin

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import sage.entity.*
import sage.service.SearchService
import sage.service.ServiceInitializer
import sage.transfer.BlogView
import sage.transfer.TweetView
import sage.web.auth.Auth
import sage.web.context.DataInitializer
import java.util.*

@Controller
open class ZOperationController @Autowired constructor(
    private val si: ServiceInitializer, private val di: DataInitializer,
    private val searchService: SearchService
) {

  @RequestMapping("/z-init")
  @ResponseBody
  open fun initData(): String {
    if (User.byId(1) != null) {
      return "Already inited."
    }
    si.init()
    di.init()
    return "Done."
  }

  @RequestMapping("/z-reindex")
  @ResponseBody
  open fun reindex(): String {
    if (Auth.checkUid() != 1L) {
      return "Page not found."
    }
    searchService.setupMappings()
    Blog.findEach {
      searchService.index(it.id, BlogView(it))
    }
    Tweet.findEach {
      searchService.index(it.id, TweetView(it, Tweet.getOrigin(it), false, {false}))
    }
    return "Done."
  }

  @RequestMapping("/z-reload")
  open fun reloadHttl(@RequestParam name: String) = name

  @RequestMapping("/z-genstats")
  @ResponseBody
  open fun genstats(@RequestParam(defaultValue = "false") loopAll: Boolean): String {
    if (Auth.checkUid() != 1L) {
      return "Page not found."
    }

    val blogIds = arrayListOf<Long>()
    Blog.findEachWhile {
      if (BlogStat.byId(it.id) == null) {
        BlogStat(id = it.id, whenCreated = it.whenCreated).save()
        blogIds += it.id
        return@findEachWhile true
      } else return@findEachWhile loopAll
    }

    val tweetIds = arrayListOf<Long>()
    Tweet.findEachWhile {
      if (TweetStat.byId(it.id) == null) {
        TweetStat(id = it.id, whenCreated = it.whenCreated).save()
        tweetIds += it.id
        return@findEachWhile true
      } else return@findEachWhile loopAll
    }

    return "Done:\nblogs:$blogIds , tweets:$tweetIds"
  }

  @RequestMapping("/z-genavatars")
  @ResponseBody
  open fun genavatars(): String {
    if (Auth.checkUid() != 1L) {
      return "Page not found."
    }
    val random = Random()
    User.findEach { user ->
      if (user.avatar.isNullOrEmpty()) {
        val num = random.nextInt(6) + 2 // 0~5 + 2 = 2~7
        user.avatar = "/files/avatar/color${num}.png"
        user.update()
      }
    }
    return "Done."
  }
}
