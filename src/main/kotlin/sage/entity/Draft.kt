package sage.entity

import javax.persistence.*

@Entity
class Draft(
    var targetId: Long,
    var title: String,
    @Column(columnDefinition = "TEXT") @Lob
    var content: String,
    @ManyToOne
    val owner: User) : AutoModel() {
  companion object : Find<Long, Draft>()
}