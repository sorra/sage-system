package sage.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Lob

interface ListEntity {
  var id: Long
  var ownerId: Long?
  var name: String?
}

@Entity
class FollowListEntity(
    override var ownerId: Long?,
    override var name: String?,
    @Column(columnDefinition = "TEXT") @Lob
    var listJson: String?) : BaseModel(), ListEntity {

  companion object : Find<Long, FollowListEntity>() {
    fun get(id: Long) = getNonNull(FollowListEntity::class, id)

    fun byOwner(ownerId: Long) = where().eq("ownerId", ownerId).findList()
  }
}

@Entity
class ResourceListEntity(
    override var ownerId: Long?,
    override var name: String?,
    @Column(columnDefinition = "TEXT") @Lob
    var listJson: String?
) : BaseModel(), ListEntity {

  companion object : Find<Long, ResourceListEntity>() {
    fun get(id: Long) = getNonNull(ResourceListEntity::class, id)

    fun byOwner(ownerId: Long) = where().eq("ownerId", ownerId).findList()
  }
}