package sage.transfer

import sage.entity.Follow

class FollowInfo {
  var user: UserLabel? = null
  var tags: Collection<TagLabel> = emptyList()

  internal constructor() {
  }

  constructor(user: UserLabel, tags: Collection<TagLabel>) {
    this.user = user
    this.tags = tags
  }

  constructor(follow: Follow) {
    user = UserLabel(follow.target)
    tags = follow.tags.map { TagLabel(it) }
  }
}
