package sage.transfer

import sage.annotation.KotlinNoArg
import sage.entity.Follow

@KotlinNoArg
class FollowInfo {
  var user: UserLabel? = null
  var tags: Collection<TagLabel> = emptyList()

  constructor(user: UserLabel, tags: Collection<TagLabel>) {
    this.user = user
    this.tags = tags
  }

  constructor(follow: Follow) {
    user = UserLabel(follow.target)
    tags = follow.tags.map { TagLabel(it) }
  }
}
