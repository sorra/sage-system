package sage.domain.permission

import sage.entity.TagChangeRequest

class TagChangeRequestPermission(override val userId: Long, override val target: TagChangeRequest) : AbstractPermission() {
  override val targetId: Long = target.id

  override fun checkResult(): Boolean {
    return userId == target.submitter.id
  }
}