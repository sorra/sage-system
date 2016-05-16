package sage.service

import org.springframework.stereotype.Service
import sage.domain.commons.DomainException
import sage.entity.Group
import sage.entity.Tag
import sage.entity.User
import sage.transfer.GroupPreview
import sage.transfer.UserLabel

@Service
class GroupService {

  fun getGroupPreview(id: Long): GroupPreview {
    return GroupPreview(Group.get(id))
  }

  fun members(groupId: Long): Collection<UserLabel> {
    return Group.get(groupId).members.map { UserLabel(it) }
  }

  fun create(userId: Long, name: String, introduction: String, tagIds: Collection<Long>): GroupPreview {
    if (name.isEmpty()) {
      throw DomainException("Must enter a group name!")
    }
    if (introduction.isEmpty()) {
      throw DomainException("Must enter a group introduction!")
    }

    val tags = Tag.multiGet(tagIds)
    val group = Group(name, introduction, tags, User.ref(userId))
    group.save()
    return GroupPreview(group)
  }

  fun edit(userId: Long, groupId: Long,
           name: String, introduction: String, tagIds: Collection<Long>): Group {
    if (name.isEmpty()) {
      throw DomainException("Must enter a group name!")
    }
    if (introduction.isEmpty()) {
      throw DomainException("Must enter a group introduction!")
    }
    val group = Group.get(groupId)
    if (userId != group.creator?.id) {
      throw DomainException("User[%d] is not the owner of Group[%d]", userId, groupId)
    }
    group.name = name
    group.introduction = introduction
    group.tags = Tag.multiGet(tagIds)
    group.update()
    return group
  }

  fun join(userId: Long, groupId: Long) {
    val user = User.ref(userId)
    val group = Group.get(groupId)
    if (group.members.add(user)) {
      group.update()
    }
  }

  fun exit(userId: Long, groupId: Long) {
    val user = User.ref(userId)
    val group = Group.get(groupId)
    if (group.members.remove(user)) {
      group.update()
    }
  }

  fun byTags(tagIds: Collection<Long>) =
      Group.where().filterMany("tags").`in`("id", tagIds).findList().map { GroupPreview(it) }

  fun all() = Group.all().map { GroupPreview(it) }
}
