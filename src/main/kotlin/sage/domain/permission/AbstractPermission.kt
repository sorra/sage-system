package sage.domain.permission

import sage.domain.commons.PermissionDeniedException

abstract class AbstractPermission {
  abstract val userId: Long
  abstract val target: Any
  abstract val targetId: Long

  protected abstract fun checkResult(): Boolean

  fun canEdit() {
    can(checkResult(), "edit")
  }

  fun canDelete() {
    can(checkResult(), "delete")
  }

  private fun can(checkResult: Boolean, action: String) {
    if (!checkResult) {
      throw PermissionDeniedException("User[$userId] is not permitted to $action ${target.javaClass.simpleName}[$targetId]")
    }
  }
}