package sage.service

import org.springframework.stereotype.Service
import sage.entity.Tag
import sage.entity.TagHeed
import sage.entity.User

@Service
class HeedService {

  fun tagHeeds(userId: Long): Collection<TagHeed> {
    return TagHeed.byUser(userId)
  }

  fun existsTagHeed(userId: Long, tagId: Long): Boolean {
    return TagHeed.find(userId, tagId) != null
  }

  fun heedTag(userId: Long, tagId: Long) {
    if (TagHeed.find(userId, tagId) == null) {
      TagHeed(User.ref(userId), Tag.ref(tagId)).save()
    }
  }

  fun unheedTag(userId: Long, tagId: Long) {
    TagHeed.find(userId, tagId)?.delete()
  }
}
