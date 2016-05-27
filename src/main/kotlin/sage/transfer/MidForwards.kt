package sage.transfer

import java.util.LinkedList

import sage.entity.Tweet
import sage.web.context.Json

class MidForwards {
  var xs: MutableList<MidForward> = LinkedList()

  internal constructor() {
  }

  constructor(directForward: Tweet) {
    addForward(directForward)
    val formerMidForwards = directForward.midForwards()
    if (formerMidForwards != null) { xs.addAll(formerMidForwards.xs) }
  }

  private fun addForward(forward: Tweet): MidForwards {
    val t = forward
    val asString = "@${t.author.name}#${t.author.id} : ${t.content}"
    xs.add(MidForward(t.id, asString))
    return this
  }

  fun removeById(idToRemove: Long): MidForwards {
    val idx = xs.indexOfFirst { it.id == idToRemove }
    if (idx >= 0) { xs.removeAt(idx) }
    return this
  }

  fun toJson(): String {
    return Json.json(this)
  }

  companion object {

    fun from(tweet: Tweet): MidForwards {
      return Json.`object`(tweet.midForwardsJson!!, MidForwards::class.java)
    }

    fun fromJson(json: String): MidForwards {
      return Json.`object`(json, MidForwards::class.java)
    }
  }
}