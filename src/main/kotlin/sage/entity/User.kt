package sage.entity

import sage.domain.concept.Authority
import javax.persistence.Entity

@Entity
class User : BaseModel {

  var email: String = ""
  var name: String = ""
  var password: String = ""
  var intro: String = ""
  var avatar: String = ""
  var authority = Authority.USER

  constructor(email: String, password: String) {
    this.email = email
    this.password = password
  }

  constructor(email: String, password: String, name: String, intro: String, avatar: String) : this(email, password) {
    this.name = name
    this.intro = intro
    this.avatar = avatar
  }

  companion object : Find<Long, User>() {
    fun get(id: Long) = getNonNull(User::class, id)

    fun byEmail(email: String) = where().eq("email", email).findUnique()
    fun byName(name: String) = where().eq("name", name).findUnique()
  }
}