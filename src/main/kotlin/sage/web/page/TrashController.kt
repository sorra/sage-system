package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.entity.Blog
import sage.entity.Tweet
import sage.service.TransferService
import sage.transfer.BlogPreview

@Controller
@RequestMapping("/trash")
open class TrashController @Autowired constructor(private val transferService: TransferService) {
  @RequestMapping
  open fun show(): ModelAndView {
    val blogs = Blog.where().eq("deleted", true).setIncludeSoftDeletes().findList().map(::BlogPreview)
    val tweets = Tweet.where().eq("deleted", true).findList().run { transferService.toTweetViews(this) }
    return ModelAndView("trash").addObject("blogs", blogs).addObject("tweets", tweets)
  }
}