package sage.entity

import sage.domain.concept.Authority
import sage.transfer.*
import javax.persistence.Entity

@Entity
class User : BaseModel {

  var email: String = ""
  var password: String = ""
  var name: String = ""
  var avatar: String = ""
  var intro: String = ""
  var authority = Authority.USER

  constructor(email: String, password: String) {
    this.email = email
    this.password = password
  }

  constructor(email: String, password: String, name: String, intro: String, avatar: String) : this(email, password) {
    this.name = name
    this.avatar = avatar
    this.intro = intro
  }

  fun toUserCard(followerCount: Int, blogCount: Int, tweetCount: Int,
                 followFromCurrentUser: Follow?, followToCurrentUser: Follow?, tags: Collection<TagLabel>): UserCard {
    val u = UserCard()
    u.id = id
    u.name = name
    u.avatar = avatar
    u.intro = intro
    u.whenCreated = whenCreated

    u.followerCount = followerCount
    u.blogCount = blogCount
    u.tweetCount = tweetCount

    u.isFollowing = followFromCurrentUser != null
    u.isFollower = followToCurrentUser != null

    u.tags.addAll(tags)
    if (followFromCurrentUser != null) {
      u.follow = UserCardFollow(followFromCurrentUser)
    }
    return u
  }

  fun toUserSelf(followingCount: Int, followerCount: Int, blogCount: Int, tweetCount: Int, topTags: Collection<TagLabel>): UserSelf {
    val u = UserSelf()
    u.id = id
    u.name = name
    u.avatar = avatar
    u.intro = intro

    u.followingCount = followingCount
    u.followerCount = followerCount
    u.blogCount = blogCount
    u.tweetCount = tweetCount
    u.topTags.addAll(topTags)
    return u
  }

  fun toUserLabel(): UserLabel {
    val u = UserLabel()
    u.id = id
    u.name = name
    u.avatar = avatar
    u.intro = intro
    return u
  }

  companion object : Find<Long, User>() {
    fun get(id: Long) = getNonNull(User::class, id)

    fun byEmail(email: String) = where().eq("email", email).findUnique()
    fun byName(name: String) = where().eq("name", name).findUnique()
  }
}