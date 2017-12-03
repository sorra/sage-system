package sage.entity

import javax.persistence.Entity
import javax.persistence.ManyToOne


@Entity
class Fav(
    var link: String,
    @ManyToOne
    var owner: User) : AutoModel() {
  companion object : BaseFind<Long, Fav>(Fav::class) {

    fun ofOwner(ownerId: Long): List<Fav> = where().eq("owner", User.ref(ownerId)).findList()
  }
}