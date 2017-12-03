package sage.entity

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Feedback(
    @Column(columnDefinition = "TEXT")
    val content: String = "",
    val name: String = "",
    val email: String = "",
    val ip: String = ""
) : AutoModel() {

  companion object : BaseFind<Long, Feedback>(Feedback::class) {
    fun allDescending(): List<Feedback> = orderBy("id desc").findList()
  }
}