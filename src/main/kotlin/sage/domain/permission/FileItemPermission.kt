package sage.domain.permission

import sage.entity.FileItem


class FileItemPermission(override val userId: Long, override val target: FileItem) : AbstractPermission() {
  override val targetId: Long = target.id

  override fun checkResult(): Boolean {
    return userId == target.ownerId
  }
}