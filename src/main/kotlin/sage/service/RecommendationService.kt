package sage.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.IdCommons
import sage.entity.User
import sage.transfer.UserCard

@Service
class RecommendationService @Autowired constructor(
    private val userService: UserService,
    private val relationService: RelationService
) {

  fun recommendUsers(userId: Long): List<UserCard> {
    val recomms = recommendPeopleByTag(userId).map { it.id }
    val followingUsers = relationService.followings(userId).mapTo(hashSetOf()) { it.target.id }

    return (recomms - followingUsers).map {
      userService.getUserCard(userId, it)
    }
  }

  private fun recommendPeopleByTag(userId: Long): List<PersonValue> {
    val resultList = mutableListOf<PersonValue>()
    val selfTags = userService.topTags(userId)

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
        val personTags = userService.topTags(i)
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
        resultList.add(PersonValue(i, value))
      }

      i++
    }

    resultList.sort()
    return resultList
  }

  private class PersonValue(var id: Long, var value: Int) : Comparable<PersonValue> {

    override fun compareTo(other: PersonValue): Int {
      return -(value - other.value)
    }
  }
}