package sage.service

import org.springframework.stereotype.Service
import sage.entity.*

@Service
class HeedService {

  fun tagHeeds(userId: Long): Collection<TagHeed> {
    return TagHeed.byUser(userId)
  }

  fun heedTag(userId: Long, tagId: Long) {
    if (TagHeed.find(userId, tagId) == null) {
      TagHeed(User.ref(userId), Tag.ref(tagId)).save()
    }
  }

  fun unheedTag(userId: Long, tagId: Long) {
    TagHeed.find(userId, tagId)?.delete()
  }

  fun followListHeeds(userId: Long): Collection<FollowListHeed> {
    return FollowListHeed.byUser(userId)
  }

  fun existsFollowListHeed(userId: Long, followListId: Long): Boolean {
    return FollowListHeed.find(userId, followListId) != null
  }

  fun heedFollowList(userId: Long, followListId: Long) {
    if (FollowListHeed.find(userId, followListId) == null) {
      FollowListHeed(User.ref(userId), FollowListEntity.ref(followListId)).save()
    }
  }

  fun unheedFollowList(userId: Long, followListId: Long) {
    FollowListHeed.find(userId, followListId)?.delete()
  }
}
