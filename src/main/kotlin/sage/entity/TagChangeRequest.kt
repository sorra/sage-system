package sage.entity

import org.apache.commons.lang3.builder.ToStringBuilder
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class TagChangeRequest(@ManyToOne
                       val tag: Tag,
                       @ManyToOne
                       val submitter: User,
                       val type: Type) : AutoModel() {
  @ManyToOne
  var transactor: User? = null

  var status = Status.PENDING

  var parentId: Long = 0
    private set

  var name: String = ""

  var intro: String = ""

  enum class Status private constructor(val desc: String) {
    PENDING("待定"), CANCELED("已取消"), ACCEPTED("已接受"), REJECTED("已拒绝")
  }

  enum class Type private constructor(val desc: String) {
    MOVE("移动"), RENAME("改名"), SET_INTRO("修改简介")
  }

  override fun toString(): String {
    return ToStringBuilder.reflectionToString(this)
  }

  companion object : BaseFind<Long, TagChangeRequest>(TagChangeRequest::class) {

    fun byTag(tagId: Long): List<TagChangeRequest> = where().eq("tag", Tag.ref(tagId)).orderBy("id desc").findList()

    fun countByTagAndStatus(tagId: Long, status: Status) =
        where().eq("tag", Tag.ref(tagId)).eq("status", status.ordinal).findRowCount()

    fun byTagScope(tag: Tag): List<TagChangeRequest> =
        where().`in`("tag", tag.getQueryTags()).orderBy("id desc").findList()

    fun countByTagScopeAndStatus(tag: Tag, status: Status) =
        where().`in`("tag", tag.getQueryTags()).eq("status", status.ordinal).findRowCount()

    fun forMove(tag: Tag, submitter: User, parentId: Long): TagChangeRequest {
      val request = TagChangeRequest(tag, submitter, Type.MOVE)
      request.parentId = parentId
      return request
    }

    fun forRename(tag: Tag, submitter: User, name: String): TagChangeRequest {
      val request = TagChangeRequest(tag, submitter, Type.RENAME)
      request.name = name
      return request
    }

    fun forSetIntro(tag: Tag, submitter: User, intro: String): TagChangeRequest {
      val request = TagChangeRequest(tag, submitter, Type.SET_INTRO)
      request.intro = intro
      return request
    }
  }
}