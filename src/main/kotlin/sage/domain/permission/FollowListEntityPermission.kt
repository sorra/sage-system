package sage.domain.permission

import sage.entity.FollowListEntity

class FollowListEntityPermission(override val userId: Long, override val target: FollowListEntity)
  : AbstractPermission() {
  override val targetId: Long = target.id

  override fun checkResult(): Boolean {
    return userId == target.ownerId
  }
}