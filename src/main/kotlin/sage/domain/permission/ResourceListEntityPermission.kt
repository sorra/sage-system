package sage.domain.permission

import sage.entity.ResourceListEntity

class ResourceListEntityPermission(override val userId: Long, override val target: ResourceListEntity)
  : AbstractPermission() {
  override val targetId: Long = target.id

  override fun checkResult(): Boolean {
    return userId == target.ownerId
  }
}