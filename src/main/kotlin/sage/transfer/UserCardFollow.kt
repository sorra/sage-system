package sage.transfer

import sage.annotation.KotlinNoArg
import sage.entity.Follow

@KotlinNoArg
class UserCardFollow {
  var reason: String? = null
  var tagIds: Collection<Long> = emptyList()
  var includeNew: Boolean = false
  var includeAll: Boolean = false

  constructor(follow: Follow) {
    reason = follow.reason
    tagIds = follow.tags.map { it.id }
    includeNew = follow.isIncludeNew
    includeAll = follow.isIncludeAll
  }
}
