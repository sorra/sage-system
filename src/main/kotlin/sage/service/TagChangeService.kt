package sage.service

import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sage.domain.commons.AuthorityException
import sage.domain.commons.DomainException
import sage.domain.concept.Authority
import sage.entity.Tag
import sage.entity.TagChangeRequest
import sage.entity.TagChangeRequest.Status
import sage.entity.TagChangeRequest.Type
import sage.entity.User
import sage.transfer.TagChangeRequestCard

@Service
class TagChangeService {

  fun requestMove(userId: Long?, tagId: Long?, parentId: Long): TagChangeRequest {
    if (Tag.byId(parentId) == null) {
      throw IllegalArgumentException("parentId $parentId is wrong!")
    }
    return saveRequest(TagChangeRequest.forMove(Tag.ref(tagId), User.ref(userId), parentId))
  }

  fun requestRename(userId: Long?, tagId: Long?, name: String): TagChangeRequest {
    if (StringUtils.isBlank(name)) {
      throw IllegalArgumentException("name is empty!")
    }
    return saveRequest(TagChangeRequest.forRename(Tag.ref(tagId), User.ref(userId), name))
  }

  fun requestSetIntro(userId: Long?, tagId: Long?, intro: String): TagChangeRequest {
    if (StringUtils.isBlank(intro)) {
      throw IllegalArgumentException("intro is empty!")
    }
    return saveRequest(TagChangeRequest.forSetIntro(Tag.ref(tagId), User.ref(userId), intro))
  }

  private fun saveRequest(req: TagChangeRequest): TagChangeRequest {
    req.save()
    val submitter = req.submitter
    if (Authority.isTagAdminOrHigher(submitter.authority)) {
      // Admin权限者自动接受自己的修改
      acceptRequest(submitter.id, req.id)
    } else {
      //TODO 暂时以全站Admin身份自动接受所有修改
      acceptRequest(1L, req.id)
    }
    return req
  }

  fun getRequestsOfTag(tagId: Long): Collection<TagChangeRequestCard> {
    return TagChangeRequest.byTag(tagId).map { TagChangeRequestCard(it) }
  }

  fun countPendingRequestsOfTag(tagId: Long): Int {
    return TagChangeRequest.byTagAndStatus(tagId, Status.PENDING).size
  }

  fun getRequestsOfTagScope(tagId: Long): Collection<TagChangeRequestCard> {
    return TagChangeRequest.byTagScope(Tag.get(tagId)).map { TagChangeRequestCard(it) }
  }

  fun countPendingRequestsOfTagScope(tagId: Long): Int {
    return TagChangeRequest.byTagScopeAndStatus(Tag.get(tagId), Status.PENDING).size
  }

  fun cancelRequest(userId: Long, reqId: Long) {
    val request = TagChangeRequest.byId(reqId)!!
    if (userId != request.submitter.id) {
      throw DomainException("User[%d] is not the owner of TagChangeRequest[%d]", userId, reqId)
    }
    request.status = Status.CANCELED
  }

  fun acceptRequest(userId: Long, reqId: Long) = transactRequest(userId, reqId, Status.ACCEPTED)

  fun rejectRequest(userId: Long, reqId: Long) = transactRequest(userId, reqId, Status.REJECTED)

  fun userCanTransact(userId: Long) = Authority.isTagAdminOrHigher(User.get(userId).authority)

  private fun transactRequest(userId: Long, reqId: Long, status: Status): TagChangeRequest {
    val user = User.get(userId)
    if (!Authority.isTagAdminOrHigher(user.authority)) {
      throw AuthorityException("Require TagAdmin or higher.")
    }
    val req = TagChangeRequest.byId(reqId)!!
    if (req.status !== Status.PENDING) {
      throw DomainException("Don't repeat TagChangeService.transactRequest on obsolete request.")
    }
    req.status = status
    req.transactor = user
    req.update()

    if (status === Status.ACCEPTED) {
      val tagId = req.tag.id
      if (req.type === Type.MOVE) {
        doTransact(tagId, { tag -> tag.parent = Tag.ref(req.parentId) })
      } else if (req.type === Type.RENAME) {
        doTransact(tagId, { tag -> tag.name = req.name })
      } else if (req.type === Type.SET_INTRO) {
        doTransact(tagId, { tag -> tag.intro = req.intro })
      }
      log.info("transactRequest done: {}", req)
    }
    return req
  }

  private fun doTransact(tagId: Long, action: (Tag) -> Unit) {
    val tag = Tag.byId(tagId)!!
    action.invoke(tag)
    tag.update()
  }

  companion object {
    private val log = LoggerFactory.getLogger(TagChangeService::class.java)
  }
}
