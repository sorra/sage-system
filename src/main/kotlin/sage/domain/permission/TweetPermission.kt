package sage.domain.permission

import sage.entity.Tweet


class TweetPermission(override val userId: Long, override val target: Tweet) : AbstractPermission() {
  override val targetId: Long = target.id

  override fun checkResult(): Boolean {
    return userId == target.author.id
  }
}