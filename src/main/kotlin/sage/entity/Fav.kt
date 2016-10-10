package sage.entity

import javax.persistence.Entity
import javax.persistence.ManyToOne


@Entity
class Fav(
    var link: String,
    @ManyToOne
    var owner: User) : BaseModel() {
  companion object : Find<Long, Fav>() {
    fun ofOwner(ownerId: Long): List<Fav> = where().eq("owner", User.ref(ownerId)).findList()
  }
}