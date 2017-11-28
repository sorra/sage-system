package sage.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.permission.FavPermission
import sage.entity.Fav
import sage.entity.User
import sage.transfer.FavInfo
import java.util.*

@Service
class FavService
@Autowired constructor(private val tweetRead: TweetReadService) {

  fun favs(userId: Long): Collection<FavInfo> {
    val favs = ArrayList(Fav.ofOwner(userId))
    favs.sortByDescending { it.id }
    return FavInfo.listOf(favs, { tweetRead.getTweetView(it) })
  }

  fun create(userId: Long, link: String) {
    val fav = Fav(link, User.ref(userId))
    fav.save()
  }

  fun delete(userId: Long, favId: Long) {
    val fav = Fav.get(favId)

    FavPermission(userId, fav).canDelete()

    fav.delete()
  }
}
