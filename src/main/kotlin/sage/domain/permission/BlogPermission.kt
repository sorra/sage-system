package sage.domain.permission

import sage.entity.Blog
import sage.entity.User


class BlogPermission(override val userId: Long, override val target: Blog) : AbstractPermission() {
  override val targetId = target.id

  override fun checkResult(): Boolean {
    return userId == target.author.id || User.get(userId).authority.isSiteAdmin
  }
}