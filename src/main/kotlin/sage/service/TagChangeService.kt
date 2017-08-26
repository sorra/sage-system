package sage.service

import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sage.domain.commons.AuthorityException
import sage.domain.commons.DomainException
import sage.domain.permission.TagChangeRequestPermission
import sage.entity.Tag
import sage.entity.TagChangeRequest
import sage.entity.TagChangeRequest.Status
import sage.entity.TagChangeRequest.Type
import sage.entity.User
import sage.transfer.TagChangeRequestCard

@Service
class TagChangeService {

  fun requestMove(userId: Long, tagId: Long, parentId: Long): TagChangeRequest {
    if (Tag.byId(parentId) == null) {
      throw IllegalArgumentException("parentId $parentId is wrong!")
    }
    return saveRequest(TagChangeRequest.forMove(Tag.ref(tagId), User.ref(userId), parentId))
  }

  fun requestRename(userId: Long?, tagId: Long, name: String): TagChangeRequest {
    if (StringUtils.isBlank(name)) {
      throw IllegalArgumentException("name is empty!")
    }
    Tag.get(tagId).siblingsCannotHaveThatName(name)
    return saveRequest(TagChangeRequest.forRename(Tag.ref(tagId), User.ref(userId), name))
  }

  fun requestSetIntro(userId: Long, tagId: Long, intro: String): TagChangeRequest {
    if (StringUtils.isBlank(intro)) {
      throw IllegalArgumentException("intro is empty!")
    }
    return saveRequest(TagChangeRequest.forSetIntro(Tag.ref(tagId), User.ref(userId), intro))
  }

  private fun saveRequest(req: TagChangeRequest): TagChangeRequest {
    req.save()
    val submitter = req.submitter
    if (submitter.authority.canManageTags()) {
      // Admin权限者自动接受自己的修改
      acceptRequest(submitter.id, req.id)
    } else {
      //TODO 暂时以全站Admin身份自动接受所有修改
      acceptRequest(1L, req.id)
    }
    return req
  }

  fun getRequestsOfTag(tagId: Long): Collection<TagChangeRequestCard> {
    return TagChangeRequest.byTag(tagId).map(asTagChangeRequestCard)
  }

  fun countPendingRequestsOfTag(tagId: Long): Int {
    return TagChangeRequest.byTagAndStatus(tagId, Status.PENDING)
  }

  fun getRequestsOfTagScope(tagId: Long): Collection<TagChangeRequestCard> {
    return TagChangeRequest.countByTagScope(Tag.get(tagId)).map(asTagChangeRequestCard)
  }

  fun countPendingRequestsOfTagScope(tagId: Long): Int {
    return TagChangeRequest.countByTagScopeAndStatus(Tag.get(tagId), Status.PENDING)
  }

  private val asTagChangeRequestCard = {req: TagChangeRequest ->
    TagChangeRequestCard().apply {
      id = req.id
      tag = req.tag.toTagLabel()
      submitter = req.submitter.toUserLabel()
      if (req.transactor != null) {
        transactor = req.transactor?.toUserLabel()
      }
      statusKey = req.status.name
      status = req.status.desc
      type = req.type.desc
      when (req.type) {
        TagChangeRequest.Type.MOVE -> {
          val parent = Tag.get(req.parentId)
          desc = "移动到标签${parent.name}[${parent.id}]下"
        }
        TagChangeRequest.Type.RENAME -> desc = "改名为\"${req.name}\""
        TagChangeRequest.Type.SET_INTRO -> desc = "修改简介为\"${req.intro}\""
      }
    }
  }

  fun cancelRequest(userId: Long, reqId: Long) {
    val request = TagChangeRequest.byId(reqId)!!
    TagChangeRequestPermission(userId, request).canEdit()

    request.status = Status.CANCELED
    request.update()
  }

  fun acceptRequest(userId: Long, reqId: Long) = transactRequest(userId, reqId, Status.ACCEPTED)

  fun rejectRequest(userId: Long, reqId: Long) = transactRequest(userId, reqId, Status.REJECTED)

  fun userCanTransact(userId: Long) = User.get(userId).authority.canManageTags()

  private fun transactRequest(userId: Long, reqId: Long, status: Status): TagChangeRequest {
    val user = User.get(userId)
    if (user.authority.cannotManageTags()) {
      throw AuthorityException("You are not authorized to manage tags.")
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
        doTransact(tagId) { tag -> tag.parent = Tag.ref(req.parentId) }
      } else if (req.type === Type.RENAME) {
        doTransact(tagId) { tag -> tag.name = req.name }
      } else if (req.type === Type.SET_INTRO) {
        doTransact(tagId) { tag -> tag.intro = req.intro }
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
