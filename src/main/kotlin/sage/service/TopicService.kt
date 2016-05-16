package sage.service

import httl.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.DomainException
import sage.domain.commons.IdCommons
import sage.domain.commons.Links
import sage.domain.commons.ReplaceMention
import sage.entity.*
import sage.entity.TopicReply.Companion.ofPost
import sage.transfer.HotTopic
import sage.transfer.TopicPreview
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class TopicService
@Autowired constructor(private val notifService: NotifService) {

  fun post(userId: Long, blog: Blog, groupId: Long): TopicPost {
    if (!IdCommons.equal(userId, blog.author.id)) {
      throw DomainException("Cannot post topic. User[%d] is not the author of Blog[%d]", userId, blog.id)
    }
    val tp = TopicPost(blog, Group.ref(groupId))
    tp.save()
    return tp
  }

  fun post(userId: Long, blogId: Long, groupId: Long): TopicPost {
    return post(userId, Blog.get(blogId), groupId)
  }

  fun reply(userId: Long, content: String, topicPostId: Long, toReplyId: Long?): TopicReply {
    var content = content
    content = StringUtils.escapeXml(content)
    val mentionedIds = HashSet<Long>()
    content = ReplaceMention.with { User.byName(it) }.apply(content, mentionedIds)
    content = Links.linksToHtml(content)

    val toUserId = TopicReply.byId(toReplyId ?: 0)?.author?.id
    val reply = TopicReply(TopicPost.ref(topicPostId), User.ref(userId), content).setToInfo(toUserId, toReplyId)
    reply.save()

    mentionedIds.forEach { atId -> notifService.mentionedByTopicReply(atId, userId, reply.id) }
    if (reply.toUserId != null) {
      notifService.repliedInTopic(toUserId, userId, reply.id)
    }
    return reply
  }

  fun setHiddenOfTopicPost(id: Long, hidden: Boolean) {
    val post = TopicPost.get(id)
    val origHidden = post.isHidden
    if (origHidden != hidden) {
      post.isHidden = hidden
      post.update()
    }
  }

  fun getTopicPost(id: Long): TopicPost {
    return TopicPost.get(id)
  }

  fun repliesOfTopicPost(topicPostId: Long): List<TopicReply> {
    return TopicReply.ofPost(topicPostId)
  }

  // 按帖子最后活动时间排序, 新的在前
  fun groupTopicPreviews(groupId: Long): Collection<TopicPreview> {
    return TopicPost.byGroup(groupId).map { post ->
      val time = TopicReply.lastReplyOfPost(post.id)?.whenCreated ?: post.whenCreated ?: Timestamp(0)
      Pair(post, time)
    }.sortedByDescending { it.second }.map { it.first }
        .map { post -> TopicPreview(post, TopicReply.where().ofPost(post.id).findRowCount()) }
  }

  fun hotTopics(): Collection<HotTopic> {
    //TODO 显然比较糙
    return TopicPost.recent(1000).map({ post ->
      HotTopic(post, repliesOfTopicPost(post.id).size,
          TopicReply.lastReplyOfPost(post.id)?.whenCreated)
    }).map { hotTopic ->
      hotTopic.rank = (hotTopic.replyCount + 1) * computeFallDown(hotTopic.lastActiveTime)
      hotTopic
    }.sorted()
  }

  private fun computeFallDown(time: Date): Double {
    val days = Instant.ofEpochMilli(time.time).until(Instant.now(), ChronoUnit.DAYS)
    return Math.pow(0.8, days.toDouble())
  }
}
