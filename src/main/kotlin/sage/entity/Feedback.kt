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
) : BaseModel() {
  companion object : Find<Long, Feedback>()
}