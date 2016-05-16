package sage.entity

import com.avaje.ebean.ExpressionList
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class TopicPost : BaseModel {

  var blog: Blog?
    private set

  @ManyToOne
  var author: User?

  var group: Group?

  var isHidden = false

  constructor(blog: Blog, group: Group) {
    this.blog = blog
    this.author = blog.author
    this.group = group
  }

  companion object : Find<Long, TopicPost>() {
    fun get(id: Long) = getNonNull(TopicPost::class, id)

    fun byGroup(groupId: Long) =
        where().eq("group", Group.ref(groupId)).eq("hidden", false).findList()

    fun recent(maxSize: Int) = query().orderBy("id desc").setMaxRows(maxSize).findList()
  }
}

@Entity
class TopicReply : BaseModel {

  var content: String? = null

  var topicPost: TopicPost? = null

  @ManyToOne
  var author: User? = null

  var toUserId: Long? = null

  var toReplyId: Long? = null

  constructor(topicPost: TopicPost, author: User, content: String) {
    this.topicPost = topicPost
    this.author = author
    this.content = content
  }

  fun setToInfo(toUserId: Long?, toReplyId: Long?): TopicReply {
    this.toUserId = toUserId
    this.toReplyId = toReplyId
    return this
  }

  companion object : Find<Long, TopicReply>() {
    fun get(id: Long) = getNonNull(TopicReply::class, id)

    fun <T> ExpressionList<T>.ofPost(postId: Long) = eq("topicPost", TopicPost.ref(postId))

    fun ofPost(postId : Long) = where().ofPost(postId).findList()

    fun lastReplyOfPost(postId: Long) =
        where().ofPost(postId).orderBy("id desc").setMaxRows(1).findUnique()
  }
}