package sage.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.cache.GlobalCaches
import sage.domain.commons.BadArgumentException
import sage.domain.commons.DomainException
import sage.domain.commons.Markdown
import sage.domain.commons.ReplaceMention
import sage.entity.*
import sage.transfer.TopicPreview
import sage.transfer.TopicReplyView
import sage.transfer.TopicView
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@Suppress("NAME_SHADOWING")
class TopicService @Autowired constructor(
    private val notificationService: NotificationService, private val searchService: SearchService) {

  fun post(userId: Long, title: String, content: String, reference: String, tagIds: Collection<Long>): TopicPost {
    if (title.isEmpty() || title.length > MAX_TITLE_LENGTH) throw BAD_TITLE_LENGTH
    if (content.isEmpty() || content.length > MAX_CONTENT_LENGTH) throw BAD_CONTENT_LENGTH
    if (tagIds.size > MAX_TAGS) throw TOO_MANY_TAGS

    val (content, mentionedIds) = processContent(content)

    val belongTag = Tag.get(tagIds.firstOrNull() ?: Tag.ROOT_ID)
    val tags = Tag.multiGet(tagIds)
    val tp = TopicPost(title, content, reference, User.ref(userId), belongTag, tags)
    tp.save()

    TopicStat(id = tp.id, whenCreated = tp.whenCreated).save()

    mentionedIds.forEach { atId ->
      notificationService.mentionedByTopicPost(atId, userId, tp.id)
    }

    searchService.index(tp.id, TopicView(tp))
    GlobalCaches.topicsCache.invalidateAll()
    return tp
  }

  fun edit(userId: Long, id: Long, title: String, content: String, reference: String, belongTagId: Long?, tagIds: Set<Long>) {
    if (title.isEmpty() || title.length > MAX_TITLE_LENGTH) throw BAD_TITLE_LENGTH
    if (content.isEmpty() || content.length > MAX_CONTENT_LENGTH) throw BAD_CONTENT_LENGTH
    if (tagIds.size > MAX_TAGS) throw TOO_MANY_TAGS

    val tp = TopicPost.get(id)
    if (userId != tp.author.id) throw DomainException("User[$userId] is not the author of TopicPost[${tp.id}]")

    val (content, mentionedIds) = processContent(content)

    tp.title = title
    tp.content = content
    tp.reference = reference
    tp.belongTag = Tag.get(belongTagId ?: Tag.ROOT_ID)
    tp.tags = Tag.multiGet(tagIds)
    tp.update()

    searchService.index(tp.id, TopicView(tp))
  }

  fun reply(userId: Long, content: String, topicPostId: Long, toReplyId: Long?): TopicReply {
    if (content.isEmpty() || content.length > MAX_REPLY_LENGTH) throw BAD_REPLY_LENGTH

    val (content, mentionedIds) = processContent(content)

    val toUserId = TopicReply.byId(toReplyId ?: 0)?.author?.id
    val floorNumber = TopicPost.nextFloorNumber(topicPostId)
    val reply = TopicReply(content, User.ref(userId), topicPostId, toUserId, toReplyId, floorNumber)
    reply.save()

    TopicStat.get(topicPostId).apply {
      whenLastReplied = reply.whenCreated
      replies = floorNumber
      update()
    }

    mentionedIds.forEach { atId ->
      notificationService.mentionedByTopicReply(atId, userId, reply.id)
    }
    if (toUserId != null) {
      notificationService.repliedInTopic(toUserId, userId, reply.id)
    }

    searchService.index(reply.id, TopicReplyView(reply, null))
    return reply
  }

  private fun processContent(content: String): Pair<String, HashSet<Long>> {
    var text = Markdown.addBlankRow(content)
    val mentionedIds = HashSet<Long>()
    text = ReplaceMention.with { User.byName(it) }.apply(text, mentionedIds)
    return Pair(text, mentionedIds)
  }

  fun byBelongTag(belongTagId: Long) = TopicPost.where()
      .eq("belongTag", Tag.ref(belongTagId)).orderBy("id desc").setMaxRows(20).findList()

  fun byTags(tagIds: List<Long>) = TopicPost.where()
      .`in`("tags", Tag.multiGet(tagIds)).orderBy("id desc").setMaxRows(20).findList()

  fun hotTopics(): List<TopicPreview> {
    val stats = TopicStat.where().orderBy("rank desc, id desc").setMaxRows(20).findList()
    return stats.map { TopicPreview(TopicPost.get(it.id)) }
  }

  private fun computeFallDown(time: Date): Double {
    val days = Instant.ofEpochMilli(time.time).until(Instant.now(), ChronoUnit.DAYS)
    return Math.pow(0.8, days.toDouble())
  }

  private companion object {
    val MAX_TITLE_LENGTH = 100
    val MAX_CONTENT_LENGTH = 5000
    val MAX_REPLY_LENGTH = 2000
    val MAX_TAGS = 3
    val BAD_TITLE_LENGTH = BadArgumentException("标题应为1~100字")
    val BAD_CONTENT_LENGTH = BadArgumentException("内容应为1~5000字")
    val BAD_REPLY_LENGTH = BadArgumentException("回复应为1~2000字")
    val TOO_MANY_TAGS = BadArgumentException("最多3个标签")
  }
}
