package sage.service

import httl.util.StringUtils
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import sage.entity.FollowListEntity
import sage.entity.ResourceListEntity
import sage.entity.Tag
import sage.entity.User
import sage.transfer.*
import sage.util.Strings
import java.util.*

@Service
class ListService {

  fun getResourceList(id: Long): ResourceList {
    return ResourceList.fromEntity(ResourceListEntity.get(id))
  }

  fun addResourceList(rc: ResourceList, userId: Long): Long? {
    val entity = escaped(rc).toEntity()
    entity.ownerId = userId
    entity.save()
    return entity.id
  }

  fun updateResourceList(rc: ResourceList, userId: Long) {
    val entity = ResourceListEntity.get(rc.id)
    Assert.isTrue(entity.ownerId == userId)

    val neo = escaped(rc).toEntity()
    entity.name = neo.name
    entity.listJson = neo.listJson
    entity.update()
  }

  fun getFollowList(id: Long): FollowList {
    return FollowListLite.fromEntity(FollowListEntity.get(id)).toFull({UserLabel(User.get(it)) }) { TagLabel(Tag.get(it)) }
  }

  fun addFollowList(fcLite: FollowListLite, userId: Long): Long {
    val entity = fcLite.toEntity()
    entity.ownerId = userId
    entity.save()
    return entity.id
  }

  fun updateFollowList(fcLite: FollowListLite, userId: Long) {
    val entity = FollowListEntity.get(fcLite.id)
    Assert.isTrue(entity.ownerId == userId)

    val neo = fcLite.toEntity()
    entity.name = neo.name
    entity.listJson = neo.listJson
    entity.update()
  }

  private fun escaped(rc: ResourceList): ResourceList {
    val neo = ResourceList(rc.id, rc.ownerId, rc.name, ArrayList<ResourceInfo>())
    rc.list.forEach { info ->
      neo.list.add(ResourceInfo(Strings.escapeHtmlTag(info.link), Strings.escapeHtmlTag(info.desc)))
    }
    return neo
  }

  fun followListsOfOwner(ownerId: Long): List<FollowList> {
    return FollowListEntity.byOwner(ownerId).map { l ->
      FollowListLite.fromEntity(l).toFull({ UserLabel(User.get(it)) }, { TagLabel(Tag.get(it)) })
    }
  }

  fun resourceListsOfOwner(ownerId: Long): List<ResourceList> {
    return ResourceListEntity.byOwner(ownerId).map { l -> ResourceList.fromEntity(l) }
  }
}
