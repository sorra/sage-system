package sage.domain.permission

import sage.domain.commons.PermissionDeniedException
import sage.entity.BaseModel

object CheckPermission {
  fun canEdit(userId: Long, target: BaseModel, checkResult: Boolean) {
    can(userId, target, checkResult, "edit")
  }

  fun canDelete(userId: Long, target: BaseModel, checkResult: Boolean) {
    can(userId, target, checkResult, "delete")
  }

  private fun can(userId: Long, target: BaseModel, checkResult: Boolean, action: String) {
    if (!checkResult) {
      throw PermissionDeniedException("User[$userId] is not allowed to $action ${target.javaClass.simpleName}[${target.id}]")
    }
  }
}