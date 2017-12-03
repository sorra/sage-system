package sage.entity

import javax.persistence.Column
import javax.persistence.Entity

interface ListEntity {
  var id: Long
  var ownerId: Long?
  var name: String?
}

@Entity
class FollowListEntity(
    override var ownerId: Long?,
    override var name: String?,
    @Column(columnDefinition = "TEXT")
    var listJson: String?) : AutoModel(), ListEntity {

  companion object : BaseFind<Long, FollowListEntity>(FollowListEntity::class) {

    fun byOwner(ownerId: Long): List<FollowListEntity> = where().eq("ownerId", ownerId).findList()
  }
}

@Entity
class ResourceListEntity(
    override var ownerId: Long?,
    override var name: String?,
    @Column(columnDefinition = "TEXT")
    var listJson: String?
) : AutoModel(), ListEntity {

  companion object : BaseFind<Long, ResourceListEntity>(ResourceListEntity::class) {

    fun byOwner(ownerId: Long): List<ResourceListEntity> = where().eq("ownerId", ownerId).findList()
  }
}