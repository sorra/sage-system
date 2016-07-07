package sage.service

import org.jasypt.util.password.StrongPasswordEncryptor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sage.domain.commons.DomainException
import sage.domain.commons.IdCommons
import sage.domain.concept.Authority
import sage.entity.*
import sage.transfer.TagLabel
import sage.transfer.UserCard
import sage.transfer.UserLabel
import sage.transfer.UserSelf
import java.util.*

@Service
class UserService {
  private val log = LoggerFactory.getLogger(javaClass)

  fun getSelf(userId: Long): UserSelf {
    return UserSelf(User.get(userId),
        Follow.followingsCount(userId),
        Follow.followersCount(userId),
        Blog.where().eq("author", User.ref(userId)).findRowCount(),
        Tweet.where().eq("author", User.ref(userId)).findRowCount(),
        topTags(userId))
  }

  fun filterNewTags(userId: Long, existingTags: Collection<TagLabel>): Collection<TagLabel> {
    val existingTagIds = existingTags.map { it.id }
    return topTags(userId).filterNot { existingTagIds.contains(it.id) }
  }

  fun getUserCard(cuid: Long?, userId: Long): UserCard {
    return UserCard(User.get(userId),
        Follow.followersCount(userId),
        Blog.where().eq("author", User.ref(userId)).findRowCount(),
        Tweet.where().eq("author", User.ref(userId)).findRowCount(),
        cuid?.run { Follow.find(this, userId) },
        cuid?.run { Follow.find(userId, this) },
        userTags(userId))
  }

  fun getUserLabel(userId: Long): UserLabel {
    return UserLabel(User.get(userId))
  }

  fun changeInfo(userId: Long, name: String?, intro: String?, avatar: String?) {
    val user = User.get(userId)
    if (name == user.name && intro == user.intro && avatar == null) {
      return
    }
    name?.apply { user.name = this }
    intro?.apply { user.intro = this }
    avatar?.apply { user.avatar = avatar }
    user.update()
  }

  fun changeIntro(userId: Long, intro: String) {
    val user = User.get(userId)
    user.intro = intro
    user.update()
  }

  fun changeAvatar(userId: Long, avatar: String) {
    val user = User.get(userId)
    user.avatar = avatar
    user.update()
  }

  fun grantAuthority(userId: Long, authority: Authority) {
    val user = User.get(userId)
    user.authority = authority
    user.update()
  }

  fun login(email: String, password: String): User {
    val user = User.byEmail(email) ?: throw DomainException("User[email: %s] does not exist", email)
    if (checkPassword(password, user.password)) {
      return user
    } else {
      throw DomainException("Login failed, wrong user or password")
    }
  }

  fun register(user: User): Long {
    if (User.byEmail(user.email) != null) {
      throw DomainException("Email(%s) already registered", user.email)
    }
    user.password = encryptPassword(user.password)
    user.save()
    if(user.name.isEmpty()) user.name = "u" + user.id
    user.update()
    return user.id
  }

  fun updatePassword(userId: Long, oldPassword: String, newPassword: String): Boolean {
    val user = User.get(userId)
    if (checkPassword(oldPassword, user.password)) {
      user.password = encryptPassword(newPassword)
      user.update()
      return true
    } else {
      return false
    }
  }

  private fun checkPassword(plainPassword: String, encryptedPassword: String): Boolean {
    return StrongPasswordEncryptor().checkPassword(plainPassword, encryptedPassword)
  }

  private fun encryptPassword(plainPassword: String): String {
    return StrongPasswordEncryptor().encryptPassword(plainPassword)
  }

  fun updateUserTag(userId: Long, tagIds: Collection<Long>) {
    val usedTagIds = UserTag.byUser(userId).map { it.tagId }
    val newTagIds = tagIds - usedTagIds
    newTagIds.forEach { tagId ->
      try {
        UserTag(userId, tagId).save()
      } catch (e: Exception) {
        log.error("UserTag[userId=$userId, tagId=$tagId] saving failed!", e)
      }
    }
  }

  fun people(selfId: Long): Collection<UserCard> {
    return User.all().filter { it.id != selfId }.map { getUserCard(selfId, it.id) }
  }

  fun recommendByTag(selfId: Long): Collection<UserCard> {
    return recommend(selfId).map { getUserCard(selfId, it.id) }
  }

  private fun recommend(userId: Long): List<PersonValue> {
    val list = ArrayList<PersonValue>()
    val selfTags = topTags(userId)
    var i: Long = 1
    while (true) {
      val person = User.byId(i) ?: break
      if (IdCommons.equal(person.id, userId)) {
        i++
        continue
      }
      var value = 0
      var j = selfTags.size
      for (st in selfTags) {
        val personTags = topTags(i)
        var k = personTags.size
        for (pt in personTags) {
          if (IdCommons.equal(st.id, pt.id)) {
            value += j * k
          }
          k--
        }
        j--
      }
      if (value > 0) {
        list.add(PersonValue(i, value))
      }
      i++
    }
    Collections.sort(list)
    return list
  }

  private fun userTags(userId: Long): List<TagLabel> {
    val topping = ArrayList<TagCounter>()
    for (tweet in Tweet.byAuthor(userId)) {
      countTags(tweet.tags, topping)
    }
    for (blog in Blog.byAuthor(userId)) {
      countTags(blog.tags, topping)
    }
    topping.sort()

    val topTags = ArrayList<TagLabel>()
    for (topOne in topping) {
      topTags.add(TagLabel(topOne.tag))
    }
    return topTags
  }

  fun topTags(userId: Long) = userTags(userId).take(5)

  private fun countTags(tags: Collection<Tag>, topping: MutableList<TagCounter>) {
    for (tag in tags) {
      if (tag.id.equals(Tag.ROOT_ID)) {
        continue
      }
      val counter = TagCounter(tag)
      if (topping.contains(counter)) {
        topping[topping.indexOf(counter)].count++
      } else {
        topping.add(counter)
      }
    }
  }

  private class TagCounter constructor(val tag: Tag) : Comparable<TagCounter> {
    var count: Int = 1

    override fun compareTo(other: TagCounter): Int {
      return -(count - other.count)
    }

    override fun equals(other: Any?): Boolean {
      if (other is TagCounter == false) {
        return false
      }
      return IdCommons.equal(tag.id, (other as TagCounter).tag.id)
    }

    override fun hashCode(): Int {
      return tag.hashCode()
    }
  }

  internal class PersonValue(var id: Long, var value: Int) : Comparable<PersonValue> {

    override fun compareTo(other: PersonValue): Int {
      return -(value - other.value)
    }
  }
}
