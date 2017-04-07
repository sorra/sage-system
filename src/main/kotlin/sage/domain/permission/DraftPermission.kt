package sage.domain.permission

import sage.entity.Draft


class DraftPermission(override val userId: Long, override val target: Draft) : AbstractPermission() {
  override val targetId: Long = target.id

  override fun checkResult(): Boolean {
    return userId == target.owner.id
  }
}