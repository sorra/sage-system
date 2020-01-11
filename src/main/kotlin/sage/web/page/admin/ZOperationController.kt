package sage.web.page.admin

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import sage.domain.cache.GlobalCaches
import sage.entity.*
import sage.service.BlogService
import sage.service.SearchService
import sage.service.TestServiceInitializer
import sage.service.TweetPostService
import sage.web.auth.Auth
import sage.web.context.TestDataInitializer
import java.util.*

@Controller
class ZOperationController @Autowired constructor(
    private val si: TestServiceInitializer,
    private val di: TestDataInitializer,
    private val searchService: SearchService,
    private val blogService: BlogService,
    private val tweetPostService: TweetPostService
) {

  @RequestMapping("/z-init")
  @ResponseBody
  fun initData(): String {
    if (User.byId(1) != null) {
      return "Already inited."
    }

    si.init()
    di.init()

    return "Done."
  }

  @RequestMapping("/z-reindex")
  @ResponseBody
  fun reindex() = authDo {
    searchService.setupIndex(SearchService.BLOG)
    blogService.reindexAll()

    searchService.setupIndex(SearchService.TWEET)
    tweetPostService.reindexAll()

    "Done."
  }

  @RequestMapping("/z-drop-reindex")
  @ResponseBody
  fun dropReindex() = authDo {
    searchService.dropIndex(SearchService.BLOG)
    searchService.dropIndex(SearchService.TWEET)

    reindex()
  }

  @RequestMapping("/z-reload")
  fun reloadHttl(@RequestParam name: String) = name

  @RequestMapping("/z-genstats")
  @ResponseBody
  fun genstats(@RequestParam(defaultValue = "false") loopAll: Boolean) = authDo {
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

    "Done:\nblogs:$blogIds , tweets:$tweetIds"
  }

  @RequestMapping("/z-genavatars")
  @ResponseBody
  fun genavatars() = authDo {
    val random = Random()
    User.findEach { user ->
      if (user.avatar.isEmpty()) {
        val num = random.nextInt(6) + 2 // 0~5 + 2 = 2~7
        user.avatar = "/files/avatar/color${num}.png"
        user.update()
      }
    }
    "Done."
  }

  @RequestMapping("/z-recoverchars")
  @ResponseBody
  fun recoverChars() = authDo {
    Blog.findEach { blog ->
      blog.inputContent = StringUtils.replaceEach(blog.inputContent,
          arrayOf("&amp;", "&lt;", "&gt;"), arrayOf("&", "<", ">"))
      blog.update()
    }
    "Done."
  }

  @RequestMapping("/z-delete")
  @ResponseBody
  fun delete(@RequestParam blogId: Long) = authDo {
    blogService.delete(Auth.checkUid(), blogId)
    "Done $blogId"
  }

  @RequestMapping("/z-clearcache")
  @ResponseBody
  fun clearCache() = authDo {
    GlobalCaches.blogsCache.clear()
    GlobalCaches.tweetsCache.clear()
    GlobalCaches.tagsCache.clear()
    "Done."
  }

  fun authDo(f: () -> String): String {
    return if(Auth.checkUid() != 1L) {
      "Page not found."
    } else {
      f()
    }
  }
}
