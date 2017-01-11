package sage.entity

import javax.persistence.*

@Entity
class Draft(
    var targetId: Long,
    var title: String,
    @Column(columnDefinition = "TEXT")
    var content: String,
    @ManyToOne
    val owner: User) : BaseModel() {
  companion object : Find<Long, Draft>()
}