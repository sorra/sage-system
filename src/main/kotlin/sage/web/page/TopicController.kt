package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import sage.domain.commons.ReformMention
import sage.entity.Liking
import sage.entity.TopicPost
import sage.entity.TopicReply
import sage.entity.TopicStat
import sage.service.TopicService
import sage.service.UserService
import sage.transfer.TagLabel
import sage.transfer.TopicPreview
import sage.transfer.TopicReplyView
import sage.transfer.TopicView
import sage.web.auth.Auth
import java.sql.Timestamp

@Controller
@RequestMapping("/topics")
open class TopicController @Autowired constructor(
    private val topicService: TopicService,
    private val userService: UserService) {

  @RequestMapping("/new", method = arrayOf(GET))
  open fun newPage(@RequestParam(required = false) belongTagId: Long?): ModelAndView {
    Auth.checkUid()
    return ModelAndView("write-topic").addObject("belongTagId", belongTagId)
  }

  @RequestMapping("/new", method = arrayOf(POST))
  @ResponseBody
  open fun create(@RequestParam title: String, @RequestParam content: String,
                  @RequestParam(defaultValue = "") reference: String,
                  @RequestParam("tagIds[]", defaultValue = "") tagIds: List<Long>): String {
    val uid = Auth.checkUid()
    val topic = topicService.post(uid, title, content, reference, tagIds)
    return "/topics/${topic.id}"
  }

  @RequestMapping("/{id}/edit", method = arrayOf(GET))
  open fun editPage(@PathVariable id: Long): ModelAndView {
    val uid = Auth.checkUid()
    val topic = TopicPost.get(id)
    val existingTags = topic.tags.map { TagLabel(it) }
    val topTags = userService.filterNewTags(uid, topic.tags.map { TagLabel(it) })
    return ModelAndView("write-topic")
        .addObject("topic", topic)
        .addObject("existingTags", existingTags).addObject("topTags", topTags)
  }

  @RequestMapping("/{id}/edit", method = arrayOf(POST))
  @ResponseBody
  open fun edit(@PathVariable id: Long, @RequestParam title: String, @RequestParam content: String,
                @RequestParam(defaultValue = "") reference: String,
                @RequestParam(required = false) belongTagId: Long?,
                @RequestParam("tagIds[]", defaultValue = "") tagIds: Set<Long>): String {
    val uid = Auth.checkUid()
    topicService.edit(uid, id, title, content, reference, belongTagId, tagIds)
    return "/topics/$id"
  }

  @RequestMapping("/{id}")
  open fun get(@PathVariable id: Long): ModelAndView {
    val topic = TopicPost.get(id).run { TopicView(this) }
    val replies = TopicReply.byPostId(id).map { reply ->
      reply.content = ReformMention.apply(reply.content)
      TopicReplyView(reply, reply.toUserId?.run { userService.getUserLabel(this) })
    }
    topic.views += 1
    TopicStat.incViews(id)
    val isLiked: Boolean? = Auth.uid()?.run { Liking.find(this, Liking.TOPIC, id) != null }
    return ModelAndView("topic").addObject("topic", topic).addObject("replies", replies)
        .addObject("isLiked", isLiked)
  }

  @RequestMapping("/{id}/reply", method = arrayOf(POST))
  @ResponseBody
  open fun reply(@PathVariable id: Long, @RequestParam content: String,
                 @RequestParam(required = false) toReplyId: Long?): String {
    topicService.reply(Auth.checkUid(), content, id, toReplyId)
    return "/topics/$id"
  }

  @RequestMapping("/{id}/like")
  @ResponseBody
  open fun like(@PathVariable id: Long) {
    val uid = Auth.checkUid()
    TopicStat.like(id, uid)
  }

  @RequestMapping("/{id}/unlike")
  @ResponseBody
  open fun unlike(@PathVariable id: Long) {
    val uid = Auth.checkUid()
    TopicStat.unlike(id, uid)
  }

  @RequestMapping("/{id}/likes")
  @ResponseBody
  open fun likes(@PathVariable id: Long) = TopicStat.get(id).likes

  @RequestMapping
  open fun all(@RequestParam(defaultValue = "1") page: Int,
               @RequestParam(defaultValue = "20") size: Int) : ModelAndView {
    val topics = TopicPost.orderBy("whenLastActive desc").findPagedList(page-1, size).list
        .map { TopicPreview(it) }
    return ModelAndView("topics").addObject("topics", topics)
  }
}