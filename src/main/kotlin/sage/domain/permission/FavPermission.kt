package sage.domain.permission

import sage.entity.Fav

class FavPermission(override val userId: Long, override val target: Fav) : AbstractPermission() {
  override val targetId: Long = target.id

  override fun checkResult(): Boolean {
    return userId == target.owner.id
  }
}